package me.darth.darthbot.main;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DmReply extends ListenerAdapter {
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		if (e.getAuthor().equals(e.getJDA().getSelfUser())) {
			return;
		}
		e.getAuthor().openPrivateChannel().queue((channel) ->
	    {
	    	e.getChannel().sendMessage("Responding to DMs is a bit beyond me yet. If you're looking for Support, join the DarthBot Server! https://discord.gg/hVgXYyv").queue();
	    });
	}
}