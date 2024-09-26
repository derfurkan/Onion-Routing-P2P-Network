package de.furkan.network;

public interface DisconnectReceiver {

    void onDisconnect(DisconnectType disconnectType);

    enum DisconnectType {
        CONNECTION_LOST, // When Socket closed connection.
        CONNECTION_CLOSED // When Client (we) closed connection.
    }
}
