# Onion Routing based P2P Network

## How It Works
- When **Client A** wants to send a message to **Client B**, they do not send it directly. Instead, the message is encrypted X times and passed through other client(s) (hops). Each hop decrypts one layer to learn the next hop's IP, and the process continues until the message reaches **Client B**.
- The system prevents **Client A** from knowing **Client B's** IP address by using multiple intermediate nodes, with each node only knowing the IP of the next hop.

### **Server's Role and Encryption**
The server generates a multi-layered encrypted routing plan using [Onion Routing](https://en.wikipedia.org/wiki/Onion_routing). The hops that the server selects are primarily based on latency but can also take additional factors into account.

## Hop Compromise
There is a risk that one or more hops in the network could be compromised or malicious. Compromised hops could potentially analyze traffic, drop messages, or attempt to correlate incoming and outgoing connections. While there are methods to defend against such attacks (e.g., multi-path routing, traffic padding), these are not implemented in this PoC version.
