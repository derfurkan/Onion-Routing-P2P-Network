package de.furkan.network.packets.type;

import lombok.Getter;

@Getter
public class MessagePacket {

  private String message;

  public MessagePacket(String decryptedJson) {}
}
