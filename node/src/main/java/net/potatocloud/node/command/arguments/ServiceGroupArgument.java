package net.potatocloud.node.command.arguments;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.ArgumentType;

import java.util.List;

public class ServiceGroupArgument extends ArgumentType<ServiceGroup> {

    public ServiceGroupArgument(String name) {
        super(name);
    }

    @Override
    public ParseResult<ServiceGroup> parse(String input) {
        return Node.getInstance()
                .groupManager()
                .find(input)
                .map(ParseResult::success)
                .orElseGet(() -> ParseResult.error("Group &a" + input + " &7does &cnot &7exist"));
    }

    @Override
    public List<String> suggest(String input) {
        return Node.getInstance()
                .groupManager()
                .groups()
                .stream()
                .map(ServiceGroup::name)
                .filter(name -> name.startsWith(input))
                .toList();
    }
}
