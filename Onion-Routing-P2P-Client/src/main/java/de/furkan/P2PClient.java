package de.furkan;

import de.furkan.crypt.CryptoUtil;
import de.furkan.network.packets.PacketHandler;
import lombok.Getter;

@Getter
public class P2PClient {

  private final Logger logger = new Logger(PacketHandler.class);
  private final CryptoUtil cryptoUtil = new CryptoUtil();
  private final PacketHandler packetHandler;

  public P2PClient() {
    cryptoUtil.generateRSAKeyPair(4096, "RSA");
    packetHandler = new PacketHandler(this);
  }
}
