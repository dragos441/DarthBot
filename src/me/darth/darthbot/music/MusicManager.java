package me.darth.darthbot.music;

import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

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
		Message msg = channel.sendMessage("`STATUS` Joining Channel & Configuring...").complete();
		MusicPlayer player = getPlayer(channel.getGuild());
		
		channel.getGuild().getAudioManager().setSendingHandler(player.getAudioHandler());
		
		manager.loadItemOrdered(player, source, new AudioLoadResultHandler(){
			
			@Override
			public void trackLoaded(AudioTrack track) {
				msg.editMessage("Added track to the queue..."+track.getInfo().title+".").queue();
				player.playTrack(track);
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				StringBuilder builder = new StringBuilder();
				builder.append("Added all songs from **").append(playlist.getName()).append("** to the song queue:\n");
				
				for(int i = 0; i < playlist.getTracks().size(); i++){
					AudioTrack track = playlist.getTracks().get(i);
					builder.append("\n>  **#"+player.getListener().getTrackSize()+"**").append(track.getInfo().title);
					player.playTrack(track);
				}
				
				msg.editMessage(builder.toString()).queue();
			}
			
			
			@Override
			public void noMatches() {
				msg.editMessage("That is not a valid YouTube URL!").queue();
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				msg.editMessage("Unable to play the track: (" + exception.getMessage()+")").queue();
			}
		});
	}
}