package de.furkan.network;

import com.google.gson.Gson;
import de.furkan.PeerToPeer;
import de.furkan.network.packets.Packet;
import java.security.PublicKey;
import lombok.Getter;

@Getter
public class ConnectionHandler {

  private final NetworkSocket serverSocket;
  private final Gson gson;
  private final PeerToPeer peerToPeer;

  public ConnectionHandler(PeerToPeer peerToPeer, Gson gson) {
    this.gson = gson;
    this.peerToPeer = peerToPeer;
    this.serverSocket = new NetworkSocket("127.0.0.1", 7979);
  }

  public void sendUnencryptedServerPacket(Packet packet) {
    if (!serverSocket.isConnected()) return;
    try {
      serverSocket.sendMessage(packet.toJson());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void sendEncryptedServerPacket(PublicKey publicKey, Packet packet) {
    if (!serverSocket.isConnected()) return;
    try {
      serverSocket.sendMessage(
          peerToPeer.getCryptoUtil().encryptWithPublicKey(packet.toJson(), publicKey));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
