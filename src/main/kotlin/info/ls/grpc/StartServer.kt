package info.ls.grpc

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val server = HelloServer(port)
    server.start()
    server.blockUntilShutdown()
}
