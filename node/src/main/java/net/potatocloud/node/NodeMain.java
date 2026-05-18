package net.potatocloud.node;

public class NodeMain {

    public static void main(String[] args) {
        final long startupTime = System.currentTimeMillis();

        final Node node = new Node(startupTime);
        node.start();

        Runtime.getRuntime().addShutdownHook(new Thread(node::shutdown));
    }
}
