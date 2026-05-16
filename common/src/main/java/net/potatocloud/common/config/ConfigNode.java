package net.potatocloud.common.config;

import java.util.List;

public interface ConfigNode {

    String asString();

    int asInt();

    boolean asBoolean();

    List<String> asStringList();

}
