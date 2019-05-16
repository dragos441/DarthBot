package me.darth.darthbot.main;

import java.util.List;

import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class PublicRooms extends ListenerAdapter {

	public static Category category = me.darth.darthbot.main.Main.jda.getGuildById("568849490425937940").getCategoryById("569616194726920202");
	
	public static boolean availableChannel(Channel c) {
		List<VoiceChannel> channels = category.getVoiceChannels();
		for(int x = 0 ; x < channels.size() ; x++) {
			if (channels.get(x).getMembers().isEmpty() && !channels.get(x).equals(c)) {
				return true;
			}
		}
		return false;
	}
	
	public static int channelNum() {
		List<VoiceChannel> channels = category.getVoiceChannels();
		int num = 1;
		while (true) {
			int firstnum = num;
			for(int x = 0 ; x < channels.size() ; x++) {
				if (Integer.parseInt(channels.get(x).getName().replace("Voice #", "")) == num) {
					num++;
				}
			}
			if (firstnum == num) {
				return num;
			}
		}
	}
	
	public void deleteChannel(Channel c) {
		if (availableChannel(c) == true) {
			List<VoiceChannel> channels = category.getVoiceChannels();
			Channel toDelete = null;
			for(int x = 0 ; x < channels.size() ; x++) {
				if (channels.get(x).getMembers().isEmpty()) {
					if (toDelete == null || Integer.parseInt(channels.get(x).getName().replace("Voice #", "")) > Integer.parseInt(toDelete.getName().replace("Voice #", ""))) {
						toDelete = channels.get(x);
					}
				}
			}
			if (toDelete != null) {
				toDelete.delete().reason("[Auto] Public Voice Channel Remove").queue();
			}
		}
	}
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		if (!category.getVoiceChannels().contains(e.getChannelJoined())) {
			return;
		}
		boolean availableChannel = availableChannel(e.getChannelJoined());
		if (!availableChannel) {
			category.createVoiceChannel("Voice #"+channelNum()).reason("[Auto] Public Voice Channel Add").queue();
		}
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		
		if (!category.getVoiceChannels().contains(e.getChannelLeft())) {
			return;
		}
		deleteChannel(e.getChannelLeft());
		
	}
	
	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
		if (category.getVoiceChannels().contains(e.getChannelJoined())) {
			boolean availableChannel = availableChannel(e.getChannelJoined());
			if (!availableChannel) {
				category.createVoiceChannel("Voice #"+channelNum()).reason("[Auto] Public Voice Channel Add").queue();
			}
		}
		if (category.getVoiceChannels().contains(e.getChannelLeft())) {
			deleteChannel(e.getChannelLeft());
		}
		
	}
	
}
