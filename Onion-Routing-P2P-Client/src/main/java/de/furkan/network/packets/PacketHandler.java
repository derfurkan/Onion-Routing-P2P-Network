package de.furkan.network.packets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.furkan.Logger;
import de.furkan.network.packets.type.MessagePacket;
import de.furkan.network.packets.type.RoutingPacket;
import lombok.Getter;

@Getter
public class PacketHandler {

  private final Logger logger = new Logger(PacketHandler.class);

  private PacketType packetType;
  private Object packet;

  public void buildPacket(String rawMessage) {
    if (rawMessage == null) return;

    JsonObject jsonObject;
    String packetMessage;
    PacketType packetType;

    try {
      jsonObject = JsonParser.parseString(rawMessage).getAsJsonObject();
      packetMessage =
          decryptMessage(
              jsonObject
                  .get("packetMessage")
                  .getAsString()); // Use cryptography util, if decryption fails, return;
      packetType = getTypeFromString(jsonObject.get("packetType").getAsString());
    } catch (Exception exception) {
      logger.warn(
          "Failed to build Packet | Exception: "
              + exception.getMessage()
              + " | rawMessage: "
              + rawMessage);
      return;
    }

    this.packetType = packetType;
    packet =
        switch (packetType) {
          case ROUTING -> new RoutingPacket(packetMessage);
          case MESSAGE -> new MessagePacket(packetMessage);
          case NONE -> null;
        };
    logger.debug(
        "Built Packet | rawMessage: " + rawMessage + " | packetType: " + packetType.name());
  }

  private String decryptMessage(String message) {
    return message; // This method is temporary
  }

  private PacketType getTypeFromString(String packetType) {
    try {
      return PacketType.valueOf(packetType.toUpperCase());
    } catch (IllegalArgumentException e) {
      return PacketType.NONE;
    }
  }

  private enum PacketType {
    ROUTING,
    MESSAGE,
    NONE
  }
}
