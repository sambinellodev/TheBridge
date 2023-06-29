package br.com.stenoxz.thebridge.game.controller;

import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.game.Game;
import br.com.stenoxz.thebridge.map.GameMap;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.spigotmc.WatchdogThread;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameController {

    @Getter
    private final Set<Game> games;

    public GameController(){
        games = Sets.newHashSet();
    }

    public void create(Game model){
        games.add(model);
    }

    public Game find(String id){
        return games.stream().filter(game -> game.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public void remove(String id){
        games.remove(find(id));
    }

    public void regenerate(Game[] games){
        WatchdogThread.doStop();

        try {
            for (Game game : games){
                Set<GamePlayer> pls = new HashSet<>(game.getPlayers());

                for (GamePlayer player : pls) {
                    if (player == null) continue;
                    if (player.getGame() != game) continue;
                    game.leaveGame(player);
                }

                for (Player player : game.getMap().getWorld().getPlayers()) {
                    player.teleport(Bukkit.getWorld("world").getSpawnLocation());
                }

                if (!Bukkit.unloadWorld(game.getMap().getWorld(), false)){
                    throw new IllegalStateException("Não foi possível descarregar o mundo");
                }

                game.getMap().load();
                game.reset();
            }
            System.gc();
            WatchdogThread.doStart(10, false);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
