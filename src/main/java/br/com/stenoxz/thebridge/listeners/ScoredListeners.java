package br.com.stenoxz.thebridge.listeners;

import br.com.stenoxz.thebridge.Main;
import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.game.Game;
import br.com.stenoxz.thebridge.game.stage.GameStage;
import br.com.stenoxz.thebridge.game.team.GameTeam;
import lombok.AllArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

@AllArgsConstructor
public class ScoredListeners implements Listener {

    private final Main plugin;

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        Block block = event.getFrom().getBlock();
        if (block == null || !block.hasMetadata("bridge")) return;

        GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());
        if (gamePlayer == null || gamePlayer.getGame() == null)
            return;

        Game game = gamePlayer.getGame();
        if (game == null || game.getStage() != GameStage.INGAME) return;

        GameTeam team = (GameTeam) block.getMetadata("bridge").get(0).value();
        if (team == null) return;

        if (gamePlayer.getTeam() == team) return;

        game.scored(gamePlayer);
    }
}
