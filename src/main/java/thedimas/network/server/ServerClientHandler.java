package thedimas.network.server;

import lombok.Getter;
import thedimas.network.enums.DcReason;
import thedimas.network.packet.DisconnectPacket;
import thedimas.network.packet.Packet;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

import static thedimas.network.Main.logger;

public class ServerClientHandler {
    @Getter
    private final Socket socket;
    private Consumer<Packet> packetListener = (packet) -> {
    };

    private Consumer<DcReason> disconnectListener = (reason -> {
    });
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean listening;

    public ServerClientHandler(Socket socket) {
        this.socket = socket;
    }

    void start() throws IOException {
        listening = true;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            while (listening) {
                Object receivedObject = in.readObject();
                handlePacket(receivedObject);
            }
        } catch (IOException e) {
            if (e instanceof ObjectStreamException) {
                logger.warning("Corrupted stream");
                handleDisconnect(DcReason.STREAM_CORRUPTED);
            } else {
                logger.warning("Connection closed");
                handleDisconnect(DcReason.CONNECTION_CLOSED);
            }
        } catch (ClassNotFoundException e) {
            logger.severe("Class not found");
            e.printStackTrace();
        }
    }

    public void send(Packet packet) throws IOException {
        out.writeObject(packet);
    }

    public void disconnect() {
        try {
            listening = false;
            if (in != null) in.close();
            if (out != null) out.close();
            socket.close();
        } catch (IOException e) {
            logger.severe("Error while disconnecting client " + socket.getInetAddress().getHostAddress());
        }
    }

    void received(Consumer<Packet> consumer) { // TODO: List of consumers
        packetListener = consumer;
    }

    void disconnected(Consumer<DcReason> consumer) {
        disconnectListener = consumer;
    }

    void handlePacket(Object object) {
        logger.config("New object: " + object.toString());
        if (object instanceof Packet packet) {
            if (packet instanceof DisconnectPacket disconnectPacket) {
                handleDisconnect(disconnectPacket.getReason());
            } else {
                packetListener.accept(packet);
            }
        }
    }

    private void handleDisconnect(DcReason reason) {
        logger.warning("Disconnected: " + reason.name());
        disconnectListener.accept(reason);
    }

}