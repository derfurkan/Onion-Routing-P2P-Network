package de.furkan.network.packets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.furkan.PeerToPeer;
import de.furkan.network.packets.type.*;

public record PacketHandler(PeerToPeer peerToPeer, Gson gson) {

  public Object buildPacket(String decryptedJson) {
    if (decryptedJson == null) return null;

    JsonObject jsonObject;
    PacketType packetType;

    try {
      jsonObject = JsonParser.parseString(decryptedJson).getAsJsonObject();
      packetType = getTypeFromString(jsonObject.get("packetType").getAsString());

      return switch (packetType) {
        case HOP -> gson.fromJson(decryptedJson, HopPacket.class);
        case MESSAGE -> gson.fromJson(decryptedJson, MessagePacket.class);
        case PUBLIC_KEY -> gson.fromJson(decryptedJson, PublicKeyPacket.class);
        case INFORMATION -> gson.fromJson(decryptedJson, InformationPacket.class);
        case ROUTE_REQUEST -> gson.fromJson(decryptedJson, RouteRequestPacket.class);
        case PUBLIC_KEY_REQUEST -> gson.fromJson(decryptedJson,PublicKeyRequestPacket.class);
        case NONE -> null;
      };

    } catch (Exception exception) {
      return null;
    }
  }

  private PacketType getTypeFromString(String packetType) {
    try {
      return PacketType.valueOf(packetType.toUpperCase());
    } catch (IllegalArgumentException e) {
      return PacketType.NONE;
    }
  }
}
