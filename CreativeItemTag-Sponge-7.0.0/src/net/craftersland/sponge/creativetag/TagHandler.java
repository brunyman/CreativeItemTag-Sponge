package net.craftersland.sponge.creativetag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class TagHandler {
	
	private CT pl;
	
	public TagHandler(CT plugin) {
		this.pl = plugin;
	}
	
	public boolean hasPermission(String playerName, String node) {
		Optional<Player> p = Sponge.getServer().getPlayer(playerName);
		if (p.isPresent() == true) {
			return p.get().hasPermission(node);
		}
		return false;
	}
	
	public ItemStack setItemTag(String playerName, ItemStack item) {
		List<Text> lore = null;
		Optional<List<Text>> oLore = item.get(Keys.ITEM_LORE);
		if (oLore.isPresent()) {
			lore = oLore.get();
		} else {
			lore = new ArrayList<Text>();
		}
		lore.add(pl.getConfigHandler().getTextWithColorAndPlaceholders(playerName, "General", "CreativeTag"));
		item.offer(Keys.ITEM_LORE, lore);
		//CT.log.warn("Debug 104 - " + item + " - " + hasTag(item));
		return item;
	}
	
	public ItemStack removeItemTag(ItemStack item) {
		List<Text> lore = null;
		Optional<List<Text>> oLore = item.get(Keys.ITEM_LORE);
		if (oLore.isPresent()) {
			lore = oLore.get();
		}
		if (lore != null) {
			if (lore.size() > 0) {
				for (Text t : lore) {
					if (t.toPlain().contains("Spawned in by")) {
						lore.remove(t);
						item.offer(Keys.ITEM_LORE, lore);
						return item;
					}
				}
			}
		}
		return null;
	}
	
	public boolean hasTag(ItemStack item) {
		Optional<List<Text>> oLore = item.get(Keys.ITEM_LORE);
		if (oLore.isPresent()) {
			for (Text t : oLore.get()) {
				if (t.toPlain().contains("Spawned in by")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public ItemStack copyTag(ItemStack sourceItem, ItemStack destination) {
		String tagOwner = getTagOwner(sourceItem);
		if (tagOwner != null) {
			return setItemTag(tagOwner, destination);
		}
		return null;
	}
	
	public String getTagOwner(ItemStack item) {
		//CT.log.warn("Debug 100");
		List<Text> lore = item.get(Keys.ITEM_LORE).get();
		String name = null;
		for (Text t : lore) {
			//CT.log.warn("Debug 101");
			if (t.toPlain().contains("Spawned in by")) {
				//CT.log.warn("Debug 102");
				String[] data = t.toPlain().split(" ");
				if (data.length == 4) {
					name = data[3];
					//CT.log.warn("Debug 103 - " + name);
				}
				break;
			}
		}
		return name;
	}

}
