package br.com.stenoxz.thebridge;

import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.account.controller.GamePlayerController;
import br.com.stenoxz.thebridge.commands.JoinCommand;
import br.com.stenoxz.thebridge.commands.LeaveCommand;
import br.com.stenoxz.thebridge.game.Game;
import br.com.stenoxz.thebridge.game.controller.GameController;
import br.com.stenoxz.thebridge.game.stage.GameStage;
import br.com.stenoxz.thebridge.game.team.GameTeam;
import br.com.stenoxz.thebridge.game.type.GameType;
import br.com.stenoxz.thebridge.listeners.*;
import br.com.stenoxz.thebridge.map.GameMap;
import br.com.stenoxz.thebridge.map.controller.GameMapController;
import br.com.stenoxz.thebridge.scoreboard.Sidebar;
import br.com.stenoxz.thebridge.utils.ProgressBar;
import br.com.stenoxz.thebridge.utils.TagAPI;
import br.com.stenoxz.thebridge.utils.TimeUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

@Getter
public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    private GameController controller;
    private GameMapController mapController;
    private GamePlayerController playerController;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("Plugin initialising...");

        controller = new GameController();
        mapController = new GameMapController();
        playerController = new GamePlayerController();

        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        Bukkit.getPluginManager().registerEvents(new BlockListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new BowListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new DataListeners(), this);
        Bukkit.getPluginManager().registerEvents(new GameListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new PregameListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new ScoredListeners(this), this);

        ((CraftServer) Bukkit.getServer()).getCommandMap().register("join", new JoinCommand(this));
        ((CraftServer) Bukkit.getServer()).getCommandMap().register("leave", new LeaveCommand(this));

        mapController.deleteWorld(new File("example"));
        mapController.copyWorld(new File(getDataFolder() + File.separator + "example"), new File("example"));

        WorldCreator creator = new WorldCreator("example");
        creator.generateStructures(false);

        World world = creator.createWorld();

        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setTime(0L);

        GameMap example = new GameMap("example", world);
        mapController.create(example);

        Game game = new Game("1234abcd", example, GameType.DUO);
        controller.create(game);

        TagAPI.startUpdate();

        updateScoreboard();

        getLogger().info("Plugin initialized.");
    }

    private void updateScoreboard() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    GamePlayer gamePlayer = playerController.find(player.getName());
                    if (gamePlayer == null || gamePlayer.getWrapper().getSidebar().isHided() || gamePlayer.getGame() == null)
                        continue;

                    Sidebar sidebar = gamePlayer.getWrapper().getSidebar();

                    if (gamePlayer.getGame().getStage() == GameStage.STARTING) {
                        sidebar.updateRows(rows -> {
                            rows.add("  ");
                            rows.add("§fMapa: §e" + gamePlayer.getGame().getMap().getName());
                            rows.add("§fJogadores: §e" + gamePlayer.getGame().getPlayers().size());
                            rows.add("  ");
                            rows.add(gamePlayer.getGame().getPlayers().size() == gamePlayer.getGame().getMaxPlayers() ? "§fIniciando em §e" + gamePlayer.getGame().getTime() : "§eAguardando...");
                            rows.add("  ");
                            rows.add("§ewww.mc-legacy.net");
                        });
                    } else if (gamePlayer.getGame().getStage() == GameStage.INGAME || gamePlayer.getGame().getStage() == GameStage.ENDING) {
                        sidebar.updateRows(rows -> {
                            rows.add("  ");
                            rows.add("§fTempo restante: §e" + TimeUtil.toTime(gamePlayer.getGame().getTime()));
                            rows.add("  ");
                            rows.add("§c[V] " + ProgressBar.progressBar(gamePlayer.getGame().getPointsForTeam(GameTeam.RED), 5, 5, "⬤", "§c", "§7"));
                            rows.add("§9[A] " + ProgressBar.progressBar(gamePlayer.getGame().getPointsForTeam(GameTeam.BLUE), 5, 5, "⬤", "§9", "§7"));
                            rows.add("  ");
                            rows.add("§fAbates: §e" + gamePlayer.getKills());
                            rows.add("§fPontos: §e" + gamePlayer.getPoints());
                            rows.add("  ");
                            rows.add("§fModo: §e" + gamePlayer.getGame().getType().getName());
                            rows.add("  ");
                            rows.add("§ewww.mc-legacy.net");
                        });
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);
    }
}
