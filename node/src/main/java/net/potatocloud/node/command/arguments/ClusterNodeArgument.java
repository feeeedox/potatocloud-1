package net.potatocloud.node.command.arguments;

import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.ArgumentType;

import java.util.List;

public class ClusterNodeArgument extends ArgumentType<ClusterNode> {

    public ClusterNodeArgument(String name) {
        super(name);
    }

    @Override
    public ParseResult<ClusterNode> parse(String input) {
        final ClusterNode local = Node.getInstance().getClusterManager().localNode();

        return Node.getInstance().getClusterManager().nodes().stream()
                .filter(node -> !node.id().equals(local.id()))
                .filter(node -> node.name().equalsIgnoreCase(input))
                .findFirst()
                .map(ParseResult::success)
                .orElse(ParseResult.error("Remote cluster node &a" + input + " &7does &cnot &7exist"));
    }

    @Override
    public List<String> suggest(String input) {
        final ClusterNode local = Node.getInstance().getClusterManager().localNode();

        return Node.getInstance().getClusterManager().nodes().stream()
                .filter(n -> !n.id().equals(local.id()))
                .map(ClusterNode::name)
                .filter(name -> name.startsWith(input))
                .toList();
    }
}
