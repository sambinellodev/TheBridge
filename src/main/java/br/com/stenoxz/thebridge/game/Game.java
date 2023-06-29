package br.com.stenoxz.thebridge.game;

import br.com.stenoxz.thebridge.Main;
import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.game.stage.GameStage;
import br.com.stenoxz.thebridge.game.team.GameTeam;
import br.com.stenoxz.thebridge.game.type.GameType;
import br.com.stenoxz.thebridge.map.GameMap;
import br.com.stenoxz.thebridge.scoreboard.ScoreboardWrapper;
import br.com.stenoxz.thebridge.scoreboard.Sidebar;
import br.com.stenoxz.thebridge.utils.TagAPI;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class Game {

    private final String id;

    @Setter
    private GameMap map;

    private final GameType type;

    private final Set<GamePlayer> players;
    private final int maxPlayers;

    @Setter
    private GameStage stage;

    private int time;

    private GamePlayer winner;

    private final Map<GameTeam, Location> spawnPoints;

    private final List<Block> cagesBlocks;
    private final List<Block> cageArea;

    private final Location bluePortal;
    private final Location redPortal;

    private final List<Location> safeConstruction;

    public Game(String id, GameMap map, GameType type) {
        this.id = id;
        this.map = map;
        this.type = type;

        bluePortal = new Location(map.getWorld(), 1652.5, 66, 1261.5);
        redPortal = new Location(map.getWorld(), 1564.5, 66, 1261.5);

        this.players = Sets.newConcurrentHashSet();

        this.maxPlayers = type == GameType.SOLO ? 2 : 4;

        this.stage = GameStage.STARTING;
        this.time = 5;

        spawnPoints = Maps.newHashMap();

        spawnPoints.put(GameTeam.BLUE, new Location(map.getWorld(), 1642.5, 79, 1261.5, 90, 0));
        spawnPoints.put(GameTeam.RED, new Location(map.getWorld(), 1575.5, 79, 1261.5, -90, 0));

        this.cagesBlocks = new ArrayList<>();
        this.cageArea = new ArrayList<>();

        generateCages(spawnPoints.get(GameTeam.BLUE));
        generateCages(spawnPoints.get(GameTeam.RED));

        setEndPortals();

        safeConstruction = new ArrayList<>();

        Location midBridge = new Location(map.getWorld(), 1608, 66, 1261);

        for (int x = -20; x < 20; x++) {
            for (int z = -20; z < 20; z++) {
                safeConstruction.add(midBridge.clone().add(x, 0, z));
            }
        }
    }

    public boolean joinPlayer(GamePlayer gamePlayer) {
        if (players.size() == maxPlayers) return false;

        try {
            players.add(gamePlayer);

            gamePlayer.getPlayer().getInventory().clear();
            gamePlayer.getPlayer().getInventory().setArmorContents(null);
            gamePlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
            gamePlayer.getPlayer().setFoodLevel(20);
            gamePlayer.getPlayer().setMaxHealth(20.0);
            gamePlayer.getPlayer().setHealth(gamePlayer.getPlayer().getMaxHealth());
            gamePlayer.getPlayer().setAllowFlight(false);

            if (gamePlayer.getPlayer().hasMetadata("bridge"))
                gamePlayer.getPlayer().removeMetadata("bridge", Main.getInstance());

            gamePlayer.getPlayer().setMetadata("bridge", new FixedMetadataValue(Main.getInstance(), this));

            if (gamePlayer.getWrapper().getSidebar().isHided())
                gamePlayer.setWrapper(new ScoreboardWrapper(gamePlayer.getPlayer().getScoreboard()));

            setHealthScoreboard(gamePlayer);
            TagAPI.setTag(gamePlayer.getPlayer(), "§7§k");

            gamePlayer.setGame(this);

            gamePlayer.getPlayer().teleport(new Location(map.getWorld(), 1586.5, 50, 1203.5));

            Sidebar sidebar = gamePlayer.getWrapper().getSidebar();

            if (!sidebar.isHided()) {
                sidebar.setTitle("§6§lBRIDGE");
                sidebar.updateRows(rows -> {
                    rows.add("  ");
                    rows.add("§fMapa: §e" + map.getName());
                    rows.add("§fJogadores: §e" + players.size());
                    rows.add("  ");
                    rows.add("§fIniciando em §e...");
                    rows.add("  ");
                    rows.add("§ewww.mc-legacy.net");
                });
            }

            if (players.size() == maxPlayers)
                startTask();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setHealthScoreboard(GamePlayer gamePlayer) {
        if (gamePlayer == null) return;

        Player player = gamePlayer.getPlayer();

        Scoreboard board = player.getScoreboard();

        Objective objective = board.getObjective("showhealth") == null ? board.registerNewObjective("showhealth", "health") : board.getObjective("showhealth");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName("§c❤");

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setScoreboard(board);
            online.setHealth(online.getHealth());
        }
    }

    private void removeHealthScoreboard(GamePlayer gamePlayer) {
        if (gamePlayer == null) return;

        Player player = gamePlayer.getPlayer();
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void leaveGame(GamePlayer gamePlayer) {
        if (gamePlayer == null) return;

        gamePlayer.setGame(null);
        gamePlayer.setKills(0);
        gamePlayer.setPoints(0);
        gamePlayer.getWrapper().getSidebar().hide();

        players.remove(gamePlayer);

        if (stage == GameStage.STARTING) {
            cancelTask();
        } else if (stage == GameStage.INGAME) {
            players.forEach(player -> player.getPlayer().sendMessage("§cUm jogador desconectou."));
            endGame();
        }

        Player player = gamePlayer.getPlayer();
        if (player == null) return;

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.teleport(Bukkit.getWorld("world").getSpawnLocation());

        if (player.hasMetadata("bridge"))
            player.removeMetadata("bridge", Main.getInstance());

        removeHealthScoreboard(gamePlayer);
        TagAPI.setTag(player, "");
    }

    private BukkitTask task;

    private void startTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (stage == GameStage.STARTING) {
                    time--;

                    if (time == 0) {
                        task = null;

                        time = 900;

                        setStage(GameStage.INGAME);

                        selectTeams();

                        teleportPlayers();
                        openCages();

                        players.forEach(player -> {
                            TagAPI.setTag(player.getPlayer(), player.getTeam() == GameTeam.BLUE ? "§9" : "§c");

                            player.giveItems();
                        });
                        return;
                    }

                    for (GamePlayer player : players) {
                        player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 1F, 1F);
                        player.sendTitle("§e" + time, "");
                    }
                    players.forEach(player -> player.getPlayer().sendMessage("§eO jogo inicia em §c" + time + " §esegundos!"));
                } else if (stage == GameStage.INGAME) {
                    time--;

                    if (time == 0) {
                        endGame();
                    }
                } else {
                    cancel();
                    task = null;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    private void cancelTask() {
        if (task == null) return;

        task.cancel();
        task = null;
        time = 5;
        players.forEach(player -> player.getPlayer().sendMessage("§cUm jogador desconectou, reiniciando o contador..."));
    }

    public void scored(GamePlayer gamePlayer) {
        gamePlayer.setPoints(gamePlayer.getPoints() + 1);

        if (gamePlayer.getPoints() == 5) {
            endGame();
            return;
        }

        players.forEach(player -> {
            player.getPlayer().sendMessage(gamePlayer.getTeam() == GameTeam.BLUE ? "§9Time Azul pontuou!" : "§cTime Vermelho pontuou!");
            player.sendTitle(gamePlayer.getTeam() == GameTeam.BLUE ? "§9Time Azul" : "§cTime Vermelho", "§7Pontuou");
        });

        generateCages(spawnPoints.get(GameTeam.BLUE));
        generateCages(spawnPoints.get(GameTeam.RED));

        players.forEach(GamePlayer::giveItems);

        teleportPlayers();

        openCages();
    }

    private void endGame() {
        setStage(GameStage.ENDING);

        players.forEach(player -> player.getPlayer().teleport(this.map.getWorld().getSpawnLocation()));

        boolean tied = false;

        if (!teamIsEmpty(GameTeam.BLUE) && !teamIsEmpty(GameTeam.RED))
            tied = getPointsForTeam(GameTeam.RED) == getPointsForTeam(GameTeam.BLUE);

        if (tied) {
            winner = null;
            players.forEach(player -> player.getPlayer().sendMessage("§aA partida não teve ganhador!"));
        } else {
            if (winner == null) {
                for (GamePlayer player : players) {
                    if (winner == null)
                        winner = player;
                    else {
                        if (player.getPoints() > winner.getPoints()) {
                            winner = player;
                        }
                    }
                }

                assert winner != null;
                players.forEach(player -> player.getPlayer().sendMessage("§a" + winner.getName() + " foi o ganhador!"));
            }
        }

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (i == 5) {
                    cancel();
                } else {
                    map.getWorld().spawnEntity(map.getWorld().getSpawnLocation(), EntityType.FIREWORK);
                }
                i++;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);

        Game game = this;

        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getInstance().getController().regenerate(new Game[]{game});
            }
        }.runTaskLater(Main.getInstance(), 10 * 20L);
    }

    public int getPointsForTeam(GameTeam team) {
        AtomicInteger i = new AtomicInteger(0);

        players.stream().filter(p -> p.getTeam().equals(team)).collect(Collectors.toList()).forEach(p -> i.getAndAdd(p.getPoints()));

        return i.get();
    }

    public GamePlayer findPlayerForTeam(GameTeam team) {
        return players.stream().filter(p -> p.getTeam().equals(team)).findFirst().orElse(null);
    }

    private boolean teamIsEmpty(GameTeam team) {
        return players.stream().noneMatch(p -> p.getTeam().equals(team));
    }

    public void generateCages(Location mid) {
        for (double x = -2.0; x <= 2.0; ++x) {
            for (double z = -2.0; z <= 2.0; ++z) {
                for (double y = 0.0; y <= 4.0; ++y) {
                    Location l = new Location(mid.getWorld(), mid.getX() + x, mid.getY() + y, mid.getZ() + z);
                    l.getBlock().setType(Material.GLASS);
                    cagesBlocks.add(l.getBlock());
                    cageArea.add(l.getBlock());
                }
            }
        }
        for (double x = -1.0; x <= 1.0; ++x) {
            for (double z = -1.0; z <= 1.0; ++z) {
                for (double y = 1.0; y <= 3.0; ++y) {
                    Location l = new Location(mid.getWorld(), mid.getX() + x, mid.getY() + y, mid.getZ() + z);
                    l.getBlock().setType(Material.AIR);
                    cagesBlocks.remove(l.getBlock());
                }
            }
        }
    }

    public void openCages() {
        new BukkitRunnable() {
            int time = 5;

            @Override
            public void run() {
                if (time == 0) {
                    for (Block b : cageArea) {
                        if (b.getType() != null && b.getType() != Material.AIR) {
                            b.setType(Material.AIR);
                        }
                        cagesBlocks.remove(b);
                    }
                    cageArea.clear();
                    cancel();
                    return;
                }

                for (GamePlayer player : players) {
                    player.sendTitle("", "§7Cabines abrem em §e" + time + "s§7...");
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 1F, 1F);
                }
                time--;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    private void selectTeams() {
        int i = 0;

        for (GamePlayer player : this.players) {
            if (i == 0 || i == 1) {
                player.setTeam(GameTeam.BLUE);
            } else {
                player.setTeam(GameTeam.RED);
            }
            i++;
        }
    }

    private void teleportPlayers() {
        players.forEach(player -> {
            player.getPlayer().teleport(spawnPoints.get(player.getTeam()).clone().add(0, 1, 0));
            player.getPlayer().setHealth(20.0D);
        });
    }

    private void setEndPortals() {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block bluePortalBlock = new Location(map.getWorld(), bluePortal.getX() + x, bluePortal.getY(), bluePortal.getZ() + z).getBlock();
                bluePortalBlock.setType(Material.ENDER_PORTAL);
                bluePortalBlock.setMetadata("bridge", new FixedMetadataValue(Main.getInstance(), GameTeam.BLUE));

                Block redPortalBlock = new Location(map.getWorld(), redPortal.getX() + x, redPortal.getY(), redPortal.getZ() + z).getBlock();
                redPortalBlock.setType(Material.ENDER_PORTAL);
                redPortalBlock.setMetadata("bridge", new FixedMetadataValue(Main.getInstance(), GameTeam.RED));
            }
        }
    }

    public void reset() {
        Main.getInstance().getController().remove(this.getId());
        Main.getInstance().getController().create(new Game(this.id, this.map, this.type));
    }
}
