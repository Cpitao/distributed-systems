import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;
    private boolean running = true;
    private String nickname;
    private final LinkedList<String> nicknames;

    private static final int messageLengthBytes = 8;

    public ClientHandler(Socket socket, LinkedList<String> nicknames) throws IOException {
        this.socket = socket;
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.nicknames = nicknames;
    }

    @Override
    public void run() {
        try {
            getHello();
            runClientHandler();
        } catch (IOException ignored) {}
        finally {
            terminate();
        }
    }

    private void getHello() throws IOException {
        while (running) {
            char hello;

            try {
                hello = in.readChar();
            } catch (EOFException ignored) {
                continue;
            }

            if (hello != 'H') {
                out.write("HERR".getBytes(StandardCharsets.UTF_8));
                out.flush();
                continue;
            }

            int nickLength;
            try {
                nickLength = in.readInt();
            } catch (IOException ignored) {
                out.write("HERR".getBytes(StandardCharsets.UTF_8));
                out.flush();
                continue;
            }

            if (nickLength <= 0) {
                out.write("HERR".getBytes(StandardCharsets.UTF_8));
                out.flush();
                continue;
            }

            nickname = new String(in.readNBytes(nickLength), StandardCharsets.UTF_8);

            if (nicknames.stream().filter(x -> x.equals(nickname)).toArray().length != 0) {
                // Hello nickname taken message
                out.writeUTF("HTAK");
                out.flush();
            } else {
                // Hello OK
                out.writeUTF("HOK");
                out.flush();
                synchronized (nicknames) {
                    nicknames.add(nickname);
                }
                System.out.println("[" + Logger.getDateTime() + "]" + " Connection from " + socket.getInetAddress()
                        + ":" + socket.getPort() + " | " + nickname);
                return;
            }
        }
    }

    private void runClientHandler() {
        while (running) {
            try {
                receiveMessage();
            } catch (Exception ignored) {
                break;
            }
        }
    }

    public void sendMessage(String from, String message) throws IOException {
        if (from.equals(nickname)) return;

        ByteBuffer payload = ByteBuffer
                .allocate(Character.BYTES + Integer.BYTES
                        + from.getBytes(StandardCharsets.UTF_8).length + messageLengthBytes +
                        message.getBytes(StandardCharsets.UTF_8).length)
                .putChar('M')
                .putInt(from.getBytes(StandardCharsets.UTF_8).length)
                .put(from.getBytes(StandardCharsets.UTF_8))
                .putInt(message.getBytes(StandardCharsets.UTF_8).length)
                .put(message.getBytes(StandardCharsets.UTF_8));
        out.write(payload.array());
        out.flush();
    }

    public boolean isRemoteHost(String address, int port) {
        String remoteAddress = socket.getRemoteSocketAddress().toString();
        remoteAddress = remoteAddress.substring(1, remoteAddress.indexOf(':'));
        return address.equals(remoteAddress) && port == socket.getPort();
    }

    public String getAddress() {
        String fullAddress = socket.getRemoteSocketAddress().toString();
        return fullAddress.substring(1, fullAddress.indexOf(':'));
    }

    public int getPort() {
        return socket.getPort();
    }

    public void receiveMessage() throws IOException {
        // message in the form of:
        // 'M' - denoting start of message
        // 8 byte length of the message
        // content

        while (running && in.readChar() != 'M');

        int messageLength = in.readInt();
        if (messageLength < 0) {
            return;
        }


        byte[] message = new byte[messageLength];
        if (in.readNBytes(message, 0, messageLength) != messageLength) {
            // something went wrong
            return;
        }

        System.out.println(new String(message, StandardCharsets.UTF_8));

        Server.broadcastMessage(nickname, new String(message, StandardCharsets.UTF_8));
    }
    public void terminate() {
        running = false;

        try {
            socket.close();
        } catch (IOException ignored) {}

        synchronized (nicknames) {
            nicknames.remove(nickname);
            System.out.println("[" + Logger.getDateTime() + "] " + "Removing client " + nickname);
        }
    }
}
