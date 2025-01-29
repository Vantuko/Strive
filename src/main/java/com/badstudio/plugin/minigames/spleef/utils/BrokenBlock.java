package com.badstudio.plugin.minigames.spleef.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BrokenBlock {
    private final Location location;
    private final Player breaker;
    private final long breakTime;

    public BrokenBlock(Location location, Player breaker) {
        this.location = location;
        this.breaker = breaker;
        this.breakTime = System.currentTimeMillis();
    }

    public Location getLocation() {
        return location;
    }

    public Player getBreaker() {
        return breaker;
    }

    public long getBreakTime() {
        return breakTime;
    }
}
