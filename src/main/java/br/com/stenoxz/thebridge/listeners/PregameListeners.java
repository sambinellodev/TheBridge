package br.com.stenoxz.thebridge.listeners;

import br.com.stenoxz.thebridge.Main;
import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.game.stage.GameStage;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

@RequiredArgsConstructor
public class PregameListeners implements Listener {

    private final Main plugin;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());

        if (gamePlayer == null) {
            player.kickPlayer("§cConta não carregada.");
            return;
        }

        if (gamePlayer.getGame() == null) return;
        if (gamePlayer.getGame().getStage() == GameStage.STARTING || gamePlayer.getGame().getCagesBlocks().contains(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        handle(event, event.getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());
        if (gamePlayer == null) {
            player.kickPlayer("§cConta não carregada.");
            return;
        }

        if (gamePlayer.getGame() == null) return;

        if (gamePlayer.getGame().getStage() == GameStage.STARTING || gamePlayer.getGame().getStage() == GameStage.ENDING) {
            event.setCancelled(true);
        } else if (gamePlayer.getGame().getStage() == GameStage.INGAME) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());
        if (gamePlayer == null) {
            player.kickPlayer("§cConta não carregada.");
            return;
        }

        if (gamePlayer.getGame() == null) return;
        event.setFoodLevel(20);
        event.setCancelled(true);
    }

    private void handle(Cancellable event, Player player) {
        GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());
        if (gamePlayer == null) {
            player.kickPlayer("§cConta não carregada.");
            return;
        }

        if (gamePlayer.getGame() == null) return;
        if (gamePlayer.getGame().getStage() == GameStage.STARTING)
            event.setCancelled(true);
    }
}
