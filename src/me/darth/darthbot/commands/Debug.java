package me.darth.darthbot.commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Debug extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!debug")) {
			if (me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getMember(e.getAuthor()).getRoles().contains(me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getRoleById("569463842552152094"))) {
				if (args.length == 1) {
					e.getChannel().sendMessage(":no_entry: `CHANNELS/CATEGORIES/EMOTES/ROLES/TEXTCHANNELS/VOICECHANNELS`").queue();
				} else if (args[1].toLowerCase().contains("channel")) {
					e.getChannel().sendMessage(e.getGuild().getChannels().toString()).queue();
				} else if (args[1].toLowerCase().contains("cat")) {
					e.getChannel().sendMessage(e.getGuild().getCategories().toString()).queue();
				} else if (args[1].toLowerCase().contains("emote")) {
					e.getChannel().sendMessage(e.getGuild().getEmotes().toString()).queue();
				} else if (args[1].toLowerCase().contains("role")) {
					e.getChannel().sendMessage(e.getGuild().getRoles().toString()).queue();
				} else if (args[1].toLowerCase().contains("text")) {
					e.getChannel().sendMessage(e.getGuild().getTextChannels().toString()).queue();
				} else if (args[1].toLowerCase().contains("voice")) {
					e.getChannel().sendMessage(e.getGuild().getVoiceChannels().toString()).queue();
				} else {
					e.getChannel().sendMessage(":no_entry: `CHANNELS/CATEGORIES/EMOTES/ROLES/TEXTCHANNELS/VOICECHANNELS`").queue();
				}
				
				
			}
		}
		
	}

}
