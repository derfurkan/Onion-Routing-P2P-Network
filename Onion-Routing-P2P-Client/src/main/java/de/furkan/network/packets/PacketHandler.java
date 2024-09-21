package de.furkan.network.packets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PacketHandler {

  // Maybe use another approach to build the packet. Generics?, Separate variable?
  public Object buildPacket(String rawMessage) {
    if (rawMessage == null) return null;

    JsonObject jsonObject;
    try {
      jsonObject = JsonParser.parseString(rawMessage).getAsJsonObject();
    } catch (Exception exception) {
      return null;
    }

    String packetMessage =
        decryptMessage(
            jsonObject
                .get("packetMessage")
                .getAsString()); // Use cryptography util, if decryption fails, return null;
    PacketType packetType = getTypeFromString(jsonObject.get("packetType").getAsString());

    if (packetMessage == null || packetType == null) return null;

    return switch (packetType) {
      case ROUTING -> new RoutingPacket(packetMessage);
      case NONE -> null;
    };
  }

    private String decryptMessage(String message) {
    return message; // This method is temporary
  }

    private PacketType getTypeFromString(String packetType) {
      if (packetType == null) return PacketType.NONE;
      try {
          return PacketType.valueOf(packetType.toUpperCase());
      } catch (IllegalArgumentException e) {
          return PacketType.NONE;
      }
  }

  private enum PacketType {
    ROUTING,
    NONE
  }
}
