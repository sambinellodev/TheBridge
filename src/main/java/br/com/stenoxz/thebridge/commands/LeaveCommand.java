package br.com.stenoxz.thebridge.commands;

import br.com.stenoxz.thebridge.Main;
import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class LeaveCommand extends Command {

    private final Main plugin;

    public LeaveCommand(Main plugin) {
        super("leave", "", "", Arrays.asList("sair", "lobby", "l"));

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("§cApenas jogadores podem digitar esse comando.");
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 0){
            player.sendMessage("§cUse: /" + lb);
            return false;
        }

        GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());
        if (gamePlayer == null){
            player.kickPlayer("§cConta não carregada.");
            return false;
        }

        if (gamePlayer.getGame() == null){
            player.sendMessage("§cVocê não está em um jogo.");
            return false;
        }

        gamePlayer.getGame().leaveGame(gamePlayer);
        player.sendMessage("§cVocê saiu da partida.");
        return false;
    }
}
