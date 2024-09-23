package de.furkan.network.packets.type;

import lombok.Getter;

@Getter
public class RoutingPacket {

  private String encryptedMessage;
  private String hopIp;
  private int hopPort;
}
