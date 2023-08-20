package thedimas.network.server;

import thedimas.network.enums.DcReason;
import thedimas.network.packet.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

@SuppressWarnings("unused")
public class Server {
    private final List<ServerClientHandler> clients = new ArrayList<>();
    private final List<ServerListener> listeners = new ArrayList<>();
    private final int port;
    private ServerSocket serverSocket;
    private boolean listening;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        logger.info("Starting...");
        listening = true;
        serverSocket = new ServerSocket(port);
        logger.config("Started");
        listeners.forEach(ServerListener::started);
        try {
            while (listening) {
                Socket clientSocket = serverSocket.accept();
                ServerClientHandler clientHandler = new ServerClientHandler(clientSocket);
                String ip = clientSocket.getInetAddress().getHostAddress();
                listeners.forEach(ServerListener::stopped);
                clients.add(clientHandler);
                logger.info("New connection from " + ip);
                clientHandler.init();
                listeners.forEach(l -> l.connected(clientHandler));
                new Thread(() -> {
                    clientHandler.received(packet -> {
                        logger.info("Packet received " + packet.getClass().getSimpleName());
                        listeners.forEach(l -> l.received(clientHandler, packet));
                    });
                    clientHandler.disconnected(reason ->
                            listeners.forEach(l -> l.disconnected(clientHandler, reason))
                    );
                    clientHandler.listen();
                }).start();
            }
        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Socket closed", e);
        }
    }

    public void send(Packet packet) {
        clients.forEach(c -> {
            try {
                c.send(packet);
            } catch (IOException e) {
                logger.log(Level.FINE, "Unable to send packet to " + c.getIp(), e);
            }
        });
    }

    public void stop() throws IOException {
        listening = false;
        clients.forEach(client -> client.disconnect(DcReason.SERVER_CLOSED));
        serverSocket.close();
        listeners.forEach(ServerListener::stopped);
    }

    public void addListener(ServerListener listener) {
        listeners.add(listener);
    }
}