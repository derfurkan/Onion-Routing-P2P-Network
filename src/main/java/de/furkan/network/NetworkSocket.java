package de.furkan.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;

public class NetworkSocket {

  private final String socketIP;
  private final int socketPort;

  private final ArrayList<MessageReceiver> messageReceivers = new ArrayList<>();
  private final ArrayList<DisconnectReceiver> disconnectReceivers = new ArrayList<>();
  private final ReentrantLock connectionLock = new ReentrantLock();
  private Thread messageHandlerThread;
  @Getter private boolean isConnected;
  @Getter private Socket socket;
  private PrintWriter printWriter;
  private BufferedReader bufferedReader;

  public NetworkSocket(String socketIP, int socketPort) {
    this.socketIP = socketIP;
    this.socketPort = socketPort;
  }

  private void buildMessageReceiverThread() {
    NetworkSocket networkSocket = this;
    this.messageHandlerThread =
        new Thread(
            () -> {
              try {
                String message;
                while (((message = bufferedReader.readLine()) != null)) {
                  String finalMessage = message;
                  messageReceivers.forEach(
                      messageReceiver -> messageReceiver.onMessage(finalMessage));
                }
                disconnectReceivers.forEach(
                    disconnectReceiver ->
                        disconnectReceiver.onDisconnect(
                            DisconnectReceiver.DisconnectType.CONNECTION_LOST));
                networkSocket.disconnectSocket();
              } catch (IOException e) {
                disconnectReceivers.forEach(
                    disconnectReceiver ->
                        disconnectReceiver.onDisconnect(
                            DisconnectReceiver.DisconnectType.CONNECTION_CLOSED));
              }
            });
  }

  public void registerNewAsyncReceiver(MessageReceiver messageReceiver) {
    messageReceivers.add(messageReceiver);
  }

  public void registerDisconnectHandler(DisconnectReceiver disconnectReceiver) {
    disconnectReceivers.add(disconnectReceiver);
  }

  public void sendMessage(String message) {
    if (!isConnected) return;
    printWriter.println(message);
  }

  public void disconnectSocket() {
    connectionLock.lock();
    if (!isConnected) return;
    messageHandlerThread.interrupt();
    printWriter.close();
    try {
      bufferedReader.close();
      socket.close();
    } catch (Exception e) {
      // Ignore
    }
    isConnected = false;
    connectionLock.unlock();
  }

  public void connectSocket() throws IOException {
    if (isConnected) return;
    socket = new Socket(socketIP, socketPort);
    printWriter = new PrintWriter(socket.getOutputStream(), true);
    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    buildMessageReceiverThread();
    messageHandlerThread.start();
    isConnected = true;
  }

  public CompletableFuture<Boolean> connectSocketAsync() {
    CompletableFuture<Boolean> connectionFuture = new CompletableFuture<>();
    if (isConnected) connectionFuture.complete(false);
    else {
      try {
        socket = new Socket(socketIP, socketPort);
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        buildMessageReceiverThread();
        messageHandlerThread.start();
        isConnected = true;
        connectionFuture.complete(true);
      } catch (IOException e) {
        connectionFuture.complete(false);
      }
    }
    return connectionFuture;
  }
}
