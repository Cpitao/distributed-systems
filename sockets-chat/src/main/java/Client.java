import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    private static String serverDest = "localhost";
    private static String multicastDest = "234.0.0.0";
    private static int serverPort = 13337;
    private static int clientPort = 13338;
    private static String nickname;
    private static boolean running = true;
    private static char mode = 'T';

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            serverDest = args[0];
        }
        runClient();
    }

    private static void runClient() throws IOException {
        Socket socket = null;
        try {
            System.out.println("[System] Connecting to " + serverDest + ":" + serverPort);
            socket = new Socket(serverDest, serverPort, null, clientPort);
            System.out.println("[System] Connection established");

            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            try {
                register(out, in);
            } catch (IOException e) {
                System.out.println("[System] Unable to connect with server");
                return;
            }

            if (running)
                runChat(out, in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    private static void runChat(DataOutputStream out, DataInputStream in) {
        System.out.println("Type exit to exit");
        System.out.println("End your message with two empty lines to send");
        System.out.println("---------------------------------------------");
        System.out.println("       Waiting for incoming messages         ");
        System.out.println("---------------------------------------------");

        Thread tcpReceiver = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        while (in.readChar() != 'M') ; // wait for message start
                        int nameLength = in.readInt();
                        byte[] from = in.readNBytes(nameLength);
                        String fromName = new String(from, StandardCharsets.UTF_8);

                        int messageLength = in.readInt();
                        byte[] messageBytes = in.readNBytes(messageLength);
                        String message = new String(messageBytes, StandardCharsets.UTF_8);

                        System.out.println("\n" + fromName + ": \n" + message);
                    } catch (IOException e) {
                        running = false;
                        return;
                    }
                }
            }
        });
        Thread messageSender = new Thread(new Runnable() {
            @Override
            public void run() {
                String lineSeparator = System.lineSeparator();
                while (running) {
                    try {
                        Scanner scanner = new Scanner(System.in);
                        StringBuilder messageBuilder = new StringBuilder();
                        do {
                            String line = scanner.nextLine();
                            messageBuilder.append(line);

                            switch (messageBuilder.toString()) {
                                case "exit" :
                                    running = false;
                                    return;
                                case "U":
                                    System.out.println("[System] Next message will be sent using UDP");
                                    mode = 'U';
                                    messageBuilder = new StringBuilder();
                                    continue;
                                case "M":
                                    System.out.println("[System] Next message will be sent using multicast");
                                    mode = 'M';
                                    messageBuilder = new StringBuilder();
                                    continue;
                            }

                            messageBuilder.append(lineSeparator);
                        } while (!messageBuilder.toString().endsWith(lineSeparator + lineSeparator));
                        sendMessage(out, messageBuilder.toString().strip());
                    } catch (IOException e) {
                        running = false;
                        return;
                    }
                }
            }
        });
        Thread udpHandler = new UDPHandler();
        udpHandler.start();
        tcpReceiver.start();
        messageSender.start();

        try {
            tcpReceiver.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            messageSender.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            udpHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void register(DataOutputStream out, DataInputStream in) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Specify your nickname: ");
        nickname = scanner.nextLine();
        while (nickname.length() > 255) {
            System.out.print("Max length exceeded. Try another: ");
            nickname = scanner.nextLine();
        }
        byte[] payload = ByteBuffer
                .allocate(Character.BYTES + Integer.BYTES + nickname.getBytes(StandardCharsets.UTF_8).length)
                .putChar('H')
                .putInt(nickname.getBytes(StandardCharsets.UTF_8).length)
                .put(nickname.getBytes(StandardCharsets.UTF_8)).array();
        out.write(payload);
        out.flush();

        String response;
        response = in.readUTF();

        while (response.equals("HTAK")
                || response.equals("HERR")) {
            if (response.equals("HERR")) {
                System.out.println("Unknown error.");
                running = false;
                return;
            }
            System.out.print("Nickname taken. Try another: ");
            nickname = scanner.nextLine();
            payload = ByteBuffer
                    .allocate(Character.BYTES + Integer.BYTES + nickname.getBytes(StandardCharsets.UTF_8).length)
                    .putChar('H')
                    .putInt(nickname.length())
                    .put(nickname.getBytes(StandardCharsets.UTF_8)).array();
            out.write(payload);
            out.flush();
            response = in.readUTF();
        }
        System.out.println("Registered successfully");
    }

    private static void sendMessage(DataOutputStream out, String message) throws IOException {
        if (mode == 'T') {
            byte[] content = ByteBuffer
                    .allocate(Character.BYTES + Integer.BYTES + message.getBytes(StandardCharsets.UTF_8).length)
                    .putChar('M')
                    .putInt(message.getBytes(StandardCharsets.UTF_8).length)
                    .put(message.getBytes(StandardCharsets.UTF_8)).array();
            out.write(content);
            out.flush();
        } else {
            byte[] content = ByteBuffer
                    .allocate(Character.BYTES + Integer.BYTES + nickname.getBytes(StandardCharsets.UTF_8).length +
                              Integer.BYTES + message.getBytes(StandardCharsets.UTF_8).length)
                    .putChar('M')
                    .putInt(nickname.getBytes(StandardCharsets.UTF_8).length)
                    .put(nickname.getBytes(StandardCharsets.UTF_8))
                    .putInt(message.getBytes(StandardCharsets.UTF_8).length)
                    .put(message.getBytes(StandardCharsets.UTF_8)).array();
            InetAddress address;
            int port;
            if (mode == 'U') {
                address = InetAddress.getByName(serverDest);
                port = serverPort;
            } else {
                address = InetAddress.getByName(multicastDest);
                port = clientPort;
            }
            DatagramPacket packet = new DatagramPacket(content, content.length, address, port);
            UDPHandler.sendPacket(packet);
            mode = 'T';
            System.out.println("[System] Back in TCP mode");
        }
    }

    private static class UDPHandler extends Thread {
        private static MulticastSocket socket = null;
        private static final byte[] buffer = new byte[1024];

        public UDPHandler() {
            try {
                socket = new MulticastSocket(clientPort);
                socket.setLoopbackMode(true);
                InetAddress group = InetAddress.getByName(multicastDest);
                socket.joinGroup(group);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (running) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receivePacket);
                    byte[] data = receivePacket.getData();
                    DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data));
                    char tag = stream.readChar();
                    if (tag != 'M') continue;
                    int nameLength = stream.readInt();
                    String name = new String(stream.readNBytes(nameLength), StandardCharsets.UTF_8);
                    int messageLength = stream.readInt();
                    String message = new String(stream.readNBytes(messageLength), StandardCharsets.UTF_8);

                    System.out.println("\n" + name + ": \n" + message);

                } catch (Exception ignored) {
                }
            }
        }

        public static void sendPacket(DatagramPacket packet) throws IOException {
            socket.send(packet);
        }
    }
}
