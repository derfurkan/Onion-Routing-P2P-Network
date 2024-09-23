package de.furkan.network.packets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.furkan.Logger;
import de.furkan.P2PClient;
import de.furkan.network.packets.type.MessagePacket;
import de.furkan.network.packets.type.PublicKeyPacket;
import de.furkan.network.packets.type.RoutingPacket;
import lombok.Getter;

@Getter
public class PacketHandler {

  private final Logger logger = new Logger(PacketHandler.class);
  private final Gson gson = new Gson();
  private final P2PClient p2PClient;
  private PacketType packetType;
  private Object packet;

  public PacketHandler(P2PClient p2PClient) {
    this.p2PClient = p2PClient;
  }

  public void buildPacket(String rawMessage) {
    if (rawMessage == null) return;

    JsonObject jsonObject;
    String packetMessage;
    PacketType packetType;

    try {
      jsonObject = JsonParser.parseString(rawMessage).getAsJsonObject();
      packetMessage =
          p2PClient.getCryptoUtil().decrypt(jsonObject.get("packetMessage").getAsString());
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
          case ROUTING -> gson.fromJson(packetMessage, RoutingPacket.class);
          case MESSAGE -> gson.fromJson(packetMessage, MessagePacket.class);
          case PUBLIC_KEY -> gson.fromJson(packetMessage, PublicKeyPacket.class);
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
    PUBLIC_KEY,
    NONE
  }
}
