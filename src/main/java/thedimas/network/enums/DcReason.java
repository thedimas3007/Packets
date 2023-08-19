package thedimas.network.enums;

@SuppressWarnings("unused")
public enum DcReason {
    /**
     * When the client or server forcefully closes connection.
     */
    CONNECTION_CLOSED,

    /**
     * When the client gracefully disconnects from the server
     */
    DISCONNECTED,

    /**
     * Is sent to all clients when the server is shutting down
     */
    SERVER_CLOSED,

    /**
     * Usually is only sent to listeners
     */
    STREAM_CORRUPTED,
    ACCESS_DENIED,
    TIMEOUT
}
