package keepie.config

import mu.KLogging

data class SecretsAccessConfig(val servicesToSecrets: Map<String, List<String>>) {
    
    companion object : KLogging()

    fun print() {
        logger.info { "Services access:" }
        servicesToSecrets.forEach { (serviceName, secretNames) -> logger.info { " - ${serviceName}: $secretNames" } }
    }
}