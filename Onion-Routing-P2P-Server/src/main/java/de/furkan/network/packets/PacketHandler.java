package de.furkan.network.packets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.furkan.P2PServer;
import de.furkan.network.packets.type.HopPacket;
import de.furkan.network.packets.type.MessagePacket;
import de.furkan.network.packets.type.PublicKeyPacket;

public record PacketHandler(P2PServer p2PServer, Gson gson) {

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
