package de.furkan;

import de.furkan.network.packets.PacketHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Core {

  private static final Logger logger = new Logger(PacketHandler.class);

  public static void main(String[] args) {
    logger.info("Connecting to server socket...");
    try (Socket socket = new Socket("localhost", 12345)) {

      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
