package net.potatocloud.node.utils;

import oshi.ffm.SystemInfo;

public final class HardwareUtils {

    private HardwareUtils() {
    }

    private static final SystemInfo INFO = new SystemInfo();

    public static int getCpuCores() {
        return INFO.getHardware().getProcessor().getPhysicalProcessorCount();
    }

    public static int getRam() {
        return (int) (INFO.getHardware().getMemory().getTotal() / (1024.0 * 1024 * 1024));
    }

    public static boolean isLowHardware() {
        return getCpuCores() < 4 || getRam() < 4;
    }
}
