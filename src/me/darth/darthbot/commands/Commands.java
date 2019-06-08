package me.darth.darthbot.commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!commands") || args[0].equalsIgnoreCase("!help")) {
			e.getChannel().sendMessage(me.darth.darthbot.main.Main.sm.getTextChannelById("569465554079842306").getMessageById("569595704675139594").complete().getContentDisplay()).queue();
		}
	}
}
