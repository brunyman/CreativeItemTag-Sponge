package net.craftersland.sponge.creativetag.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import net.craftersland.sponge.creativetag.CT;

public class CreativeClickInventory {
	
	private CT pl;
	
	public CreativeClickInventory(CT plugin) {
		this.pl = plugin;
	}
	
	@Listener
	public void onItemDrop(DropItemEvent e) {
		Optional<Player> p = e.getCause().first(Player.class);
		if (p.isPresent()) {
			if (p.get().get(Keys.GAME_MODE).get() == GameModes.CREATIVE && e.getCause().toString().contains("sponge:dropped_item") && p.get().hasPermission("CreativeItemTag.TagBypass") == false) {
				e.setCancelled(true);
				pl.getSoundHandler().sendFailedSound(p.get());
				p.get().sendMessage(pl.getConfigHandler().getTextWithColor("ChatMessages", "creativeDrop"));
				
				//CT.log.warn("Debug 1 - " + e.getCause() + " - " + e.getCause().containsType(SpawnTypes.class));
			}
		}
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
	public void onInvClickCreative(ClickInventoryEvent.Creative e) {
		Optional<Player> p = e.getCause().first(Player.class);
		if (p.isPresent()) {
			if (p.get().hasPermission("CreativeItemTag.TagBypass")) return;
			Optional<ItemStack> is = p.get().getItemInHand(HandTypes.MAIN_HAND);
			if (is.isPresent()) {
				for (String s : pl.getConfigHandler().getStringList("General", "NbtCopyRestrict")) {
					if (is.get().getType().getId().matches(s)) {
						p.get().setItemInHand(HandTypes.MAIN_HAND, ItemStack.builder().itemType(is.get().getType()).build());
						return;
					}
				}
			}
		}
	}
	
	@Listener
	public void onInvClick(ClickInventoryEvent e) {
		Optional<Player> p = e.getCause().first(Player.class);
		//Optional<Container> c = e.getCause().first(Container.class);
		//CT.log.warn("Debug 0 - " + e.getCause() + " - " + p + " - " + e.getCursorTransaction().getOriginal() + " - " + e.getCursorTransaction().getFinal() + " - " );
		if (p.isPresent()) {
			if (p.get().hasPermission("CreativeItemTag.TagBypass")) return;
			//CT.log.warn("Debug 1 - ");
			if (p.get().get(Keys.GAME_MODE).get() == GameModes.CREATIVE && e.getTargetInventory().getArchetype() != InventoryArchetypes.PLAYER) {
				//Inventory click for non player inventory
				//CT.log.warn("Debug 2 - " + e.getTargetInventory().getArchetype().getName());
				if (e.getTransactions().isEmpty() == false) {
					//SlotTransaction s = e.getTransactions().get(0);
					for (SlotTransaction s : e.getTransactions()) {
						//CT.log.warn("Debug 3 - " + s.getSlot().peek() + " - " + s.getSlot().getInventoryProperty(SlotIndex.class));
						Sponge.getScheduler().createTaskBuilder().delayTicks(1L).execute(new Runnable() {

							@Override
							public void run() {
								Optional<ItemStack> ois = s.getSlot().peek();
								//CT.log.warn("Debug 4 - " + s.getSlot().getInventoryProperty(SlotIndex.class));
								if (ois.isPresent()) {
									if (pl.getTagHandler().hasTag(ois.get()) == false) {
										ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), ois.get());
										s.getSlot().set(is);
										//CT.log.warn("Debug 5 - " + s.getSlot().getInventoryProperty(SlotIndex.class).get().getValue());
									}
								}
							}
							
						}).submit(pl);
					}
				}
			} else if (p.get().get(Keys.GAME_MODE).get() == GameModes.CREATIVE && e.getTargetInventory().getArchetype() == InventoryArchetypes.PLAYER) {
				//CT.log.warn("Debug 1 - " + p.get().getInventory().totalItems() + " - ");
				int initalItemsCount = p.get().getInventory().totalItems();
				//Map<Inventory, ItemStack> invMap = new HashMap<Inventory, ItemStack>();
				List<ItemStack> invMap = new ArrayList<ItemStack>();
				for (Inventory invs : p.get().getInventory().slots()) {
					Optional<ItemStack> oi = invs.peek();
					if (oi.isPresent()) {
						//CT.log.warn("Debug 3 - " );
						invMap.add(oi.get());
					}
				}
				
				Sponge.getScheduler().createTaskBuilder().delayTicks(1L).execute(new Runnable() {

					@Override
					public void run() {
						int afterItemsCount = p.get().getInventory().totalItems();
						//CT.log.warn("Debug 4 - " + afterItemsCount + " - " + initalItemsCount);
						if (afterItemsCount >= initalItemsCount && invMap.isEmpty() == false) {
							//CT.log.warn("Debug 5 - " );
							//for (int ii = 0; ii < p.get().getInventory().size(); ii++) {
							for (Inventory invSlot : p.get().getInventory().slots()) {
								//Inventory invSlot = p.get().getInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(ii)));
								Optional<ItemStack> oi2 = invSlot.peek();
								//CT.log.warn("Debug 6 - " + oi2);
								if (oi2.isPresent()) {
									boolean setTag = true;
									local: for (ItemStack is : invMap) {
										if (is.equalTo(oi2.get())) {
											setTag = false;
											//CT.log.warn("Debug 6 - D" );
											break local;
										}
									}
									
									if (setTag == true) {
										if (pl.getTagHandler().hasTag(oi2.get()) == false) {
											ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), oi2.get());
											invSlot.set(is);
											//CT.log.warn("Debug 11 - TAG " );
										}
										//CT.log.warn("Debug 12 -" );
									}
								}
							}
						}
						invMap.clear();
					}
					
				}).submit(pl);
			}
		}
	}
	
	/*@Listener
	public void onInvClickCreative(ClickInventoryEvent.Creative e) {
		CT.log.warn("Debug 0 - " + e.getCursorTransaction().getOriginal() + " - " + e.getCursorTransaction().getFinal());
		Optional<Player> p = e.getCause().first(Player.class);
		if (p.isPresent()) {
			if (p.get().hasPermission("CreativeItemTag.TagBypass") == false) {
				CT.log.warn("Debug 2 - " + e.getTransactions().size() + " - " + e.getTargetInventory().getArchetype().getName());
				for (SlotTransaction st : e.getTransactions()) {
					CT.log.warn("Debug 3 - " + st.getDefault() + " - " + st.getOriginal() + " - " + st.getFinal());
					Sponge.getScheduler().createTaskBuilder().delayTicks(1L).execute(new Runnable() {

						@Override
						public void run() {
							Optional<ItemStack> item = st.getSlot().peek();
							CT.log.warn("Debug 4 - " + st.getSlot().getInventoryProperty(SlotIndex.class));
							if (item.isPresent()) {
								if (pl.getTagHandler().hasTag(item.get()) == false) {
									ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), item.get());
									st.getSlot().set(is);
									CT.log.warn("Debug 5 - ");
								}
							}
						}
						
					}).submit(pl);
				}
			}
		}
		//Only works for player inventory
		CT.log.warn("Debug 0 - " + e.getCursorTransaction().getOriginal() + " - " + e.getCursorTransaction().getFinal());
		Optional<Player> p = e.getCause().first(Player.class);
		if (p.isPresent()) {
			if (p.get().hasPermission("CreativeItemTag.TagBypass")) return;
			CT.log.warn("Debug 1 - " + e.getTransactions().size() + " - " + e.getTargetInventory().getArchetype().getName() + " - ");
			if (e.getTransactions().isEmpty() == false) {
				if (e.getTransactions().size() == 1) {
					SlotTransaction s = e.getTransactions().get(0);
					CT.log.warn("Debug 2 - " + s.getDefault() + " - " + s.getFinal() + " - " + s.getOriginal());
					SlotIndex si = s.getSlot().getInventoryProperty(SlotIndex.class).get();
					int slot = si.getValue();
					if (s.getFinal().toString().contains("Name=none") == true && s.getOriginal().toString().contains("Name=none") == true || s.getFinal().toString().contains("Name=none") == false && s.getOriginal().toString().contains("Name=none") == false) {
						Sponge.getScheduler().createTaskBuilder().delayTicks(1L).execute(new Runnable() {

							@Override
							public void run() {
								Inventory inv = p.get().getInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(si));
								Optional<ItemStack> ois = e.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(si)).peek();
								CT.log.warn("Debug 3 - ");
								if (ois.isPresent() && slot <= 35) {
									if (pl.getTagHandler().hasTag(inv.peek().get()) == false) {
										ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), inv.peek().get());
										inv.set(is);
									}
								} else if (p.get().getBoots().isPresent() && slot == 36) {
									if (pl.getTagHandler().hasTag(p.get().getBoots().get()) == false) {
										ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), p.get().getBoots().get());
										p.get().setBoots(is);
									}
								} else if (p.get().getLeggings().isPresent() && slot == 37) {
									if (pl.getTagHandler().hasTag(p.get().getLeggings().get()) == false) {
										ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), p.get().getLeggings().get());
										p.get().setLeggings(is);
									}
								} else if (p.get().getChestplate().isPresent() && slot == 38) {
									if (pl.getTagHandler().hasTag(p.get().getChestplate().get()) == false) {
										ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), p.get().getChestplate().get());
										p.get().setChestplate(is);
									}
								} else if (p.get().getHelmet().isPresent() && slot == 39) {
									if (pl.getTagHandler().hasTag(p.get().getHelmet().get()) == false) {
										ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), p.get().getHelmet().get());
										p.get().setHelmet(is);
									}
								} else if (p.get().getItemInHand(HandTypes.OFF_HAND).isPresent() && slot == 40) {
									if (pl.getTagHandler().hasTag(p.get().getItemInHand(HandTypes.OFF_HAND).get()) == false) {
										ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), p.get().getItemInHand(HandTypes.OFF_HAND).get());
										p.get().setItemInHand(HandTypes.OFF_HAND, is);
									}
								}
							}
							
						}).submit(pl);
					} else if (s.getFinal().toString().contains("Name=none") == true && s.getOriginal().toString().contains("Name=none") == false) {
						Sponge.getScheduler().createTaskBuilder().delayTicks(1L).execute(new Runnable() {

							@Override
							public void run() {
								Inventory hotBarInv = p.get().getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
								Inventory fis = hotBarInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(si));
								CT.log.warn("Debug 4 - " + fis.peek() + " - " + slot + " - " + hotBarInv.getArchetype().getName() + " - "  + e.getTargetInventory().getArchetype().getName());
								if (slot < 9) {
									CT.log.warn("Debug 5 - ");
									if (fis.peek().isPresent()) {
										if (pl.getTagHandler().hasTag(fis.peek().get()) == false) {
											ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), fis.peek().get());
											fis.set(is);
										}
									}
								} else {
									Inventory inv = p.get().getInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(si));
									CT.log.warn("Debug 6 - " + inv.peek());
									if (inv.peek().isPresent()) {
										if (pl.getTagHandler().hasTag(inv.peek().get()) == false) {
											CT.log.warn("Debug 7 - ");
											ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), inv.peek().get());
											inv.set(is);
										}
									}
								}
							}
							
						}).submit(pl);
					}
				} else {
					for (SlotTransaction st : e.getTransactions()) {
						CT.log.warn("Debug 3 - " + st.getDefault() + " - " + st.getOriginal() + " - " + st.getFinal());
						Sponge.getScheduler().createTaskBuilder().delayTicks(1L).execute(new Runnable() {

							@Override
							public void run() {
								Optional<ItemStack> item = st.getSlot().peek();
								CT.log.warn("Debug 4 - " + item);
								if (item.isPresent()) {
									if (pl.getTagHandler().hasTag(item.get()) == false) {
										ItemStack is = pl.getTagHandler().setItemTag(p.get().getName(), item.get());
										st.getSlot().set(is);
										CT.log.warn("Debug 5 - ");
									}
								}
							}
							
						}).submit(pl);
					}
				}
			} else {
				e.setCancelled(true);
			}
		}
	}*/

}
