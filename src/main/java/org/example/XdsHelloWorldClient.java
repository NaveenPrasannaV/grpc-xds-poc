package org.example;

import io.grpc.Channel;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.xds.XdsChannelCredentials;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XdsHelloWorldClient {

  private static final Logger logger = Logger.getLogger(XdsHelloWorldClient.class.getName());
  private final GreeterGrpc.GreeterBlockingStub blockingStub;

  public XdsHelloWorldClient(Channel channel) {
    blockingStub = GreeterGrpc.newBlockingStub(channel);
  }

  public void greet(String name) {
    logger.info("Will try to greet " + name + " ...");
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response;
    try {
      response = blockingStub.sayHello(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Greeting: " + response.getMessage());
  }

  public static void main(String[] args) throws Exception {
    String target = "xds:///localhost:50051";
    ManagedChannel channel = XdsChannelCredentials.create(InsecureChannelCredentials.create())
        .newChannelBuilder(target)
        .build();
    try {
      XdsHelloWorldClient client = new XdsHelloWorldClient(channel);
      client.greet("xDS POC");
    } finally {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
