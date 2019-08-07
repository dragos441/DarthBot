package me.darth.darthbot.commands;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DarthBot extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) { 
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!darthbot")) {
			EmbedBuilder eb = new EmbedBuilder().setAuthor("DarthBot", "https://discord.gg/hVgXYyv", me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getIconUrl())
					.setDescription("DarthBot is a heavily community-orientated bot, with regular and exciting updates! To give feedback and vote on existing suggestions, join the DarthBot Discord above!").setColor(Color.yellow).setTitle("Join the Discord", "https://discord.gg/nVpzgJG");
			e.getChannel().sendMessage(eb.build()).queue();
		}
	}

}
