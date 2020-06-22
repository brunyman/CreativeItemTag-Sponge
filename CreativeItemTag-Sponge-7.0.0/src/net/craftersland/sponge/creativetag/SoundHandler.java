package net.craftersland.sponge.creativetag;

import org.spongepowered.api.effect.sound.PitchModulation;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;

public class SoundHandler {
	
	@SuppressWarnings("unused")
	private CT pl;
	
	public SoundHandler(CT plugin) {
		this.pl = plugin;
	}
	
	public void sendFailedSound(Player p) {
		p.playSound(SoundTypes.BLOCK_NOTE_PLING, SoundCategories.PLAYER, p.getLocation().getPosition(), 1.0, PitchModulation.A0);
	}
	
	public void sendCompleteSound(Player p) {
		p.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, SoundCategories.PLAYER, p.getLocation().getPosition(), 1.0);
	}
	
	public void sendConfirmSound(Player p) {
		p.playSound(SoundTypes.ENTITY_ARROW_HIT_PLAYER, SoundCategories.PLAYER, p.getLocation().getPosition(), 1.0, PitchModulation.A0);
	}

}
