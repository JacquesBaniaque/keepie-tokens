package demo.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.SignatureException
import keepie.Secret
import mu.KLogging
import okhttp3.OkHttpClient
import utils.ignoreAllSSLErrors
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path


/**
 * This API emulates secret receivers.
 * Normally these would be provided on services that want to receive the secrets.
 */
@Path("/keepie-receiver/service-2")
class DemoKeyPairReceiverEndpoint(private val keyReceiver: (KeyPair) -> Unit) {
    companion object : KLogging()

    private val httpClient: OkHttpClient = OkHttpClient.Builder().ignoreAllSSLErrors().build()

    data class KeyPairSecret(val publicKey: String, val privateKey: String)

    @POST
    @Consumes("application/json")
    fun receiveSecretService1(secret: Secret) {
        try {
            val keyPairSecret = ObjectMapper().registerKotlinModule().readValue(secret.value, KeyPairSecret::class.java)
            val publicKey = toPublicKey(keyPairSecret.publicKey)
            val privateKey = toPrivateKey(keyPairSecret.privateKey)
            keyReceiver.invoke(KeyPair(publicKey, privateKey))
//            httpClient.newCall(
//                Request.Builder()
//                    .get()
//                    .url("https://localhost:8080/keepie/public-keys")
//                    .build()
//            )
//                .execute().use { response ->
//                    {
//                        val encodedPublicKeyBytes = Decoders.BASE64.decode(ObjectMapper().readTree(response.body?.string()).get("jwt-auth-generator").textValue())
//                        val keyFactory = KeyFactory.getInstance(SignatureAlgorithm.RS256.value)
//                        val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(encodedPublicKeyBytes))
//                        val claims = Jwts.parserBuilder()
//                            .setSigningKey(publicKey)
//                            .build()
//                            .parseClaimsJws(secret.value)
//                        logger.info("Received secret for service-1 '${secret.name}' with verified claims: $claims (key=${secret.value})")
//                    }
//                }
        } catch (ex: SignatureException) {
            logger.error("Failed to verify JWT", ex)
        }
    }

    private fun toPrivateKey(key64: String): PrivateKey {
        val key: ByteArray = Base64.getDecoder().decode(key64.toByteArray())
        val keySpec = PKCS8EncodedKeySpec(key)
        val fact = KeyFactory.getInstance(SignatureAlgorithm.RS256.familyName)
        return fact.generatePrivate(keySpec)
    }

    private fun toPublicKey(key64: String): PublicKey {
        val data: ByteArray = Base64.getDecoder().decode(key64)
        val spec = X509EncodedKeySpec(data)
        val fact = KeyFactory.getInstance(SignatureAlgorithm.RS256.familyName)
        return fact.generatePublic(spec)
    }

}