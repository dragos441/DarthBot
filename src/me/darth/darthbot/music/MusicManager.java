package me.darth.darthbot.music;

import java.awt.Color;
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

	private final static AudioPlayerManager manager = new DefaultAudioPlayerManager();
	private final Map<String, MusicPlayer> players = new HashMap<>();
	
	public MusicManager(){
		AudioSourceManagers.registerRemoteSources(manager);
		AudioSourceManagers.registerLocalSource(manager);
	}
	
	public synchronized MusicPlayer getPlayer(Guild guild){
		if(!players.containsKey(guild.getId())) players.put(guild.getId(), new MusicPlayer(manager.createPlayer(), guild));
		return players.get(guild.getId());
	}
	
	
	public void loadTrack(final TextChannel channel, final String source, String iconURl, Guild g){
		EmbedBuilder eb = new EmbedBuilder().setAuthor("Music", null, iconURl).setColor(Color.red);
		eb.setDescription("Joining Channel & Configuring").setFooter("This may take a moment if you're loading a Playlist", null);
		System.out.print(source);
		eb.setFooter(null, null);
		MusicPlayer player = getPlayer(channel.getGuild());
		channel.getGuild().getAudioManager().setSendingHandler(player.getAudioHandler());
		Message msg = channel.sendMessage(eb.build()).complete();
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
				if (track.getDuration() / 1000 > 3600) {
					eb.setDescription(":no_entry: Error! Track is longer than an hour!");
				} else {
					player.playTrack(track, g);
					eb.setDescription("Added track **"+track.getInfo().title+"** to the queue! `"+m+":"+s+"`");
				}
				msg.editMessage(eb.build()).queue();
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				eb.setColor(Color.green);
				eb.setDescription("Added songs from **"+playlist.getName()+"** to the queue:\n");
				int i = 0;
				for(i = 0; i < playlist.getTracks().size(); i++){
					AudioTrack track = playlist.getTracks().get(i);
					long s = track.getDuration() / 1000;
					int m = 0;
					while (s >= 60) {
						s=s-60;
						m=m+1;
					}
					if (i < 20) {
						eb.addField("#"+player.getListener().getTrackSize()+" - "+track.getInfo().title, track.getInfo().uri+" `"+m+":"+s+"`", false);
					}
					player.playTrack(track, g);
				}
				if (i > 20) {
					int total = i - 20;
					eb.setFooter("And "+total+" more! (Type !queue to view full queue)", null);
				}
				msg.editMessage(eb.build()).queue();
			}
			
			
			@Override
			public void noMatches() {
				
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				eb.setDescription("Unable to play the track: (" + exception.getMessage()+")");
				exception.printStackTrace();
				msg.editMessage(eb.build()).queue();
				return;
			}
		});
	}
}