package br.com.stenoxz.thebridge.utils.title;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {

    public void send(Player player, int fadeInTime, int showTime, int fadeOutTime, String title, String subtitle) {
        try {
            IChatBaseComponent resetTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetResetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, resetTitle, fadeInTime, showTime, fadeOutTime);

            IChatBaseComponent mainTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetMainTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, mainTitle, fadeInTime, showTime, fadeOutTime);

            IChatBaseComponent subTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle packetSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subTitle, fadeInTime, showTime, fadeOutTime);

            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetResetTitle);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetMainTitle);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetSubTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
