package info.ls.grpc.v1

class HelloServiceServer : GreeterGrpcKt.GreeterCoroutineImplBase() {
    override suspend fun sayHello(request: SayHelloRequest) = sayHelloResponse {
        message = "Hello ${request.name}"
    }

    override suspend fun sayHelloAgain(request: SayHelloRequest) = sayHelloResponse {
        message = "Hello ${request.name} again"
    }
}
