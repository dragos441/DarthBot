package me.darth.darthbot.commands;

import java.util.Date;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Ping extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().toLowerCase().split(" ");
		if (args[0].equalsIgnoreCase("!ping")) {
			long start = System.currentTimeMillis();
			Message msg = e.getChannel().sendMessage("Testing Pong...").complete();
			long ping = System.currentTimeMillis() - start;
			msg.editMessage(":ping_pong: Pong! `"+ping+"ms`").queue();
		}
		
	}
	
}
