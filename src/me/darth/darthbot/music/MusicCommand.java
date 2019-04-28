package me.darth.darthbot.music;

import java.awt.Color;
import java.util.ArrayList;

import javax.sound.midi.Track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.darth.darthbot.music.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MusicCommand extends ListenerAdapter {
	
	private final MusicManager manager = new MusicManager();
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if(e.getGuild() == null) {
			return;
		}
		EmbedBuilder eb = new EmbedBuilder().setAuthor("Music", null, me.darth.darthbot.main.Main.g.getIconUrl()).setColor(Color.orange);
		if (args[0].equalsIgnoreCase("!play") || args[0].equalsIgnoreCase("!queue")) {
			try {
				String t = args[1];
				if(!e.getGuild().getAudioManager().isConnected() && !e.getGuild().getAudioManager().isAttemptingToConnect()){
					VoiceChannel voiceChannel = e.getGuild().getMember(e.getMember().getUser()).getVoiceState().getChannel();
					if(voiceChannel == null){
						eb.setDescription("Please join a voice channel!");
						e.getChannel().sendMessage(eb.build()).queue();
						return;
					}
					e.getGuild().getAudioManager().openAudioConnection(voiceChannel);
				}
			} catch (ArrayIndexOutOfBoundsException e1) {
				if (args[0].equalsIgnoreCase("!queue")) {
					eb.setColor(Color.yellow);
					ArrayList<AudioTrack> list = new ArrayList(manager.getPlayer(e.getGuild()).getListener().getTracks());
					if (manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack() != null) {
						eb.addField("Currently Playing", "**"+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title+"** "
					+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().uri, false);
					}
					for(int x = 0 ; x < list.size() ; x++) {
						AudioTrack t = list.get(x);
						long s = t.getDuration() / 1000;
						int m = 0;
						while (s > 60) {
							s=s-60;
							m=m+1;
						}
						eb.addField("#"+x+" - "+t.getInfo().title, t.getInfo().uri+" `"+m+":"+s+"`", false);
					}
					e.getChannel().sendMessage(eb.build()).queue();
					return;
				}
			}
			
			manager.loadTrack(e.getChannel(), e.getMessage().getContentRaw().replace(args[0]+" ", ""));
		}
		if (args[0].equalsIgnoreCase("!stop") || args[0].equalsIgnoreCase("!leave")) {
			manager.getPlayer(e.getGuild()).getListener().getTracks().clear();
			manager.getPlayer(e.getGuild()).getListener().nextTrack();
			e.getGuild().getAudioManager().closeAudioConnection();
			eb.setDescription("Stopped playing and left!");
			e.getChannel().sendMessage(eb.build()).queue();
		}
		if (args[0].equalsIgnoreCase("!pause")) {
			if (manager.getPlayer(e.getGuild()).getAudioPlayer().isPaused()) {
				manager.getPlayer(e.getGuild()).getAudioPlayer().setPaused(false);
				eb.setDescription("Player unpaused!");
				
			} else {
				manager.getPlayer(e.getGuild()).getAudioPlayer().setPaused(true);
				eb.setDescription("Player paused!");
			}
			e.getChannel().sendMessage(eb.build()).queue();
		}
		if (args[0].equalsIgnoreCase("!skip")) {
			if(!e.getGuild().getAudioManager().isConnected() && !e.getGuild().getAudioManager().isAttemptingToConnect()){
				eb.setDescription("There is nothing currently playing!");
				e.getChannel().sendMessage(eb.build()).queue();
				return;
			}
			String prevtrack = manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title;
			manager.getPlayer(e.getGuild()).skipTrack();
			eb.setDescription("Track **"+prevtrack+"** has been skipped, now playing **"
			+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title+"**");
			e.getChannel().sendMessage(eb.build()).queue();
		}
		if (args[0].equalsIgnoreCase("!clearqueue")) {
			if (manager.getPlayer(e.getGuild()).getListener().getTracks().isEmpty()){
				eb.setDescription("The queue is empty!");
				e.getChannel().sendMessage(eb.build()).queue();
				return;
			}
			manager.getPlayer(e.getGuild()).getListener().getTracks().clear();
			eb.setDescription("The song queue has been cleared!");
			e.getChannel().sendMessage(eb.build()).queue();
		}
		if (args[0].equalsIgnoreCase("!playing") || args[0].equalsIgnoreCase("!nowplaying")) {
			eb.setDescription("Currently playing **"+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title+"**");
			e.getChannel().sendMessage(eb.build()).queue();
		}
		
	}
}