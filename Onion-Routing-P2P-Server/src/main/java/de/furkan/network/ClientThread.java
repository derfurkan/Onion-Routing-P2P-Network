package de.furkan.network;

import de.furkan.P2PServer;
import de.furkan.network.packets.Packet;
import de.furkan.network.packets.type.PublicKeyPacket;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread implements Runnable {

    private final Socket clientSocket;
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;
    private final P2PServer p2PServer;

    public ClientThread(Socket socket, P2PServer p2PServer) {
        this.clientSocket = socket;
        this.p2PServer = p2PServer;
        try {
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Thread thread = new Thread(this, "Client-Thread");
        thread.start();
    }


    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(1500);
        printWriter.println(new PublicKeyPacket(p2PServer.getCryptoUtil().getPublicKeyString()).toJson());
        try {
            String message;
            while (((message = bufferedReader.readLine()) != null)) {
                String decrypted;
                decrypted = p2PServer.getCryptoUtil().decrypt(message);
                Packet packet = (Packet) p2PServer.getPacketHandler().buildPacket(decrypted);

            }
        } catch (Exception e) {
            p2PServer.getLogger().error("Fatal error. Dropping client. " + e.getMessage());
            clientSocket.close();
        }
    }
}
