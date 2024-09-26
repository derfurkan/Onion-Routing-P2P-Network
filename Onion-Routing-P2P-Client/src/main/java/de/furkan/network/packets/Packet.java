package de.furkan.network.packets;

import de.furkan.P2PClient;
import lombok.Getter;

@Getter
public class Packet {

    private final PacketType packetType;

    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    public String toJson() {
        return P2PClient.p2PClient.getGson().toJson(this);
    }

}
