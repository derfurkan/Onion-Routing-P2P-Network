package de.furkan.network;

import com.google.gson.Gson;
import de.furkan.P2PServer;
import de.furkan.network.packets.Packet;
import lombok.Getter;

import java.security.PublicKey;

@Getter
public class ConnectionHandler {

    private final NetworkSocket serverSocket;
    private final Gson gson;
    private final P2PServer p2PServer;

    public ConnectionHandler(P2PServer p2PServer, Gson gson) {
        this.gson = gson;
        this.p2PServer = p2PServer;
        this.serverSocket = new NetworkSocket("127.0.0.1", 7979);
    }

    public void sendSocketPacket(NetworkSocket networkSocket, PublicKey publicKey, Packet packet) {
        if (!networkSocket.isConnected()) return;
        try {
            networkSocket.sendMessage(p2PServer.getCryptoUtil().encryptWithPublicKey(gson.toJson(packet), publicKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
