package de.furkan.network.packets.type;

import de.furkan.network.packets.Packet;
import de.furkan.network.packets.PacketType;
import lombok.Getter;

@Getter
public class PublicKeyPacket extends Packet {

    private final String publicKey;

    public PublicKeyPacket(String publicKey) {
        super(PacketType.PUBLIC_KEY);
        this.publicKey = publicKey;
    }
}
