package de.furkan.network.packets.type;

import de.furkan.network.packets.Packet;
import de.furkan.network.packets.PacketType;
import lombok.Getter;

@Getter
public class HopPacket extends Packet {

  private final String encryptedMessage;
  private final String hopIp;
  private final int hopPort;

  public HopPacket(String encryptedMessage, String hopIp, int hopPort) {
    super(PacketType.HOP);
    this.encryptedMessage = encryptedMessage;
    this.hopIp = hopIp;
    this.hopPort = hopPort;
  }
}
