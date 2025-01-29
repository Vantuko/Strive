package com.badstudio.plugin.minigames.spleef.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.UUID;

public class ScoreboardManager {
    private final Scoreboard scoreboard;
    private final Objective objective;

    public ScoreboardManager() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("spleef", "dummy", ChatColor.AQUA + "Puntuación Spleef");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void updateScore(Player player, int score) {
        Score scoreEntry = objective.getScore(player.getName());
        scoreEntry.setScore(score);
    }

    public void addScore(Player player, int amount) {
        Score scoreEntry = objective.getScore(String.valueOf(player));
        int currentScore = scoreEntry.getScore();
        scoreEntry.setScore(currentScore + amount);
    }

    public int getScore(Player player) {
        Score scoreEntry = objective.getScore(String.valueOf(player));
        return scoreEntry.getScore();
    }

    public void setScoreboard(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void resetScoreboard() {
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
    }
}