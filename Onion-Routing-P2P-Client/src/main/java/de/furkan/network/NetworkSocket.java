package de.furkan.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import lombok.Getter;

@Getter
public class NetworkSocket {

  private boolean isConnected;
  private final String socketIP;
  private final int socketPort;
  private Socket socket;
  private PrintWriter printWriter;
  private BufferedReader bufferedReader;
  private final ArrayList<Thread> receiverThreads = new ArrayList<>();

  public NetworkSocket(String socketIP, int socketPort) {
    this.socketIP = socketIP;
    this.socketPort = socketPort;
  }

  public void registerNewAsyncReceiver(MessageReceiver messageReceiver) {
    Thread thread = new Thread(() -> {
      try {
        while (bufferedReader.ready()) {
          messageReceiver.onMessage(bufferedReader.readLine());
        }
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    });
    receiverThreads.add(thread);
    thread.start();
  }

  public void sendMessage(String message) {
    printWriter.write(message);
  }

  public void disconnect() throws IOException {
    if (!isConnected) return;
    receiverThreads.forEach(Thread::interrupt); // Maybe replace with bool inside of thread for more control over thread death.
    printWriter.close();
    bufferedReader.close();
    socket.close();
    isConnected = false;
  }

  public void connect() throws IOException {
    if (isConnected) return;
    socket = new Socket(socketIP, socketPort);
    printWriter = new PrintWriter(socket.getOutputStream(), true);
    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    isConnected = true;
  }
}
