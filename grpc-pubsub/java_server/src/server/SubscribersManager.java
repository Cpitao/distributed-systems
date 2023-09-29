package server;

import events.GameScore;
import events.GameSubscription;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SubscribersManager {

    private Map<GameSubscription, LinkedList<StreamObserver<GameScore>>> subscribers = new HashMap<>();
    private Map<GameSubscription, LinkedList<StreamObserver<GameScore>>> disconnectedSubs = new HashMap<>();
    private Map<GameSubscription, LinkedList<StreamObserver<GameScore>>> resumedSubs = new HashMap<>();
    private Map<StreamObserver<GameScore>, LinkedList<String>> bufferedMessages = new HashMap<>();
    private EventsManager eventsManager;

    public SubscribersManager(EventsManager manager) {
        this.eventsManager = manager;
    }

    public void removeSubscriber(StreamObserver<GameScore> observer) {
        for (LinkedList<StreamObserver<GameScore>> l : subscribers.values()) {
            l.remove(observer);
        }
    }

    public boolean addSubscriber(StreamObserver<GameScore> observer, GameSubscription subscription) {
        for (Map.Entry<GameSubscription, LinkedList<StreamObserver<GameScore>>> entry : subscribers.entrySet()) {
            GameSubscription sub = entry.getKey();
            if ((sub.getSide1().equals(subscription.getSide1()) && sub.getSide2().equals(subscription.getSide2()))
                || (sub.getSide1().equals(subscription.getSide2()) && sub.getSide2().equals(subscription.getSide1()))) {
                entry.getValue().add(observer);
                System.out.println("New sub");
                break;
            }
        }

        GameScore gameScore = eventsManager.getGameBySubscription(subscription);
        if (gameScore == null) return false;
        System.out.println("Sending");
        observer.onNext(gameScore);
        return true;
    }

    public void notifySubscribers(GameSubscription subscription, GameScore score) {
        for (Map.Entry<GameSubscription, LinkedList<StreamObserver<GameScore>>> entry: subscribers.entrySet()) {
            GameSubscription sub = entry.getKey();
            if ((sub.getSide1().equals(subscription.getSide1()) && sub.getSide2().equals(subscription.getSide2()))
                    || (sub.getSide1().equals(subscription.getSide2()) && sub.getSide2().equals(subscription.getSide1()))) {
                for (StreamObserver<GameScore> stream : entry.getValue()) {
                    stream.onNext(score);
                }
                break;
            }
        }
    }

    public void gameAdded(GameSubscription game) {
        subscribers.put(game, new LinkedList<>());
    }

//    public void checkSubs() {
//        for (GameSubscription game : subscribers.keySet()) {
//            for (StreamObserver<GameScore> observer : subscribers.get(game)) {
//                observer.
//            }
//        }
//    }
}