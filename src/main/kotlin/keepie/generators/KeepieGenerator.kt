package keepie.generators

import keepie.Secret
import keepie.config.SecretItem
import keepie.config.ServiceItem

interface KeepieGenerator {
    fun getName(): String
    fun getSecretValue(secretItem: SecretItem, serviceItem: ServiceItem): Secret
}