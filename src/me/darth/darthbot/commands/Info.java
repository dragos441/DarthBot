package me.darth.darthbot.commands;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Info extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!serverinfo") || args[0].equalsIgnoreCase("!info")) {
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Color.BLUE);
			eb.setAuthor("Server Information", null, e.getGuild().getIconUrl());
			eb.addField("Server Name", ""+e.getGuild().getName(), true);
			eb.addField("Server ID", ""+e.getGuild().getId(), true);
			eb.addField("Total Members", "`"+e.getGuild().getMembers().size()+"`", true);
			eb.addField("Owner", ""+e.getGuild().getOwner().getAsMention(), true);
			eb.addField("Host Reigon", ""+e.getGuild().getRegionRaw(), true);
			eb.addField("Creation Date", e.getGuild().getCreationTime().getDayOfMonth()+"/"+e.getGuild().getCreationTime().getMonthValue()+"/"+e.getGuild().getCreationTime().getYear()
					+" @ "+e.getGuild().getCreationTime().getHour()+":"+e.getGuild().getCreationTime().getMinute(), true);
			eb.addField("Notification Level", ""+e.getGuild().getDefaultNotificationLevel(), true);
			eb.addField("Verification Level", ""+e.getGuild().getVerificationLevel(), true);
			if (e.getGuild().getDefaultChannel() == null) {
				eb.addField("Default Channel", "No Public Channels", true);
			} else {
				eb.addField("Default Channel", ""+e.getGuild().getDefaultChannel().getAsMention(), true);
			}
			e.getChannel().sendMessage(eb.build()).queue();
			
		}
	}
	
}