package net.craftersland.sponge.creativetag;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public class ConfigHandler {
	
	private CT pl;
	private ConfigurationNode config;
	
	public ConfigHandler(CT plugin) {
		this.pl = plugin;
		loadConfig();
	}
	
	public void loadConfig() {
		File plugins = new File("plugins");
		if (plugins.exists() == false) {
			plugins.mkdir();
		}
		File pluginFolder = new File("plugins" + System.getProperty("file.separator") + pl.getPluginContainer().getName());
		if (pluginFolder.exists() == false) {
    		pluginFolder.mkdir();
    	}
		File configFile = new File("plugins" + System.getProperty("file.separator") + pl.getPluginContainer().getName() + System.getProperty("file.separator") + "config.yml");
		if (configFile.exists() == false) {
		CT.log.info("No config file found! Creating new one...");
			try {
				Sponge.getAssetManager().getAsset(pl, "config.yml").get().copyToDirectory(Paths.get("plugins" + System.getProperty("file.separator") + pl.getPluginContainer().getName()));
			} catch (Exception e) {
				CT.log.error("Failed to deploy config file! Error: " + e.getMessage());
				e.printStackTrace();
				return;
			}
			CT.log.info("Config file generated.");
		}
    	try {
    		CT.log.info("Loading the config file...");
    		config = YAMLConfigurationLoader.builder().setPath(Paths.get("plugins" + System.getProperty("file.separator") + pl.getPluginContainer().getName() + System.getProperty("file.separator") + "config.yml")).build().load();
    		CT.log.info("Config loaded successuffly!");
    	} catch (Exception e) {
    		CT.log.error("Could not load the config file! You need to regenerate the config! Error: " + e.getMessage());
			e.printStackTrace();
    	}
	}
	
	public String getStringWithColor(String playerName, Object... key) {
		ConfigurationNode data = config.getNode(key);
		if (data.isVirtual() == true) {
			CT.log.error("Could not locate " + key.toString() + " in the config.yml inside of the " + pl.getPluginContainer().getName() + " folder! (Try generating a new one by deleting the current)");
			return "Not Found";
		} else {
			String text = data.getString();
			if (playerName != null) {
				text = text.replace("%Name%", playerName);
			}
			return text.replaceAll("&", "§");
		}
	}
	
	public Text getTextWithColor(Object... key) {
		ConfigurationNode data = config.getNode(key);
		if (data.isVirtual() == true) {
			CT.log.error("Could not locate " + key.toString() + " in the config.yml inside of the " + pl.getPluginContainer().getName() + " folder! (Try generating a new one by deleting the current)");
			return Text.of();
		} else {
			return TextSerializers.FORMATTING_CODE.deserialize(data.getString());
		}
	}
	
	public Text getTextWithColorAndPlaceholders(String name, Object... key) {
		ConfigurationNode data = config.getNode(key);
		if (data.isVirtual() == true) {
			CT.log.error("Could not locate " + key.toString() + " in the config.yml inside of the " + pl.getPluginContainer().getName() + " folder! (Try generating a new one by deleting the current)");
			return Text.of();
		} else {
			String text = data.getString();
			if (name != null) {
				text = text.replace("%Name%", name);
			}
			return TextSerializers.FORMATTING_CODE.deserialize(text);
		}
	}
	
	public List<String> getStringList(Object... key) {
		ConfigurationNode data = config.getNode(key);
		if (data.isVirtual() == true) {
			CT.log.error("Could not locate " + key.toString() + " in the config.yml inside of the " + pl.getPluginContainer().getName() + " folder! (Try generating a new one by deleting the current)");
			return null;
		} else {
			return data.getList(stringTransformer);
		}
	}
	
	Function<Object,String> stringTransformer = new Function<Object,String>() {
	    @Override
	    public String apply(Object input) {
	        if (input instanceof String) {
	            return (String) input;
	        } else {
	            return null;
	        }
	    }
	};
	
	public String getString(Object... key) {
		ConfigurationNode data = config.getNode(key);
		if (data.isVirtual() == true) {
			CT.log.error("Could not locate " + key.toString() + " in the config.yml inside of the " + pl.getPluginContainer().getName() + " folder! (Try generating a new one by deleting the current)");
			return "errorCouldNotLocateInConfigYml:" + key.toString();
		} else {
			return data.getString();
		}
	}
	
	public Integer getInteger(Object... key) {
		ConfigurationNode data = config.getNode(key);
		if (data.isVirtual() == true) {
			CT.log.error("Could not locate " + key.toString() + " in the config.yml inside of the " + pl.getPluginContainer().getName() + " folder! (Try generating a new one by deleting the current)");
			return null;
		} else {
			return data.getInt();
		}
	}
	
	public Double getDouble(Object... key) {
		ConfigurationNode data = config.getNode(key);
		if (data.isVirtual() == true) {
			CT.log.error("Could not locate " + key.toString() + " in the config.yml inside of the " + pl.getPluginContainer().getName() + " folder! (Try generating a new one by deleting the current)");
			return null;
		} else {
			return data.getDouble();
		}
	}
	
	public Boolean getBoolean(Object... key) {
		ConfigurationNode data = config.getNode(key);
		if (data.isVirtual() == true) {
			CT.log.error("Could not locate " + key.toString() + " in the config.yml inside of the " + pl.getPluginContainer().getName() + " folder! (Try generating a new one by deleting the current)");
			return null;
		} else {
			return data.getBoolean();
		}
	}

}
