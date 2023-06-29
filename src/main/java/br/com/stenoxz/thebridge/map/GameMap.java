package br.com.stenoxz.thebridge.map;

import br.com.stenoxz.thebridge.Main;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;

@AllArgsConstructor
@Getter
public class GameMap {

    private final String name;

    @Setter
    private World world;

    public void load(){
        Main.getInstance().getMapController().deleteWorld(new File("example"));
        Main.getInstance().getMapController().copyWorld(new File(Main.getInstance().getDataFolder() + File.separator + "example"), new File("example"));

        this.world = Bukkit.createWorld(new WorldCreator(this.name));

        this.world.setAutoSave(false);
        this.world.setKeepSpawnInMemory(false);
        this.world.setGameRuleValue("doMobSpawning", "false");
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setGameRuleValue("mobGriefing", "false");
        this.world.setTime(0L);
    }
}
