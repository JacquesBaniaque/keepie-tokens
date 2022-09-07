package keepie.config

data class ServiceItem(
    val name: String,
    val targets: List<String>,
    val path: String
)