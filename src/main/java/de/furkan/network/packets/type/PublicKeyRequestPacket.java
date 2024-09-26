package de.furkan.network.packets.type;

import de.furkan.network.packets.Packet;
import de.furkan.network.packets.PacketType;
import lombok.Getter;

@Getter
public class PublicKeyRequestPacket extends Packet {

  private final String clientName;
  private final String publicKey;

  public PublicKeyRequestPacket(String clientName, String publicKey) {
    super(PacketType.PUBLIC_KEY_REQUEST);
    this.publicKey = publicKey;
    this.clientName = clientName;
  }
}
