package de.furkan.network.packets.type;

import de.furkan.network.packets.Packet;
import de.furkan.network.packets.PacketType;
import lombok.Getter;

@Getter
public class MessagePacket extends Packet {

    private final String message;

    public MessagePacket(String message) {
        super(PacketType.MESSAGE);
        this.message = message;
    }
}
