package me.darth.darthbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.entities.Guild;

public class MusicPlayer {

	int volume = 20;
	
	private final AudioPlayer audioPlayer;
	private final AudioListener listener;
	private final Guild guild;
	
	public MusicPlayer(AudioPlayer audioPlayer, Guild guild){
		this.audioPlayer = audioPlayer;
		this.guild = guild;
		listener = new AudioListener(this);
		audioPlayer.addListener(listener);
		audioPlayer.setVolume(volume);
	}
	
	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}
	
	public Guild getGuild() {
		return guild;
	}
	
	public AudioListener getListener() {
		return listener;
	}
	
	public AudioHandler getAudioHandler(){
	    return new AudioHandler(audioPlayer);
	}
	
	public synchronized void playTrack(AudioTrack track, Guild g){
		if (track != null) {
			listener.queue(track);
		} else {
			g.getAudioManager().closeAudioConnection();
		}
	}
	
	public synchronized void skipTrack(){
		listener.nextTrack();
	}
}