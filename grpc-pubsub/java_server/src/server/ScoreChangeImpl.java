package server;

import events.GameScore;
import events.GameSubscription;
import events.ScoreChangeGrpc;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

public class ScoreChangeImpl extends ScoreChangeGrpc.ScoreChangeImplBase {

    private final SubscribersManager manager;
    public ScoreChangeImpl(SubscribersManager manager) {
        this.manager = manager;
    }

    @Override
    public void subscribe(GameSubscription req, StreamObserver<GameScore> responseObserver) {
        ServerCallStreamObserver<GameScore> obs = (ServerCallStreamObserver<GameScore>) responseObserver;

        obs.setOnCancelHandler(() -> manager.removeSubscriber(obs));

        if (!manager.addSubscriber(obs, req)) {
            Status status = Status.NOT_FOUND;
            obs.onError(status.asException());
        }
    }
}
