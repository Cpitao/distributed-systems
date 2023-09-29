import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.LinkedList;

// Build: ./gradlew build
// then from ./build/classes/java/main:
// Run server: java Server
// Run client: java Client

public class Server {

    private final static LinkedList<String> nicknames = new LinkedList<>();
    private final static LinkedList<ClientHandler> clientHandlers = new LinkedList<>();
    private static boolean running = true;

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server");
        int port = 13337;
        runServer(port);
    }

    private static void runServer(int port) {
        Thread tcpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runTCPChannel(port);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    running = false;
                }
            }
        });
        Thread udpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runUDPChannel(port);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    running = false;
                }
            }
        });
        tcpThread.start();
        udpThread.start();
        try {
            tcpThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            udpThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void runTCPChannel(int port) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Serving on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientThread = new ClientHandler(clientSocket, nicknames);
                clientHandlers.add(clientThread);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (serverSocket != null){
                serverSocket.close();
            }
            for (ClientHandler t: clientHandlers) {
                t.terminate();
            }
            for (ClientHandler t: clientHandlers) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void runUDPChannel(int port) throws IOException {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(port);
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket;
            while (running) {
                receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                byte[] data = receivePacket.getData();
                String remoteAddress = receivePacket.getSocketAddress().toString();
                remoteAddress = remoteAddress.substring(1, remoteAddress.indexOf(':'));
                int remotePort = receivePacket.getPort();

                for (ClientHandler c: clientHandlers) {
                    if (!c.isRemoteHost(remoteAddress, remotePort)) {
                        DatagramPacket sendPacket = new DatagramPacket(data, data.length,
                                InetAddress.getByName(c.getAddress()), c.getPort());
                        socket.send(sendPacket);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            running = false;
        }
    }

    public static void broadcastMessage(String from, String message) {
        for (ClientHandler c: clientHandlers) {
            try {
                c.sendMessage(from, message);
            } catch (IOException ignored) {}
        }
    }




}
