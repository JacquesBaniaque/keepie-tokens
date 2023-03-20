package keepie

import com.fasterxml.jackson.databind.ObjectMapper
import keepie.config.SecretItem
import keepie.config.ServiceItem
import keepie.generators.KeepieGenerator
import mu.KLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import utils.ignoreAllSSLErrors
import java.util.concurrent.TimeUnit

class KeepieService(
    services: List<ServiceItem>,
    secrets: List<SecretItem>,
    servicesToSecrets: Map<String, List<String>>,
    val generators: Map<String, KeepieGenerator>
) {

    companion object : KLogging()

    private val httpClient = OkHttpClient.Builder()
        .ignoreAllSSLErrors()
        .callTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(2, TimeUnit.SECONDS)
        .build()
    private val mapper = ObjectMapper()


    private val secretsToServices = servicesToSecrets
        .flatMap { (service, serviceSecrets) ->
            serviceSecrets.mapNotNull { serviceSecret -> secrets.find { it.name == serviceSecret } }
                .map { secret -> secret to service }
        }
        .flatMap { (secret, service) -> // create unique secret if its service to
            if (secret.serviceRef == null)
                listOf(secret to service)
            else {
                listOf(
                    secret.copy(name = "$service-to-${secret.serviceRef}") to service,
                    secret.copy(name = "$service-to-${secret.serviceRef}") to secret.serviceRef
                )
            }
        }
        .groupBy({ (secret, _) -> secret }, { (_, service) -> service })
        .also { secretsToServices ->
            logger.info("Remapped secrets to services:")
            secretsToServices.forEach { (secret, services) -> logger.info("${secret.name} to ${services}}") }
        }

    private val serviceToUrls = services.associateBy { it.name }
        .also { logger.info("Service to urls: $it") }

    fun sendSecret(secretName: String, replyToUrl: String) {
        val secretItem = secretsToServices.keys.find { it.name == secretName }
            .also { if (it == null) logger.warn { "Unknown secret: $secretName" } } ?: return
        val generator = generators[secretItem.generator]
            .also { if (it == null) logger.warn { "Unknown generator: ${secretItem.generator}" } } ?: return
        val serviceNames = secretsToServices[secretItem]
        if (serviceNames == null) {
            logger.warn { "Refused to send '$secretName' to $replyToUrl. This secret is not mapped to any service." }
            return
        }
        val serviceItem = serviceNames
            .mapNotNull { serviceName -> serviceToUrls[serviceName] }
            .firstOrNull { serviceItem ->
                serviceItem.receiversUrls().stream().anyMatch { url -> replyToUrl.startsWith(url) }
            }
        if (serviceItem == null) {
            logger.warn { "Refused to send '$secretName' to $replyToUrl. Didn't find this url among services configured for this secret: $serviceNames." }
            return
        }
        try {
            val secret = generator.getSecretValue(secretItem, serviceItem)
            val request = Request.Builder()
                .post(mapper.writeValueAsString(secret).toRequestBody())
                .header("Content-Type", "application/json")
                .url(replyToUrl)
                .build()
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    logger.info("Successfully sent '$secretName' to '${serviceItem.name}' via $replyToUrl")
                } else {
                    logger.warn { "Failed to send '$secretName' to '${serviceItem.name}' via $replyToUrl (${response.code})" }
                }
            }
        } catch (ex: Exception) {
            logger.warn { "Failed to send '$secretName' to '${serviceItem.name}' via $replyToUrl due to an exception ${ex.javaClass.simpleName}/${ex.message}" }
        }
    }

}