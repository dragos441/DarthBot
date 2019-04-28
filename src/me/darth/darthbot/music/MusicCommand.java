package me.darth.darthbot.music;

import me.darth.darthbot.music.*;
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
		if (args[0].equalsIgnoreCase("!play")) {
			if(!e.getGuild().getAudioManager().isConnected() && !e.getGuild().getAudioManager().isAttemptingToConnect()){
				VoiceChannel voiceChannel = e.getGuild().getMember(e.getMember().getUser()).getVoiceState().getChannel();
				if(voiceChannel == null){
					e.getChannel().sendMessage("Please join a voice channel!").queue();
					return;
				}
				e.getGuild().getAudioManager().openAudioConnection(voiceChannel);
			}
			
			manager.loadTrack(e.getChannel(), e.getMessage().getContentRaw().replace(args[0]+" ", ""));
			
		}
		
	}
}