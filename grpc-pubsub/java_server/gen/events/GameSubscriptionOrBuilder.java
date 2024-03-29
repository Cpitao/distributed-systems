// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: event.proto

package events;

public interface GameSubscriptionOrBuilder extends
    // @@protoc_insertion_point(interface_extends:GameSubscription)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.GameSubscription.Type type = 1;</code>
   * @return The enum numeric value on the wire for type.
   */
  int getTypeValue();
  /**
   * <code>.GameSubscription.Type type = 1;</code>
   * @return The type.
   */
  events.GameSubscription.Type getType();

  /**
   * <pre>
   * either player or team name, depending on typeis
   * </pre>
   *
   * <code>string side1 = 2;</code>
   * @return The side1.
   */
  java.lang.String getSide1();
  /**
   * <pre>
   * either player or team name, depending on typeis
   * </pre>
   *
   * <code>string side1 = 2;</code>
   * @return The bytes for side1.
   */
  com.google.protobuf.ByteString
      getSide1Bytes();

  /**
   * <pre>
   * as above
   * </pre>
   *
   * <code>string side2 = 3;</code>
   * @return The side2.
   */
  java.lang.String getSide2();
  /**
   * <pre>
   * as above
   * </pre>
   *
   * <code>string side2 = 3;</code>
   * @return The bytes for side2.
   */
  com.google.protobuf.ByteString
      getSide2Bytes();
}
