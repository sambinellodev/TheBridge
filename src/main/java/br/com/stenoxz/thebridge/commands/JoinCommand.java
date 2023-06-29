package br.com.stenoxz.thebridge.commands;

import br.com.stenoxz.thebridge.Main;
import br.com.stenoxz.thebridge.account.GamePlayer;
import br.com.stenoxz.thebridge.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JoinCommand extends Command {

    private final Main plugin;

    public JoinCommand(Main plugin) {
        super("join", "", "", Collections.singletonList("entrar"));

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String lb, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("§cApenas jogadores podem digitar esse comando.");
            return false;
        }

        Player player = (Player) sender;
        if (args.length != 1){
            player.sendMessage("§cUse: /" + lb + " [game]");
            return false;
        }

        GamePlayer gamePlayer = plugin.getPlayerController().find(player.getName());
        if (gamePlayer == null){
            player.kickPlayer("§cConta não carregada.");
            return false;
        }

        if (gamePlayer.getGame() != null){
            player.sendMessage("§cVocê já está em jogo.");
            return false;
        }

        Game game = plugin.getController().find(args[0]);
        if (game == null){
            player.sendMessage("§cJogo não encontrado.");
            return false;
        }

        if (!game.joinPlayer(gamePlayer)){
            player.sendMessage("§cNão foi possível entrar na partida.");
        } else {
            player.sendMessage("§aVocê entrou na partida com sucesso.");
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1){
            List<String> list = new ArrayList<>();

            for (Game game : plugin.getController().getGames()) {
                list.add(game.getId());
            }

            return list;
        }
        return Collections.emptyList();
    }
}
