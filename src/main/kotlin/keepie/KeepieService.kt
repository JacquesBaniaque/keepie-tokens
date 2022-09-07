package keepie

import com.fasterxml.jackson.databind.ObjectMapper
import keepie.config.ServiceItem
import mu.KLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.commons.text.RandomStringGenerator
import utils.ignoreAllSSLErrors
import java.util.*
import java.util.concurrent.TimeUnit

class KeepieService(services: List<ServiceItem>) {

    companion object : KLogging()

    private val httpClient = OkHttpClient.Builder()
        .ignoreAllSSLErrors()
        .callTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(2, TimeUnit.SECONDS)
        .build()
    private val mapper = ObjectMapper()
    private val generator = RandomStringGenerator.Builder()
        .withinRange(
            charArrayOf('a', 'z'),
            charArrayOf('A', 'Z'),
            charArrayOf('0', '9')
        )
        .build()

    private val secretsToServices = mapOf("my-secret" to listOf("service-1"))
    private val serviceToUrls = services.map{it.name to it}.toMap()

    fun sendSecret(secretName: String, replyToUrl: String) {
        val serviceNames = secretsToServices[secretName] ?: return
        val service = serviceNames
            .mapNotNull { serviceName -> serviceToUrls[serviceName] }
            .firstOrNull { serviceItem -> serviceItem.targets.stream().anyMatch { url -> replyToUrl.startsWith(url) } }
        if (service != null) {
            try {
                val secretValue = generator.generate(64)
                val request = Request.Builder()
                    .post(mapper.writeValueAsString(Secret(secretName, secretValue)).toRequestBody())
                    .header("Content-Type", "application/json")
                    .url(replyToUrl)
                    .build()
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        logger.info("Successfully sent'$secretName' to '${service.name}' via $replyToUrl")
                    } else {
                        logger.warn { "Failed to send '$secretName' to '${service.name}' via $replyToUrl (${response.code})" }
                    }
                }
            } catch (ex: Exception) {
                logger.warn { "Failed to send '$secretName' to '${service.name}' via $replyToUrl due to an exception ${ex.javaClass.simpleName}/${ex.message}" }
            }
        } else {
            logger.warn { "Refused to send '$secretName' to $replyToUrl. Didn't find this url among services configured for this secret: $serviceNames." }
        }
    }

}