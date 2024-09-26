package de.furkan;

public class Core {

  public static void main(String[] args) {
    if (args.length == 0) return;
    if (args[0].equalsIgnoreCase("-client")) new PeerToPeer(false);
    else if (args[0].equalsIgnoreCase("-server")) new PeerToPeer(true);
  }
}
