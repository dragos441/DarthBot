package me.darth.darthbot.commands;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DarthBot extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!darthbot")) {
			EmbedBuilder eb = new EmbedBuilder().setAuthor("DarthBot", "https://discord.gg/hVgXYyv", me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getIconUrl())
					.setDescription("DarthBot is a heavily community-orientated bot, with regular and active updated. The bot is currently in a Beta Stage, to apply "
							+ "to have DarthBot on your server, click the link above!").setColor(Color.yellow).setTitle("Join the Discord", "https://discord.gg/hVgXYyv");
			e.getChannel().sendMessage(eb.build()).queue();
		}
	}

}
