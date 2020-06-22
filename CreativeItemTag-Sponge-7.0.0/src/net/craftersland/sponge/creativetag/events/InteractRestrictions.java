package net.craftersland.sponge.creativetag.events;

import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;

import net.craftersland.sponge.creativetag.CT;

public class InteractRestrictions {
	
	//private CT pl;
	
	public InteractRestrictions(CT plugin) {
		//this.pl = plugin;
	}
	
	/*@Listener
	public void onInvSlot(AffectSlotEvent e) {
		//TODO Crafting
		CT.log.warn("Debug 1 - " + e.getCause() + " - " + e.getTransactions().size());
		Optional<Container> c = e.getCause().first(Container.class);
		if (c.isPresent()) {
			if (c.get().getArchetype() == InventoryArchetypes.WORKBENCH) {
				CT.log.warn("Debug 2 - " + c.get().query(SlotIndex.of("1")).peek() + " - " + c.get().query(SlotIndex.of("0")).peek());
				if (c.get().query(CraftingOutput.class).peek().isPresent()) {
					Sponge.getScheduler().createTaskBuilder().delayTicks(5L).execute(new Runnable() {

						@Override
						public void run() {
							CT.log.warn("Debug 5 - " + c + " - " + c.get().query(CraftingOutput.class).offer(pl.getTagHandler().setItemTag("Test", c.get().query(CraftingOutput.class).poll().get())).getType() + " - " + c.get().query(CraftingOutput.class).peek());
						}
						
					}).submit(pl);
				}
			}
		}
		if (e.getTransactions().isEmpty() == false) {
			SlotTransaction s = e.getTransactions().get(0);
			Optional<SlotIndex> osi = s.getSlot().getProperty(SlotIndex.class, "slotindex");
			CT.log.warn("Debug 3 - " + s + " - " + osi);
			if (osi.isPresent()) {
				int slot = osi.get().getValue();
				CT.log.warn("Debug 4 - " + slot + " - ");
			}
		}
	}*/
	
	@Listener
	public void onInvClick(ClickInventoryEvent e) {
		Optional<Player> p = e.getCause().first(Player.class);
		if (p.isPresent()) {
			if (p.get().get(Keys.GAME_MODE).get() == GameModes.SURVIVAL) {
				
			}
		}
	}

}
