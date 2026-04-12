package net.potatocloud.webinterface.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.event.EventManager;
import net.potatocloud.api.event.events.player.CloudPlayerJoinEvent;
import net.potatocloud.node.Node;
import net.potatocloud.webinterface.dto.player.PlayerCountDto;
import net.potatocloud.webinterface.dto.stats.JoinPointDto;
import net.potatocloud.webinterface.dto.stats.JoinStatsDto;
import net.potatocloud.webinterface.dto.stats.StatsDto;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class StatsService {

    private static final long TWENTY_FOUR_HOURS_MS = 24 * 60 * 60 * 1000L;
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:00").withZone(ZoneOffset.UTC);

    private final CloudAPI cloudAPI;
    private final NodeService nodeService;
    private final CopyOnWriteArrayList<Long> joinTimestamps = new CopyOnWriteArrayList<>();

    public void start() {
        EventManager eventManager = cloudAPI.getEventManager();
        eventManager.on(CloudPlayerJoinEvent.class, event -> joinTimestamps.add(System.currentTimeMillis()));
    }

    public StatsDto getStats() {
        return StatsDto.builder()
                .uptime(Node.getInstance().getStartupTime())
                .groups(cloudAPI.getServiceGroupManager().getAllServiceGroups().size())
                .services(cloudAPI.getServiceManager().getOnlineServices().size())
                .onlinePlayers(cloudAPI.getPlayerManager().getOnlinePlayers().size())
                .build();
    }

    public JoinStatsDto getJoinStats() {
        return JoinStatsDto.builder()
                .total(totalJoins())
                .data(getLastTwentyFourHours())
                .build();
    }

    public PlayerCountDto getPlayerCount() {
        return PlayerCountDto.builder()
                .online(cloudAPI.getPlayerManager().getOnlinePlayers().size())
                .build();
    }

    private int totalJoins() {
        long cutoff = System.currentTimeMillis() - TWENTY_FOUR_HOURS_MS;
        return (int) joinTimestamps.stream().filter(ts -> ts >= cutoff).count();
    }

    private List<JoinPointDto> getLastTwentyFourHours() {
        long now = System.currentTimeMillis();
        long cutoff = now - TWENTY_FOUR_HOURS_MS;

        joinTimestamps.removeIf(ts -> ts < cutoff);

        Map<String, Integer> counts = new TreeMap<>();
        for (long offset = TWENTY_FOUR_HOURS_MS - 3600_000L; offset >= 0; offset -= 3600_000L) {
            String label = HOUR_FORMATTER.format(Instant.ofEpochMilli(now - offset));
            counts.putIfAbsent(label, 0);
        }

        for (long ts : joinTimestamps) {
            String label = HOUR_FORMATTER.format(Instant.ofEpochMilli(ts));
            counts.merge(label, 1, Integer::sum);
        }

        List<JoinPointDto> result = new ArrayList<>();
        counts.forEach((hour, joins) -> result.add(JoinPointDto.builder().hour(hour).joins(joins).build()));
        return result;
    }
}

