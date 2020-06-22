package net.craftersland.sponge.creativetag;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class CommandHandler implements CommandExecutor {
	
	private CT pl;
	
	public CommandHandler(CT plugin) {
		this.pl = plugin;
	}

	@Override
	public CommandResult execute(CommandSource sender, CommandContext cmd) throws CommandException {
		if (sender instanceof Player || sender instanceof ConsoleSource) {
			String[] args = {};
			if (cmd.<String>getOne("message").isPresent() == true) {
				args = cmd.<String>getOne("message").get().split(" ");
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload") == true) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (p.hasPermission("CreativeItemTag.TagBypass") == false) {
							pl.getSoundHandler().sendFailedSound(p);
							p.sendMessage(pl.getConfigHandler().getTextWithColor("ChatMessages", "NoPermission"));
							return CommandResult.success();
						} else {
							pl.getSoundHandler().sendCompleteSound(p);
							p.sendMessage(pl.getConfigHandler().getTextWithColor("ChatMessages", "CmdReload"));
						}
					}
					pl.getConfigHandler().loadConfig();
				} else if (args[0].equalsIgnoreCase("clear") == true) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (p.hasPermission("CreativeItemTag.TagBypass") == false) {
							pl.getSoundHandler().sendFailedSound(p);
							p.sendMessage(pl.getConfigHandler().getTextWithColor("ChatMessages", "NoPermission"));
							return CommandResult.success();
						} else {
							for (Inventory in : p.getInventory()) {
								Optional<ItemStack> oItem = in.peek();
								if (oItem.isPresent()) {
									ItemStack is = pl.getTagHandler().removeItemTag(oItem.get());
									if (is != null) {
										in.set(is);
									}
								}
							}
							pl.getSoundHandler().sendCompleteSound(p);
							p.sendMessage(pl.getConfigHandler().getTextWithColor("ChatMessages", "clearTagComplete"));
						}
					} else {
						sender.sendMessage(Text.of("Only works for players!"));
					}
				} else {
					sendHelp(sender);
				}
			} else {
				sendHelp(sender);
			}
		}
		return CommandResult.success();
	}
	
	private void sendHelp(CommandSource sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			pl.getSoundHandler().sendFailedSound(p);
			p.sendMessage(pl.getConfigHandler().getTextWithColor("ChatMessages", "CmdHelp"));
		} else {
			sender.sendMessage(pl.getConfigHandler().getTextWithColor("ChatMessages", "CmdHelp"));
		}
	}

}
