package net.craftersland.sponge.creativetag;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;

import net.craftersland.sponge.creativetag.events.CreativeClickInventory;
import net.craftersland.sponge.creativetag.storage.MysqlSetup;

@Plugin(id = "creativeitemtag", name = "CreativeItemTag", version = "1.1.0", 
description = "Tags for creative items.",
url = "https://www.spigotmc.org/resources/authors/brunyman.23534/",
authors = {"brunyman"})
public class CT {
	
	@Inject
    private PluginContainer pluginContainer;
	public static Logger log;
	public static CT instance;
	
	private static ConfigHandler cH;
	private static MysqlSetup ms;
	private static TagHandler tH;
	private static SoundHandler sH;
	
	@Listener
    public void onServerStart(GameStartedServerEvent event) {
		log = pluginContainer.getLogger();
		log.info("Loading plugin " + pluginContainer.getName() + "...");
		instance = this;
		cH = new ConfigHandler(this);
		sH = new SoundHandler(this);
		tH = new TagHandler(this);
		ms = new MysqlSetup(this);
		Sponge.getEventManager().registerListeners(this, new CreativeClickInventory(this));
		CommandSpec cmd = CommandSpec.builder().description(Text.of("CreativeTag commands.")).arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("message")))).executor(new CommandHandler(this)).build();
		Sponge.getCommandManager().register(this, cmd, "creativetag");
		log.info(pluginContainer.getName() + " loaded successfully!");
	}
	
	@Listener
	public void onServerStop(GameStoppedServerEvent event) {
		log.info("Disabling plugin " + pluginContainer.getName() + "...");
		Sponge.getEventManager().unregisterPluginListeners(this);
		Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
	    Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
	    ms.closeConnection();
		log.info(pluginContainer.getName() + " is disabled!");
	}
	
	public PluginContainer getPluginContainer() {
		return pluginContainer;
	}
	public ConfigHandler getConfigHandler() {
		return cH;
	}
	public MysqlSetup getMysqlSetup() {
		return ms;
	}
	public TagHandler getTagHandler() {
		return tH;
	}
	public SoundHandler getSoundHandler() {
		return sH;
	}

}
