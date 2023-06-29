package br.com.stenoxz.thebridge.account.controller;

import br.com.stenoxz.thebridge.account.GamePlayer;

import java.util.HashSet;
import java.util.Set;

public class GamePlayerController {

    private final Set<GamePlayer> players;

    public GamePlayerController(){
        players = new HashSet<>();
    }

    public void create(GamePlayer model){
        players.add(model);
    }

    public GamePlayer find(String name){
        return players.stream().filter(player -> player.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void remove(String name){
        players.remove(find(name));
    }
}
