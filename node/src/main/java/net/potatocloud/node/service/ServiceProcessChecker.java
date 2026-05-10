package net.potatocloud.node.service;

import net.potatocloud.common.Closeable;

public final class ServiceProcessChecker implements Closeable {

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

                    Thread.sleep(2000);
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
