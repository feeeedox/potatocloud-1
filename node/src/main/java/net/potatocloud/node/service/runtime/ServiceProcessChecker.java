package net.potatocloud.node.service.runtime;

import net.potatocloud.common.Closeable;
import net.potatocloud.node.service.AbstractService;

public final class ServiceProcessChecker implements Closeable {

    private static final int UPDATE_INTERVAL = 2000;

    private final Thread thread;

    public ServiceProcessChecker(AbstractService service) {
        thread = Thread.startVirtualThread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && service.isOnline()) {
                    if (!service.alive()) {
                        service.getLogger().debug("Service &a" + service.getName() + " &7seems to be offline&8...");
                        service.shutdown();
                        break;
                    }

                    Thread.sleep(UPDATE_INTERVAL);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void close() {
        thread.interrupt();
    }
}
