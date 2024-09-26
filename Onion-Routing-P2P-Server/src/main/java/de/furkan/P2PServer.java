package de.furkan;

import com.google.gson.Gson;
import de.furkan.crypt.CryptoUtil;
import de.furkan.network.ClientThread;
import de.furkan.network.packets.PacketHandler;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

@Getter
public class P2PServer {

    private final Logger logger = new Logger(P2PServer.class);
    private final CryptoUtil cryptoUtil = new CryptoUtil();
    private final PacketHandler packetHandler;
    private final Gson gson = new Gson();
    private final ServerSocket serverSocket;
    public static P2PServer p2PServer;

    private final ArrayList<ClientThread> clientThreads = new ArrayList<>();


    public P2PServer() {
        p2PServer = this;
        cryptoUtil.generateCryptoKeys();
        packetHandler = new PacketHandler(this, gson);
        try {
            this.serverSocket = new ServerSocket(7979);
            logger.info("Opened Server at Port " + serverSocket.getLocalPort());
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                clientThreads.add(new ClientThread(clientSocket,this));
                logger.info("Accepted new Client.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
