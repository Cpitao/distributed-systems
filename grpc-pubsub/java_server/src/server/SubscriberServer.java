package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import events.FootballGame;
import events.GameScore;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerTransportFilter;
import io.grpc.internal.ClientTransport;


public class SubscriberServer
{
	private static final Logger logger = Logger.getLogger(SubscriberServer.class.getName());

	private String address = "127.0.0.5";
	private int port = 50051;
	private Server server;
	public static EventsManager eventsManager = new EventsManager();
	public static SubscribersManager subscribersManager = new SubscribersManager(eventsManager);

	private SocketAddress socket;



	private void start() throws IOException, InterruptedException {
		try {
			socket = new InetSocketAddress(InetAddress.getByName(address), port);
		} catch(UnknownHostException ignored) {};

		eventsManager.setSubscribersManager(subscribersManager);

		addGame(eventsManager);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				simulate(eventsManager);
			}
		});
		t.start();

		server = ServerBuilder.forPort(port)
				.keepAliveTime(10, TimeUnit.SECONDS)
//				.addTransportFilter(new KeepAliveFilter())
				.addService(new ScoreChangeImpl(subscribersManager))
				.build()
				.start();

		logger.info("Server started, listening on " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Use stderr here since the logger may have been reset by its JVM shutdown hook.
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				SubscriberServer.this.stop();
				System.err.println("*** server shut down");
			}
		});

		t.join();

	}

	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		final SubscriberServer server = new SubscriberServer();
		server.start();
		server.blockUntilShutdown();
	}

	public void addGame(EventsManager eventsManager) {
		GameScore game = GameScore.newBuilder()
				.setFootballGame(
						FootballGame.newBuilder()
								.setTeam1("Real Madrid")
								.setTeam2("Barcelona")
								.setScore(
										FootballGame.FootballScore.newBuilder()
												.setTeam1Score(0)
												.setTeam2Score(10)
												.build()
								)
								.setScorers(
										FootballGame.Scorers.newBuilder()
												.addTeam2Scorers("Lewandowski").build())
								.build()
				).build();

		GameScore game2 = GameScore.newBuilder()
				.setFootballGame(
						FootballGame.newBuilder()
								.setTeam1("Test1")
								.setTeam2("Test2")
								.setScore(
										FootballGame.FootballScore.newBuilder()
												.setTeam1Score(0)
												.setTeam2Score(10)
												.build()
								)
								.setScorers(
										FootballGame.Scorers.newBuilder()
												.addTeam2Scorers("Peszko").build())
								.build()
				).build();

		eventsManager.addGame(game);
		eventsManager.addGame(game2);
	}

	public void simulate(EventsManager eventsManager) {
		for (int i=0; i < 1000000; i++) {
			GameScore game = GameScore.newBuilder()
				.setFootballGame(
					FootballGame.newBuilder()
						.setTeam1("Test01")
						.setTeam2("Test02")
						.setScore(
							FootballGame.FootballScore.newBuilder()
								.setTeam1Score(0)
								.setTeam2Score(i)
								.build()
						)
						.setScorers(
								FootballGame.Scorers.newBuilder()
										.addTeam2Scorers("Scorer").build())
						.build()
				).build();

			eventsManager.changeScore(game);

			GameScore game2 = GameScore.newBuilder()
				.setFootballGame(
					FootballGame.newBuilder()
						.setTeam1("Test1")
						.setTeam2("Test2")
						.setScore(
							FootballGame.FootballScore.newBuilder()
								.setTeam1Score(i - 1)
								.setTeam2Score(i + 3)
								.build()
						)
						.setScorers(
							FootballGame.Scorers.newBuilder()
								.addTeam2Scorers("Scorer").build())
						.build()
				).build();

			eventsManager.changeScore(game2);
			Random r = new Random();
			try {
				Thread.sleep(r.nextInt(15000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

//	private static class KeepAliveFilter extends ServerTransportFilter {
//		private final int MAX_FAILURES = 3;
//		private int failureCount = 0;
//
//		@Override
//		public void transportTerminated(ClientTransport transport) {
//			if (++failureCount > MAX_FAILURES) {
//				System.out.println("Max failures reached, retrying connection...");
//
//				failureCount = 0;
//			} else {
//				System.out.println("Transport terminated, retrying keepalive ping...");
//				transport.ping(new PingCallbackClass(), Executors.newSingleThreadExecutor());
//			}
//		}
//	}
//
//	public static class PingCallbackClass implements ClientTransport.PingCallback {
//
//		@Override
//		public void onSuccess(long roundTripTimeNanos) {}
//
//		@Override
//		public void onFailure(Throwable throwable) {
//			SubscriberServer.subscribersManager.checkSubs();
//		}
//	}
}