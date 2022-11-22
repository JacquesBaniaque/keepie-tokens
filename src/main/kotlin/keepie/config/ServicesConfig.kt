package keepie.config

import mu.KLogging

data class ServicesConfig(val services: List<ServiceItem>) {

    companion object : KLogging()

    fun print() {
        logger.info { "Services:" }
        services.forEach { logger.info { " ${it.name} | ${it.path} | ${it.receivers}" } }
    }
}
