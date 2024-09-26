package de.furkan.network;

import com.google.gson.Gson;
import de.furkan.P2PClient;
import de.furkan.network.packets.Packet;
import lombok.Getter;

import java.security.PublicKey;

@Getter
public class ConnectionHandler {

    private final NetworkSocket serverSocket;
    private final Gson gson;
    private final P2PClient p2PClient;

    public ConnectionHandler(P2PClient p2PClient, Gson gson) {
        this.gson = gson;
        this.p2PClient = p2PClient;
        this.serverSocket = new NetworkSocket("127.0.0.1", 7979);
    }

    public void sendSocketPacket(NetworkSocket networkSocket, PublicKey publicKey, Packet packet) {
        if (!networkSocket.isConnected()) return;
        try {
            networkSocket.sendMessage(p2PClient.getCryptoUtil().encryptWithPublicKey(gson.toJson(packet), publicKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
