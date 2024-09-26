package de.furkan;

import de.furkan.crypt.CryptoUtil;
import de.furkan.network.packets.Packet;
import de.furkan.network.packets.type.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Locale;

import lombok.SneakyThrows;

public class ServerClientThread implements Runnable {

  private final Socket clientSocket;
  private final PrintWriter printWriter;
  private final BufferedReader bufferedReader;
  private final PeerToPeer peerToPeer;
  private final Logger logger;
  private PublicKey clientPublicKey;
  private String clientName;
  private final Thread thread;

  public ServerClientThread(Socket socket, PeerToPeer peerToPeer) {
    this.clientSocket = socket;
    this.peerToPeer = peerToPeer;
    logger = peerToPeer.getLogger();
    try {
      printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
      bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.thread = new Thread(this, "Client-Thread");
    thread.start();
  }

  @SneakyThrows
  private void sendPacket(Packet packet) {
    try {
      printWriter.println(
          peerToPeer.getCryptoUtil().encryptWithPublicKey(packet.toJson(), clientPublicKey));
    } catch (Exception e) {
      peerToPeer.getLogger().error("Fatal error. Dropping client. " + e.getMessage());
      drop();
    }
  }

  @SneakyThrows
  @Override
  public void run() {
    Thread.sleep(1500);
    printWriter.println(
        new PublicKeyPacket(peerToPeer.getCryptoUtil().getPublicKeyString()).toJson());
    try {
      String message;
      while (((message = bufferedReader.readLine()) != null)) {
        String decrypted;
        decrypted = peerToPeer.getCryptoUtil().decrypt(message);

        System.out.println("Client message: " + decrypted);

        Packet packet = (Packet) peerToPeer.getPacketHandler().buildPacket(decrypted);
        switch (packet.getPacketType()) {
          case INFORMATION -> {
            InformationPacket informationPacket = (InformationPacket) packet;
            clientPublicKey =
                (PublicKey)
                    peerToPeer
                        .getCryptoUtil()
                        .toKey(informationPacket.getPublicKey(), CryptoUtil.KeyType.PUBLIC);
            clientName = informationPacket.getClientName();
            logger.info(
                "Registered Information for Client "
                    + clientSocket.getInetAddress().getHostAddress());
          }
          case ROUTE_REQUEST -> {
            RouteRequestPacket routeRequestPacket = (RouteRequestPacket) packet;
            if (peerToPeer.getServerClientThreads().size() < 3) {
              sendPacket(
                  new MessagePacket(
                      "There are less than 3 Clients connected. Hop creation not possible."));
              return;
            }
            ServerClientThread clientThread =
                peerToPeer.getServerClientThreads().stream()
                    .filter(
                        serverClientThread ->
                            serverClientThread.clientName.equals(
                                routeRequestPacket.getClientName()))
                    .findFirst()
                    .orElse(null);
            if (clientThread == null) {
              sendPacket(
                  new MessagePacket("This clientName does not exist in the client register."));
              return;
            }
            // TODO: Create Hop Packets


          }
          case PUBLIC_KEY_REQUEST -> {
            PublicKeyRequestPacket publicKeyRequestPacket = (PublicKeyRequestPacket) packet;
            ServerClientThread clientThread =
                peerToPeer.getServerClientThreads().stream()
                    .filter(
                        serverClientThread ->
                            serverClientThread.clientName.equals(
                                publicKeyRequestPacket.getClientName()))
                    .findFirst()
                    .orElse(null);
            if (clientThread == null) {
              sendPacket(
                  new MessagePacket("This clientName does not exist in the client register."));
              return;
            }
            sendPacket(
                new PublicKeyRequestPacket(
                    clientThread.clientName,
                    peerToPeer.getCryptoUtil().fromKey(clientThread.clientPublicKey)));
          }
        }
      }
      drop();
    } catch (Exception e) {
      peerToPeer.getLogger().error("Fatal error. Dropping client. " + e.getMessage());
      drop();
    }
  }

  private void drop() {
      try {
          clientSocket.close();
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      peerToPeer.getServerClientThreads().remove(this);
      thread.interrupt();
  }
}
