package de.furkan;

import com.google.gson.Gson;
import de.furkan.crypt.CryptoUtil;
import de.furkan.network.ConnectionHandler;
import de.furkan.network.NetworkSocket;
import de.furkan.network.packets.Packet;
import de.furkan.network.packets.PacketHandler;
import de.furkan.network.packets.type.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import lombok.Getter;

@Getter
public class PeerToPeer {

  public static PeerToPeer peerToPeer;
  private final Logger logger = new Logger(PeerToPeer.class);
  private final CryptoUtil cryptoUtil = new CryptoUtil();
  private final PacketHandler packetHandler;
  private final Gson gson = new Gson();
  private final ConnectionHandler connectionHandler;
  private final ArrayList<ServerClientThread> serverClientThreads = new ArrayList<>();
  private PublicKey serverPublicKey;
  private ServerSocket serverSocket;

  public PeerToPeer(boolean isServer) {
    peerToPeer = this;
    cryptoUtil.generateCryptoKeys();
    packetHandler = new PacketHandler(this, gson);
    connectionHandler = new ConnectionHandler(this, gson);
    if (isServer) runServer();
    else runClient();
  }

  private void runClient() {
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
                  if (!rawMessage.startsWith("{")) { // TODO: Other check for unencrypted message.
                    decrypted = cryptoUtil.decrypt(rawMessage);
                  }
                  System.out.println("Server message: " + decrypted);
                  Packet packet = (Packet) packetHandler.buildPacket(decrypted);
                  switch (packet.getPacketType()) {
                    case PUBLIC_KEY -> {
                      PublicKeyPacket publicKeyPacket = (PublicKeyPacket) packet;
                      if (serverPublicKey == null) {
                        serverPublicKey =
                            (PublicKey)
                                cryptoUtil.toKey(
                                    publicKeyPacket.getPublicKey(), CryptoUtil.KeyType.PUBLIC);
                        logger.info("Registered Servers public key.");
                        connectionHandler.sendEncryptedServerPacket(
                            serverPublicKey,
                            new InformationPacket("Furki", cryptoUtil.getPublicKeyString()));

                        // Test:
                        connectionHandler.sendEncryptedServerPacket(
                            serverPublicKey, new PublicKeyRequestPacket("Furki", ""));
                      }
                    }
                    case HOP -> {
                      logger.info("Routing Hop Packet...");
                      HopPacket hopPacket = (HopPacket) packet;
                      NetworkSocket hopSocket =
                          new NetworkSocket(hopPacket.getHopIp(), hopPacket.getHopPort());
                      hopSocket
                          .connectSocketAsync()
                          .whenComplete(
                              (success, throwable) -> {
                                if (!success) {
                                  logger.error(
                                      "Could not connect to Hop (" + throwable.getMessage() + ").");
                                  return;
                                }
                                hopSocket.sendMessage(hopPacket.getEncryptedMessage());
                                hopSocket.disconnectSocket();
                                logger.info("Routed Hop Packet!");
                              });
                    }
                    case MESSAGE -> {
                      MessagePacket messagePacket = (MessagePacket) packet;
                      System.out.println("Message from Server: " + messagePacket.getMessage());
                    }
                    case PUBLIC_KEY_REQUEST -> {
                      PublicKeyRequestPacket publicKeyRequestPacket =
                          (PublicKeyRequestPacket) packet;

                      connectionHandler.sendEncryptedServerPacket(
                          serverPublicKey,
                          new RouteRequestPacket(
                              "Furki",
                              cryptoUtil.encryptWithPublicKey(
                                  new MessagePacket("Hello!").toJson(),
                                  (PublicKey)
                                      cryptoUtil.toKey(
                                          publicKeyRequestPacket.getPublicKey(),
                                          CryptoUtil.KeyType.PUBLIC))));
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

  private void runServer() {
    try {
      this.serverSocket = new ServerSocket(7979);
      logger.info("Opened Server at Port " + serverSocket.getLocalPort());
      while (!serverSocket.isClosed()) {
        Socket clientSocket = serverSocket.accept();
        serverClientThreads.add(new ServerClientThread(clientSocket, this));
        logger.info("Accepted new Client.");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
