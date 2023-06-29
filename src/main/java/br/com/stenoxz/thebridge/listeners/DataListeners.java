package br.com.stenoxz.thebridge.listeners;

import br.com.stenoxz.thebridge.Main;
import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.scoreboard.ScoreboardWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DataListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        GamePlayer gamePlayer = new GamePlayer(event.getPlayer());
        gamePlayer.setWrapper(new ScoreboardWrapper(event.getPlayer().getScoreboard()));

        Main.getInstance().getPlayerController().create(gamePlayer);

        event.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        GamePlayer gamePlayer = Main.getInstance().getPlayerController().find(event.getPlayer().getName());

        if (gamePlayer.getGame() != null)
            gamePlayer.getGame().leaveGame(gamePlayer);

        Main.getInstance().getPlayerController().remove(gamePlayer.getName());
    }
}
