package keepie.config

import mu.KLogging

data class SecretsConfig(val secrets: List<SecretItem>) {

    companion object : KLogging()

    fun print() {
        logger.info { "Secrets:" }
        secrets.forEach { logger.info { " - ${it.name} | ${it.serviceRef} | ${it.generator}" } }
    }

    fun normalize() = SecretsConfig(secrets = secrets.map { it.copy(name = it.name ?: it.serviceRef)  })
}
