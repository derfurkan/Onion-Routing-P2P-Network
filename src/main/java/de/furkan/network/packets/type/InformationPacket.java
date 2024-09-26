package de.furkan.network.packets.type;

import de.furkan.network.packets.Packet;
import de.furkan.network.packets.PacketType;
import lombok.Getter;

@Getter
public class InformationPacket extends Packet {

  private final String clientName, publicKey;

  public InformationPacket(String clientName, String publicKey) {
    super(PacketType.INFORMATION);
    this.clientName = clientName;
    this.publicKey = publicKey;
  }
}
