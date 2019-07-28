package me.darth.darthbot.commands;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class PingTEST extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!test")) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor("Bot Ping", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl()).setColor(Color.blue);
			eb.setDescription(":timer: Calculating Ping...");
			long start = System.currentTimeMillis();
			Message msg = e.getChannel().sendMessage(eb.build()).complete();
			long finish = System.currentTimeMillis();
			long ping = finish - start;
			eb.setDescription("The ping is: `"+ping+"ms`");
			msg.editMessage(eb.build()).queue();
		}
		
	}

}
