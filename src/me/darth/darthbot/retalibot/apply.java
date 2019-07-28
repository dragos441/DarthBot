package me.darth.darthbot.retalibot;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class apply extends ListenerAdapter {
	
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		if (!e.getGuild().getId().equals("393499439739961366")) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!staffapp") || args[0].equalsIgnoreCase("!apply") || args[0].equalsIgnoreCase("staffapplication")
				|| args[0].equalsIgnoreCase("!application")) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setDescription("Retali8 Staff Application Form: https://goo.gl/forms/G3FVlSrP3b232Az93");
			eb.setColor(Color.GREEN);
			
			e.getChannel().sendMessage(eb.build()).queue();
		}		
		
	}
}

