package br.com.stenoxz.thebridge.listeners;

import br.com.stenoxz.thebridge.Main;
import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.game.stage.GameStage;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

@AllArgsConstructor
public class BlockListeners implements Listener {

    private final Main plugin;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        GamePlayer gamePlayer = plugin.getPlayerController().find(event.getPlayer().getName());

        if (gamePlayer == null){
            event.getPlayer().kickPlayer("§cConta não carregada.");
            return;
        }

        if (gamePlayer.getGame() == null) return;

        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        event.setCancelled(true);

        for (Location location : gamePlayer.getGame().getSafeConstruction()) {
            if (blockLocation.getX() == location.getX() && blockLocation.getZ() == location.getZ()) {
                event.setCancelled(false);
                break;
            }
        }

        Location redPortal = gamePlayer.getGame().getRedPortal();
        /*Location bluePortal = gamePlayer.getGame().getBluePortal();

        for (int x = -15; x < 15; x++){
            for (int z = -15; z < 15; z++){
                for (int y = -15; y < 15; y++){

                    Location redPortalLocation = new Location(redPortal.getWorld(),
                            (int) redPortal.getX() + x, (int) redPortal.getY() + y, (int) redPortal.getZ() + z);

                    Location bluePortalLocation = new Location(bluePortal.getWorld(),
                            (int) bluePortal.getX() + x, (int) bluePortal.getY() + y, (int) bluePortal.getZ() + z);

                    if (blockLocation.equals(redPortalLocation) || blockLocation.equals(bluePortalLocation)){
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }*/

        if ((int)blockLocation.getY() > ((int)redPortal.getY() + 6) || (gamePlayer.getGame().getStage() == GameStage.ENDING)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        GamePlayer gamePlayer = plugin.getPlayerController().find(event.getPlayer().getName());
        if (gamePlayer == null){
            event.getPlayer().kickPlayer("§cConta não carregada.");
            return;
        }

        if (gamePlayer.getGame() == null) return;

        event.setCancelled(true);

        for (Location location : gamePlayer.getGame().getSafeConstruction()) {
            if (event.getBlock().getLocation().getX() == location.getX() && event.getBlock().getLocation().getZ() == location.getZ()) {
                event.setCancelled(false);
            }
        }
    }
}
