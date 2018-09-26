package lavalink.client.io;

import net.dv8tion.jda.core.Region;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.dv8tion.jda.core.Region.*;

public enum LavalinkRegion {

    ASIA(SYDNEY, VIP_SYDNEY, SINGAPORE, VIP_SINGAPORE, JAPAN, VIP_JAPAN, HONG_KONG, SOUTH_AFRICA, VIP_SOUTH_AFRICA),

    EU(LONDON, VIP_LONDON, FRANKFURT, VIP_FRANKFURT, AMSTERDAM, VIP_AMSTERDAM, RUSSIA, EU_CENTRAL, VIP_EU_CENTRAL, EU_WEST, VIP_EU_WEST),

    US(US_CENTRAL, VIP_US_CENTRAL, US_WEST, VIP_US_WEST, US_EAST, VIP_US_EAST, US_SOUTH, VIP_US_SOUTH, BRAZIL, VIP_BRAZIL),

    /**
     * Default is empty to make it go to the old load balancing
     */
    DEFAULT();

    private final List<String> regions;
    LavalinkRegion(Region... regions) {
        this.regions = Arrays.stream(regions).map(Region::getKey).collect(Collectors.toList());
    }

    public List<String> getJDARegions() {
        return regions;
    }
}
