package br.com.stenoxz.thebridge.account;

import br.com.stenoxz.thebridge.Main;
import br.com.stenoxz.thebridge.game.Game;
import br.com.stenoxz.thebridge.game.team.GameTeam;
import br.com.stenoxz.thebridge.scoreboard.ScoreboardWrapper;
import br.com.stenoxz.thebridge.utils.ItemBuilder;
import br.com.stenoxz.thebridge.utils.title.Title;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class GamePlayer {

    private final String name;
    private Player player;

    @Setter
    private Game game;

    @Setter
    private boolean alive;

    @Setter
    private ScoreboardWrapper wrapper;

    @Setter
    private int kills, points;

    @Setter
    private GameTeam team;

    public GamePlayer(Player player){
        this.player = player;
        this.name = player.getName();
    }

    public Player getPlayer(){
        return this.player;
    }

    public void giveItems(){
        new BukkitRunnable(){
            @Override
            public void run() {
                Player player = getPlayer();

                player.getInventory().clear();
                player.getInventory().setArmorContents(null);

                player.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
                player.getInventory().setItem(1, new ItemStack(Material.BOW));
                player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                player.getInventory().setItem(3, new ItemStack(Material.STAINED_CLAY, 64, (short) (team == GameTeam.BLUE ? 11 : 14)));
                player.getInventory().setItem(4, new ItemStack(Material.STAINED_CLAY, 64, (short) (team == GameTeam.BLUE ? 11 : 14)));
                player.getInventory().setItem(5, new ItemStack(Material.GOLDEN_APPLE, 8));
                player.getInventory().setItem(8, new ItemStack(Material.ARROW));

                player.getInventory().setChestplate(new ItemBuilder().setColor(Material.LEATHER_CHESTPLATE,
                        (team == GameTeam.BLUE ? Color.BLUE : Color.RED), null));

                player.getInventory().setLeggings(new ItemBuilder().setColor(Material.LEATHER_LEGGINGS,
                        (team == GameTeam.BLUE ? Color.BLUE : Color.RED), null));

                player.getInventory().setBoots(new ItemBuilder().setColor(Material.LEATHER_BOOTS,
                        (team == GameTeam.BLUE ? Color.BLUE : Color.RED), null));
            }
        }.runTaskLater(Main.getInstance(), 1L);
    }

    public void sendTitle(String title, String subTitle){
        Title titleApi = new Title();
        titleApi.send(this.player, 1, 3, 1, title, subTitle);
    }
}