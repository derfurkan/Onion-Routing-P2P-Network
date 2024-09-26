package de.furkan.network.packets;

import de.furkan.PeerToPeer;
import lombok.Getter;

@Getter
public class Packet {

  private final PacketType packetType;

  public Packet(PacketType packetType) {
    this.packetType = packetType;
  }

  public String toJson() {
    return PeerToPeer.peerToPeer.getGson().toJson(this);
  }
}
