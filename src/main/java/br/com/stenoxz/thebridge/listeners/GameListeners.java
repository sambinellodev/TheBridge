package br.com.stenoxz.thebridge.listeners;

import br.com.stenoxz.thebridge.Main;
import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.game.Game;
import br.com.stenoxz.thebridge.game.stage.GameStage;
import br.com.stenoxz.thebridge.utils.TagAPI;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class GameListeners implements Listener {

    private final Main plugin;

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        GamePlayer gamePlayer = plugin.getPlayerController().find(event.getPlayer().getName());
        if (gamePlayer == null) {
            event.getPlayer().kickPlayer("§cConta não carregada.");
            return;
        }

        if (gamePlayer.getGame() == null) return;

        if (gamePlayer.getGame().getStage() == GameStage.STARTING) {
            event.getPlayer().sendMessage("§cVocê não pode usar o chat no Pre-Game.");
            event.setCancelled(true);
        } else {
            event.setFormat(TagAPI.getPrefix(event.getPlayer()) + event.getPlayer().getDisplayName() + "§f: §7" + event.getMessage().replaceAll("%", "%%"));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getLocation().getY() <= 0 && player.hasMetadata("bridge")) {
            Game game = (Game) player.getMetadata("bridge").get(0).value();
            if (game == null) return;

            if (game.getStage() == GameStage.STARTING) {
                event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
            } else if (game.getStage() == GameStage.INGAME) {
                GamePlayer gamePlayer = game.getPlayers().stream().filter(p -> p.getName().equalsIgnoreCase(player.getName())).findFirst().orElse(null);
                if (gamePlayer == null) {
                    event.getPlayer().kickPlayer("§cConta não carregada.");
                    return;
                }

                player.setHealth(20.0D);
                player.teleport(gamePlayer.getGame().getSpawnPoints().get(gamePlayer.getTeam()));

                gamePlayer.giveItems();

                if (player.getKiller() != null) {
                    GamePlayer killer = plugin.getPlayerController().find(player.getKiller().getName());
                    if (killer == null) return;

                    killer.setKills(killer.getKills() + 1);
                    player.getKiller().playSound(player.getKiller().getLocation(), Sound.LEVEL_UP, 1F, 1F);
                }

                gamePlayer.getGame().getPlayers().forEach(p -> p.getPlayer().sendMessage("§e" + gamePlayer.getName() + " morreu."));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        GamePlayer gamePlayer = plugin.getPlayerController().find(event.getEntity().getName());
        if (gamePlayer == null) {
            event.getEntity().kickPlayer("§cConta não carregada.");
            return;
        }

        if (gamePlayer.getGame() == null) return;

        gamePlayer.getGame().getPlayers().forEach(p -> p.getPlayer().sendMessage("§e" + gamePlayer.getName() + " morreu."));

        player.getInventory().clear();

        event.getDrops().clear();
        event.setDeathMessage(null);

        player.setHealth(20.0D);

        gamePlayer.getPlayer().teleport(gamePlayer.getGame().getSpawnPoints().get(gamePlayer.getTeam()));

        player.setVelocity(new Vector(0, 0, 0));

        gamePlayer.giveItems();

        if (player.getKiller() != null) {
            GamePlayer killer = plugin.getPlayerController().find(player.getKiller().getName());
            if (killer == null) return;

            killer.setKills(killer.getKills() + 1);
            player.getKiller().playSound(player.getKiller().getLocation(), Sound.LEVEL_UP, 1F, 1F);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());
        if (gamePlayer == null) {
            player.kickPlayer("§cConta não carregada.");
            return;
        }

        if (gamePlayer.getGame() == null) return;

        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            player.setHealth(20.0D);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            GamePlayer gamePlayer = plugin.getPlayerController().find(event.getEntity().getName());
            if (gamePlayer == null || gamePlayer.getGame() == null || gamePlayer.getTeam() == null) return;

            if (event.getDamager() instanceof Player){
                Player damager = (Player) event.getDamager();

                if (damager.getItemInHand().getType().name().contains("SWORD"))
                    event.setDamage(event.getDamage() - 2.0D);

                GamePlayer gameDamager = plugin.getPlayerController().find(damager.getName());
                if (gameDamager == null || gameDamager.getGame() == null || gameDamager.getTeam() == null) return;

                if (gamePlayer.getTeam().equals(gameDamager.getTeam()))
                    event.setCancelled(true);
            } else if (event.getDamager() instanceof Arrow){
                Arrow arrow = (Arrow) event.getDamager();
                if (arrow.getShooter() instanceof Player){
                    Player shooter = (Player) arrow.getShooter();

                    GamePlayer gameShooter = plugin.getPlayerController().find(shooter.getName());
                    if (gameShooter == null || gameShooter.getGame() == null) return;

                    if (gamePlayer.getTeam().equals(gameShooter.getTeam())) event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());
        if (gamePlayer == null) {
            player.kickPlayer("§cConta não carregada.");
            return;
        }

        if (gamePlayer.getGame() == null) return;

        event.setCancelled(true);
    }
}
