package me.darth.darthbot.commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Discord extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!darthbot")) {
			e.getChannel().sendMessage("**Join the DarthBot Official Server here:** https://discord.gg/hVgXYyv").queue();
		}
	}

}
