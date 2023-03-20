package keepie.generators

import keepie.Secret
import keepie.config.SecretItem
import keepie.config.ServiceItem
import org.apache.commons.text.RandomStringGenerator

class RandomStringGenerator : KeepieGenerator {

    private val generator = RandomStringGenerator.Builder()
        .withinRange(
            charArrayOf('a', 'z'),
            charArrayOf('A', 'Z'),
            charArrayOf('0', '9')
        )
        .build()

    override fun getName() = "random-string"

    override fun getSecretValue(secretItem: SecretItem, serviceItem: ServiceItem) =
        Secret(secretItem.name, generator.generate(64))

}