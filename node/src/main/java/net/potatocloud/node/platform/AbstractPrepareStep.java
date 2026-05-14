package net.potatocloud.node.platform;

import net.potatocloud.api.platform.PrepareStep;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPrepareStep implements PrepareStep {

    private final Map<String, Object> data = new HashMap<>();

    @Override
    public Map<String, Object> data() {
        return data;
    }
}
