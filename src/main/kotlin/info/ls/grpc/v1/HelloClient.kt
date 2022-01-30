package info.ls.grpc.v1

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import info.ls.grpc.v1.GreeterGrpcKt.GreeterCoroutineStub
import java.io.Closeable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking

class HelloClient(val channel: ManagedChannel) : Closeable {
    private val stub: GreeterCoroutineStub = GreeterCoroutineStub(channel)

    fun greet(s: String) = runBlocking {
        val request = sayHelloRequest { name = s }
        try {
            val response = stub.sayHello(request)
            println("Received response: ${response.message}")
            val againResponse = stub.sayHelloAgain(request)
            println("Received response again: ${againResponse.message}")
        } catch (e: StatusException) {
            println("RPC failed: ${e.status}")
        }
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

fun main(args: Array<String>) {
    val isRemote = args.size == 1

    Executors.newFixedThreadPool(10).asCoroutineDispatcher().use { dispatcher ->
        val builder = if (isRemote) {
            ManagedChannelBuilder.forTarget(args[0].removePrefix("https://") + ":443").useTransportSecurity()
        }
        else {
            val port = System.getenv("PORT")?.toInt() ?: 50051
            ManagedChannelBuilder.forTarget("localhost:$port").usePlaintext()
        }

        HelloClient(
            builder.executor(dispatcher.asExecutor()).build()
        ).use {
            val user = args.singleOrNull() ?: "world"
            it.greet(user)
        }
    }
}
