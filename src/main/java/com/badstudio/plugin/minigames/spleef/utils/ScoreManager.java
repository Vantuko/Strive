package com.badstudio.plugin.minigames.spleef.utils;

import java.util.HashMap;
import java.util.UUID;

public class ScoreManager {
    private final HashMap<UUID, Integer> scores = new HashMap<>();

    public void addScore(UUID playerId, int points) {
        scores.put(playerId, scores.getOrDefault(playerId, 0) + points);
    }

    public int getScore(UUID playerId) {
        return scores.getOrDefault(playerId, 0);
    }

    public void resetScores() {
        scores.clear();
    }
}
