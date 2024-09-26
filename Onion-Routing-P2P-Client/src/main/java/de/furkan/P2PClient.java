package de.furkan;

import com.google.gson.Gson;
import de.furkan.crypt.CryptoUtil;
import de.furkan.network.ConnectionHandler;
import de.furkan.network.NetworkSocket;
import de.furkan.network.packets.Packet;
import de.furkan.network.packets.PacketHandler;
import de.furkan.network.packets.type.HopPacket;
import de.furkan.network.packets.type.PublicKeyPacket;
import lombok.Getter;

import java.io.IOException;
import java.security.PublicKey;

@Getter
public class P2PClient {

    public static P2PClient p2PClient;
    private final Logger logger = new Logger(P2PClient.class);
    private final CryptoUtil cryptoUtil = new CryptoUtil();
    private final PacketHandler packetHandler;
    private final Gson gson = new Gson();
    private final ConnectionHandler connectionHandler;
    private PublicKey serverPublicKey;

    public P2PClient() {
        p2PClient = this;
        cryptoUtil.generateCryptoKeys();
        packetHandler = new PacketHandler(this, gson);
        connectionHandler = new ConnectionHandler(this, gson);
        try {
            connectionHandler.getServerSocket().connectSocket();
            logger.info("Connected to Server.");
            connectionHandler
                    .getServerSocket()
                    .registerNewAsyncReceiver(
                            rawMessage -> {
                                logger.info("Received Server message: " + rawMessage);
                                String decrypted = rawMessage;
                                try {
                                    if (!rawMessage.startsWith("{")) {
                                        decrypted = cryptoUtil.decrypt(rawMessage);
                                    }
                                    Packet packet = (Packet) packetHandler.buildPacket(decrypted);
                                    switch (packet.getPacketType()) {
                                        case PUBLIC_KEY -> {
                                            PublicKeyPacket publicKeyPacket = (PublicKeyPacket) packet;
                                            if (serverPublicKey == null) {
                                                serverPublicKey = (PublicKey) cryptoUtil.toKey(publicKeyPacket.getPublicKey(), CryptoUtil.KeyType.PUBLIC);
                                                logger.info("Registered Servers public key.");
                                            }
                                        }
                                        case HOP -> {
                                            logger.info("Routing Hop Packet...");
                                            HopPacket hopPacket = (HopPacket) packet;
                                            NetworkSocket hopSocket = new NetworkSocket(hopPacket.getHopIp(), hopPacket.getHopPort());
                                            hopSocket.connectSocketAsync().whenComplete((success, throwable) -> {
                                                if (!success) {
                                                    logger.error("Could not connect to Hop (" + throwable.getMessage() + ").");
                                                    return;
                                                }
                                                hopSocket.sendMessage(hopPacket.getEncryptedMessage());
                                                logger.info("Routed Hop Packet!");
                                            });
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.error("Something went wrong (" + e.getMessage() + ")");
                                }
                            });


        } catch (IOException e) {
            logger.error("Could not connect to Server.");
        }
    }
}
