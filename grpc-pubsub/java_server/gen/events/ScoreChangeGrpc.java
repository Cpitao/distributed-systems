package events;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.54.0)",
    comments = "Source: event.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ScoreChangeGrpc {

  private ScoreChangeGrpc() {}

  public static final String SERVICE_NAME = "ScoreChange";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<events.GameSubscription,
      events.GameScore> getSubscribeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Subscribe",
      requestType = events.GameSubscription.class,
      responseType = events.GameScore.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<events.GameSubscription,
      events.GameScore> getSubscribeMethod() {
    io.grpc.MethodDescriptor<events.GameSubscription, events.GameScore> getSubscribeMethod;
    if ((getSubscribeMethod = ScoreChangeGrpc.getSubscribeMethod) == null) {
      synchronized (ScoreChangeGrpc.class) {
        if ((getSubscribeMethod = ScoreChangeGrpc.getSubscribeMethod) == null) {
          ScoreChangeGrpc.getSubscribeMethod = getSubscribeMethod =
              io.grpc.MethodDescriptor.<events.GameSubscription, events.GameScore>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Subscribe"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  events.GameSubscription.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  events.GameScore.getDefaultInstance()))
              .setSchemaDescriptor(new ScoreChangeMethodDescriptorSupplier("Subscribe"))
              .build();
        }
      }
    }
    return getSubscribeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ScoreChangeStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ScoreChangeStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ScoreChangeStub>() {
        @java.lang.Override
        public ScoreChangeStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ScoreChangeStub(channel, callOptions);
        }
      };
    return ScoreChangeStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ScoreChangeBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ScoreChangeBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ScoreChangeBlockingStub>() {
        @java.lang.Override
        public ScoreChangeBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ScoreChangeBlockingStub(channel, callOptions);
        }
      };
    return ScoreChangeBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ScoreChangeFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ScoreChangeFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ScoreChangeFutureStub>() {
        @java.lang.Override
        public ScoreChangeFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ScoreChangeFutureStub(channel, callOptions);
        }
      };
    return ScoreChangeFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void subscribe(events.GameSubscription request,
        io.grpc.stub.StreamObserver<events.GameScore> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ScoreChange.
   */
  public static abstract class ScoreChangeImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ScoreChangeGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ScoreChange.
   */
  public static final class ScoreChangeStub
      extends io.grpc.stub.AbstractAsyncStub<ScoreChangeStub> {
    private ScoreChangeStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ScoreChangeStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ScoreChangeStub(channel, callOptions);
    }

    /**
     */
    public void subscribe(events.GameSubscription request,
        io.grpc.stub.StreamObserver<events.GameScore> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ScoreChange.
   */
  public static final class ScoreChangeBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ScoreChangeBlockingStub> {
    private ScoreChangeBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ScoreChangeBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ScoreChangeBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<events.GameScore> subscribe(
        events.GameSubscription request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ScoreChange.
   */
  public static final class ScoreChangeFutureStub
      extends io.grpc.stub.AbstractFutureStub<ScoreChangeFutureStub> {
    private ScoreChangeFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ScoreChangeFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ScoreChangeFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_SUBSCRIBE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SUBSCRIBE:
          serviceImpl.subscribe((events.GameSubscription) request,
              (io.grpc.stub.StreamObserver<events.GameScore>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSubscribeMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              events.GameSubscription,
              events.GameScore>(
                service, METHODID_SUBSCRIBE)))
        .build();
  }

  private static abstract class ScoreChangeBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ScoreChangeBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return events.Event.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ScoreChange");
    }
  }

  private static final class ScoreChangeFileDescriptorSupplier
      extends ScoreChangeBaseDescriptorSupplier {
    ScoreChangeFileDescriptorSupplier() {}
  }

  private static final class ScoreChangeMethodDescriptorSupplier
      extends ScoreChangeBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ScoreChangeMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ScoreChangeGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ScoreChangeFileDescriptorSupplier())
              .addMethod(getSubscribeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
