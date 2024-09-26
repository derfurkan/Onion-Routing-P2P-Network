package de.furkan.network.packets;

import com.google.gson.Gson;
import de.furkan.P2PServer;
import lombok.Getter;

@Getter
public class Packet {

    private final PacketType packetType;

    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    public String toJson() {
        return P2PServer.p2PServer.getGson().toJson(this);
    }

}
