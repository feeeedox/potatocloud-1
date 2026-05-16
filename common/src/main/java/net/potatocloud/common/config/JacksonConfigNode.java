package net.potatocloud.common.config;

import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class JacksonConfigNode implements ConfigNode {

    private final JsonNode node;

    public JacksonConfigNode(JsonNode node) {
        this.node = node;
    }

    @Override
    public String asString() {
        return node != null ? node.asString() : null;
    }

    @Override
    public int asInt() {
        return node != null ? node.asInt() : 0;
    }

    @Override
    public boolean asBoolean() {
        return node != null && node.asBoolean();
    }

    @Override
    public List<String> asStringList() {
        if (node == null || !node.isArray()) {
            return List.of();
        }

        final List<String> list = new ArrayList<>();

        node.forEach(element -> list.add(element.asString()));
        return list;
    }
}


