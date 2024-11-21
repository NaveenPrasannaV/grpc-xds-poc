package org.example;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;

/**
 * Implementation of the Greeter service that includes hostname in the response.
 */
public class HostnameGreeter extends GreeterGrpc.GreeterImplBase {
  private final String hostname;

  public HostnameGreeter(String hostname) {
    if (hostname == null || hostname.isEmpty()) {
      this.hostname = getDefaultHostname();
    } else {
      this.hostname = hostname;
    }
  }

  @Override
  public void sayHello(HelloRequest req, io.grpc.stub.StreamObserver<HelloReply> responseObserver) {
    String message = "Hello, " + req.getName() + " from " + hostname;
    HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }

  private String getDefaultHostname() {
    try {
      return java.net.InetAddress.getLocalHost().getHostName();
    } catch (Exception e) {
      return "unknown-host";
    }
  }
}
