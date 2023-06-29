package br.com.stenoxz.thebridge.utils;

import br.com.stenoxz.thebridge.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class TagAPI {

    public static final Map<Player, String> TAGS = new HashMap<>();

    public static void setTag(Player player, String prefix) {
        TAGS.put(player, prefix);
    }

    public static String getPrefix(Player player) {
        String prefix;
        if ((prefix = TAGS.get(player)) == null) {
            prefix = "";
        }
        return prefix;
    }

    private static void update() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getScoreboard() == null) {
                p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            } else {
                update(p.getScoreboard());
            }
        }
        update(Bukkit.getScoreboardManager().getMainScoreboard());

    }

    private static void update(Scoreboard scoreboard) {
        for (Entry<Player, String> map : TAGS.entrySet()) {
            Player p = map.getKey();

            String prefix = map.getValue();

            Team team;

            if ((team = scoreboard.getTeam(p.getName())) == null) {
                team = scoreboard.registerNewTeam(p.getName());
            }

            if (!team.hasPlayer(p)) {
                team.addPlayer(p);
            }

            team.setPrefix(prefix);
        }
    }

    public static void startUpdate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }
}