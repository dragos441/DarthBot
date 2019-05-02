package me.darth.darthbot.music;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class MusicManager {

	private final AudioPlayerManager manager = new DefaultAudioPlayerManager();
	private final Map<String, MusicPlayer> players = new HashMap<>();
	
	public MusicManager(){
		AudioSourceManagers.registerRemoteSources(manager);
		AudioSourceManagers.registerLocalSource(manager);
	}
	
	public synchronized MusicPlayer getPlayer(Guild guild){
		if(!players.containsKey(guild.getId())) players.put(guild.getId(), new MusicPlayer(manager.createPlayer(), guild));
		return players.get(guild.getId());
	}
	
	public void loadTrack(final TextChannel channel, final String source){
		EmbedBuilder eb = new EmbedBuilder().setAuthor("Music", null, me.darth.darthbot.main.Main.g.getIconUrl()).setColor(Color.red);
		eb.setDescription("Joining Channel & Configuring").setFooter("This may take a moment if you're loading a Playlist", null);
		Message msg = channel.sendMessage(eb.build()).complete();
		MusicPlayer player = getPlayer(channel.getGuild());
		channel.getGuild().getAudioManager().setSendingHandler(player.getAudioHandler());
		manager.loadItemOrdered(player, source, new AudioLoadResultHandler(){
			
			@Override
			public void trackLoaded(AudioTrack track) {
				eb.setColor(Color.green);
				long s = track.getDuration() / 1000;
				int m = 0;
				while (s >= 60) {
					s=s-60;
					m=m+1;
				}
				if (s < 10) {
					s=s*10;
				}
				eb.setDescription("Added track **"+track.getInfo().title+"** to the queue! `"+m+":"+s+"`");
				player.playTrack(track);
				msg.editMessage(eb.build()).queue();
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				eb.setColor(Color.green);
				eb.setDescription("Added songs from **"+playlist.getName()+"** to the queue:\n");
				
				int i = 0;
				for(i = 0; i < playlist.getTracks().size(); i++){
					ArrayList<AudioTrack> list = new ArrayList(manager.getPlayer(e.getGuild()).getListener().getTracks());
					AudioTrack track = playlist.getTracks().get(i);
					long s = track.getDuration() / 1000;
					int m = 0;
					while (s >= 60) {
						s=s-60;
						m=m+1;
					}
					if (s < 10) {
						s=s*10;
					}
					if (i < 20) {
						eb.addField("#"+player.getListener().getTrackSize()+" - "+track.getInfo().title, track.getInfo().uri+" `"+m+":"+s+"`", false);
					}
				}
				if (i > 20) {
					int total = i - 20;
					eb.setFooter("And "+i+" more! (Type !queue to view full queue)", null);
				}
				msg.editMessage(eb.build()).queue();
			}
			
			
			@Override
			public void noMatches() {
				eb.setDescription("That is not a valid URL!");
				msg.editMessage(eb.build()).queue();
				return;
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				eb.setDescription("Unable to play the track: (" + exception.getMessage()+")");
				msg.editMessage(eb.build()).queue();
				return;
			}
		});
	}
}