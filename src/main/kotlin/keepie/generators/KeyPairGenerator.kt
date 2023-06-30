package keepie.generators

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import keepie.Secret
import keepie.config.SecretItem
import keepie.config.ServiceItem
import java.util.*

class KeyPairGenerator : KeepieGenerator {

    private val mapper = ObjectMapper()
    override fun getName(): String = "key-pair-generator"

    override fun getSecretValue(secretItem: SecretItem, serviceItem: ServiceItem): Secret {
        val keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256)
        return Secret(
            secretItem.name,
            mapper.writeValueAsString(KeyPairSecret(
                Base64.getEncoder().encodeToString(keyPair.public.encoded),
                Base64.getEncoder().encodeToString(keyPair.private.encoded)
        )))
    }

    data class KeyPairSecret(val publicKey: String, val privateKey: String)
}