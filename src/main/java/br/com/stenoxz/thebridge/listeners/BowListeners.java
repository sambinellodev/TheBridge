package br.com.stenoxz.thebridge.listeners;

import br.com.stenoxz.thebridge.Main;
import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.game.stage.GameStage;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

@AllArgsConstructor
public class BowListeners implements Listener {

    private final Main plugin;

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            if (event.getEntity().getShooter() instanceof Player) {
                Player player = (Player) event.getEntity().getShooter();
                GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());
                if (gamePlayer == null || gamePlayer.getGame() == null || gamePlayer.getGame().getStage() != GameStage.INGAME)
                    return;

                new BukkitRunnable() {
                    int i = 3;

                    @Override
                    public void run() {
                        player.setLevel(i);
                        if (i == 0) {
                            player.getInventory().setItem(8, new ItemStack(Material.ARROW));
                            cancel();
                        }

                        i--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            arrow.remove();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Arrow)) {
            Arrow arrow = (Arrow) event.getDamager();

            if ((arrow.getShooter() instanceof Player)) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();

                    GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());
                    GamePlayer gameShooter = plugin.getPlayerController().find(((Player) arrow.getShooter()).getName());

                    if (gamePlayer == null || gamePlayer.getGame() == null || gamePlayer.getTeam() == null ||
                            gameShooter == null || gameShooter.getGame() == null || gameShooter.getTeam() == null ||
                            gamePlayer.getTeam().equals(gameShooter.getTeam()))
                        return;

                    DecimalFormat format = new DecimalFormat("#.#");

                    ((Player) arrow.getShooter()).sendMessage("§b" + player.getName() + " §ftem §c" + format.format(player.getHealth() - event.getFinalDamage()) + " §fde HP.");
                }
            }
        }
    }
}
