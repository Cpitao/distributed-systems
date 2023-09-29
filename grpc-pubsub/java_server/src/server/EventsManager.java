package server;

import events.FootballGame;
import events.GameScore;
import events.GameSubscription;
import events.TennisGame;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EventsManager {

    private List<GameScore> footballGames = new LinkedList<>();
    private List<GameScore> tennisGames = new LinkedList<>();
    private SubscribersManager manager;

    public void setSubscribersManager(SubscribersManager manager) {
        this.manager = manager;
    }

    public void changeScore(GameScore score) {
        if (score.hasFootballGame()) {
            Iterator<GameScore> iterator = footballGames.iterator();
            while (iterator.hasNext()) {
                GameScore game = iterator.next();
                String savedTeam1 = game.getFootballGame().getTeam1();
                String savedTeam2 = game.getFootballGame().getTeam2();
                String team1 = score.getFootballGame().getTeam1();
                String team2 = score.getFootballGame().getTeam2();
                if ((savedTeam1.equals(team1) && savedTeam2.equals(team2)) ||
                        (savedTeam2.equals(team1) && savedTeam1.equals(team2))) {
                    footballGames.remove(game);
                    footballGames.add(score);
                    GameSubscription subscription = GameSubscription.newBuilder()
                            .setSide1(team1)
                            .setSide2(team2)
                            .setType(GameSubscription.Type.FOOTBALL)
                            .build();
                    if (manager != null) manager.notifySubscribers(subscription, score);
                    break;
                }
            }
        } else if (score.hasTennisGame()) {
            // Mock
        }
    }

    public void addGame(GameScore score) {
        if (score.hasTennisGame()) {
            tennisGames.add(score);
            if(manager != null) {
                manager.gameAdded(
                        GameSubscription.newBuilder()
                                .setType(GameSubscription.Type.TENNIS)
                                .setSide1(score.getFootballGame().getTeam1())
                                .setSide2(score.getFootballGame().getTeam2()).build());
            }
        } else if (score.hasFootballGame()) {
            footballGames.add(score);
            if(manager != null) {
                manager.gameAdded(
                        GameSubscription.newBuilder()
                                .setType(GameSubscription.Type.FOOTBALL)
                                .setSide1(score.getFootballGame().getTeam1())
                                .setSide2(score.getFootballGame().getTeam2()).build());
            }
        }
    }

    public GameScore getGameBySubscription(GameSubscription subscription) {
        String side1 = subscription.getSide1();
        String side2 = subscription.getSide2();
        switch (subscription.getType()) {
            case FOOTBALL: {
                for (GameScore game: footballGames) {
                    FootballGame footballGame = game.getFootballGame();
                    if ((footballGame.getTeam1().equals(side1) && footballGame.getTeam2().equals(side2)) ||
                        (footballGame.getTeam1().equals(side2) && footballGame.getTeam2().equals(side1)))
                        return game;
                }
                break;
            }
            case TENNIS: {
                for (GameScore game: tennisGames) {
                    TennisGame tennisGame = game.getTennisGame();
                    if ((tennisGame.getPlayer1().equals(side1) && tennisGame.getPlayer2().equals(side2)) ||
                        (tennisGame.getPlayer1().equals(side2) && tennisGame.getPlayer2().equals(side1)))
                        return game;
                }
                break;
            }
        }
        return null;
    }

    public LinkedList<GameScore> getGames() {
        LinkedList<GameScore> allGames = new LinkedList<GameScore>();
        allGames.addAll(footballGames);
        allGames.addAll(tennisGames);

        return allGames;
    }
}
