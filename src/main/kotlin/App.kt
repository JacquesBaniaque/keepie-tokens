import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
fun main(args: Array<String>) {
    val server = Server()
    server.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop()
        logger.info("Server stopped")
    })
}