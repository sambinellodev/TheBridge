package br.com.stenoxz.thebridge.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Dye;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
	
	private ItemStack itemStack;

	public ItemBuilder setMaterial(Material type) {
		this.itemStack = new ItemStack(type);
		return this;
	}

	public ItemBuilder setFast(Material type, String name, int data) {
		setMaterial(type);
		setName(name);
		setDurability(data);
		return this;
	}

	public ItemBuilder setFast(Material type, String name) {
		setMaterial(type);
		setName(name);
		return this;
	}

	public ItemBuilder setType(Material type) {
		(this.itemStack = new ItemStack(type)).setType(type);
		return this;
	}

	public ItemBuilder setAmount(int amount) {
		this.itemStack.setAmount(amount);
		return this;
	}

	public ItemBuilder setDurability(int durability) {
		this.itemStack.setDurability((short) durability);
		return this;
	}

	public ItemBuilder setName(String name) {
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.setDisplayName(name);
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder setDescription(String... desc) {
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.setLore(Arrays.asList(desc));
		this.itemStack.setItemMeta(meta);
		return this;
	}
	
	public ItemBuilder setDescription(List<String> desc) {
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.setLore(desc);
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder setDescription(String text) {
		List<String> lore = new ArrayList<String>();
		String[] split = text.split(" ");
		text = "";
		for (int i = 0; i < split.length; i++) {
			if ((ChatColor.stripColor(text).length() > 25) || (ChatColor.stripColor(text).endsWith("."))
					|| (ChatColor.stripColor(text).endsWith("!"))) {
				lore.add("§7" + text);
				if ((text.endsWith(".")) || (text.endsWith("!"))) {
					lore.add("");
				}
				text = "";
			}
			String toAdd = split[i];
			if (toAdd.contains("\n")) {
				toAdd = toAdd.substring(0, toAdd.indexOf("\n"));
				split[i] = split[i].substring(toAdd.length() + 1);
				lore.add("§7" + text + (text.length() == 0 ? "" : " ") + toAdd);
				text = "";
				i--;
			} else {
				text = text + (text.length() == 0 ? "" : " ") + toAdd;
			}
		}
		lore.add("§7" + text);
		setDescription(lore.toArray(new String[0]));
		return this;
	}

	public ItemBuilder setEnchant(Enchantment[] enchant, int[] level) {
		for (int i = 0; i < enchant.length; i++) {
			this.itemStack.addUnsafeEnchantment(enchant[i], level[i]);
		}
		return this;
	}

	public ItemBuilder setEnchant(Enchantment enchant, int level) {
		this.itemStack.addUnsafeEnchantment(enchant, level);
		return this;
	}

	public ItemBuilder setUnbreakable() {
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.spigot().setUnbreakable(true);
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder setBreakable() {
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.spigot().setUnbreakable(false);
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder setBreakable(boolean breakable) {
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.spigot().setUnbreakable(!breakable);
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder build(Player player, int... slot) {
		build(player.getInventory(), slot);
		player.updateInventory();
		return this;
	}

	public ItemBuilder build(Player player) {
		player.getInventory().addItem(new ItemStack[] { this.itemStack });
		player.updateInventory();
		return this;
	}

	public ItemBuilder build(Inventory inventory, int... slot) {
		int[] arrayOfInt;
		int j = (arrayOfInt = slot).length;
		for (int i = 0; i < j; i++) {
			int slots = arrayOfInt[i];
			inventory.setItem(slots, this.itemStack);
		}
		return this;
	}

	public ItemBuilder build(Inventory inventory) {
		inventory.addItem(new ItemStack[] { this.itemStack });
		return this;
	}

	public ItemStack getStack() {
		return this.itemStack;
	}

	public ItemMeta setName(ItemStack stack, String name) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		return meta;
	}

	public ItemBuilder setDyeColor(DyeColor color) {
		Dye dye = (Dye) this.itemStack.getData();
		dye.setColor(color);
		this.itemStack.setData(dye);
		return this;
	}

	public ItemBuilder setOwner(String owner){
		SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();
		meta.setOwner(owner);
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemStack setColor(Material material, Color color, String name) {
		ItemStack stack = new ItemStack(material);
		LeatherArmorMeta armorMeta = (LeatherArmorMeta) stack.getItemMeta();
		armorMeta.setColor(color);
		armorMeta.setDisplayName(name);
		stack.setItemMeta(armorMeta);
		return stack;
	}

	public ItemBuilder chanceItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
		return this;
	}

	public boolean checkItem(ItemStack item, String display) {
		return (item != null) && (item.getType() != Material.AIR) && (item.hasItemMeta())
				&& (item.getItemMeta().hasDisplayName())
				&& (item.getItemMeta().getDisplayName().equalsIgnoreCase(display));
	}
}
