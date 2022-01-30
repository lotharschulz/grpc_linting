package info.ls.grpc

import info.ls.grpc.v1.HelloServiceServer
import io.grpc.Server
import io.grpc.ServerBuilder

class HelloServer(val port: Int) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(HelloServiceServer())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}
