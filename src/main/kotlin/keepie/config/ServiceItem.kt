package keepie.config

data class ServiceItem(
    val name: String,
    val receivers: List<String>,
    val path: String
) {
    fun receiversUrls() = receivers.map { it + path }
}