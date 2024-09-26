package de.furkan.network.packets.type;

import de.furkan.network.packets.Packet;
import de.furkan.network.packets.PacketType;
import lombok.Getter;

@Getter
public class RouteRequestPacket extends Packet {

  private final String clientName;
  private final String encryptedMessage;

  public RouteRequestPacket(String clientName, String encryptedMessage) {
    super(PacketType.ROUTE_REQUEST);
    this.encryptedMessage = encryptedMessage;
    this.clientName = clientName;
  }
}
