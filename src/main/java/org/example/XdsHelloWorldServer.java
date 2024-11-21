package org.example;

import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerCredentials;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.xds.XdsServerBuilder;
import io.grpc.xds.XdsServerCredentials;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class XdsHelloWorldServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    int port = 50051;
    ServerCredentials credentials = XdsServerCredentials.create(InsecureServerCredentials.create());

    Server server = XdsServerBuilder.forPort(port, credentials)
        .addService(new GreeterImpl())
        .build()
        .start();

    System.out.println("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }));
    server.awaitTermination();
  }

  static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request,
        io.grpc.stub.StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
