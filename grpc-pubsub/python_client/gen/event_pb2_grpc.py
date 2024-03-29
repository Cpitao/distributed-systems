# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
"""Client and server classes corresponding to protobuf-defined services."""
import grpc

import gen.event_pb2 as event__pb2


class ScoreChangeStub(object):
    """Missing associated documentation comment in .proto file."""

    def __init__(self, channel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """
        self.Subscribe = channel.unary_stream(
                '/ScoreChange/Subscribe',
                request_serializer=event__pb2.GameSubscription.SerializeToString,
                response_deserializer=event__pb2.GameScore.FromString,
                )


class ScoreChangeServicer(object):
    """Missing associated documentation comment in .proto file."""

    def Subscribe(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')


def add_ScoreChangeServicer_to_server(servicer, server):
    rpc_method_handlers = {
            'Subscribe': grpc.unary_stream_rpc_method_handler(
                    servicer.Subscribe,
                    request_deserializer=event__pb2.GameSubscription.FromString,
                    response_serializer=event__pb2.GameScore.SerializeToString,
            ),
    }
    generic_handler = grpc.method_handlers_generic_handler(
            'ScoreChange', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))


 # This class is part of an EXPERIMENTAL API.
class ScoreChange(object):
    """Missing associated documentation comment in .proto file."""

    @staticmethod
    def Subscribe(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_stream(request, target, '/ScoreChange/Subscribe',
            event__pb2.GameSubscription.SerializeToString,
            event__pb2.GameScore.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)
