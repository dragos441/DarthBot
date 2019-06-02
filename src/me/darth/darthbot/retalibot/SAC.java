package me.darth.darthbot.retalibot;

import java.awt.Color;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SAC extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getChannel().equals(e.getGuild().getTextChannelById("543562394702446593"))) {
			if (e.getMember().getUser().equals(e.getJDA().getSelfUser())) {
				return;
			}
			e.getMessage().addReaction(e.getGuild().getEmoteById("543564724021755916")).queue();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.getMessage().addReaction(e.getGuild().getEmoteById("543567328311508995")).queue();
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getChannel().equals(e.getGuild().getTextChannelById("543562394702446593"))) {
			if (e.getUser().equals(e.getJDA().getSelfUser())) {
				return;
			}
			if (!e.getMember().getRoles().contains(e.getGuild().getRoleById("557702978455339009"))) {
				e.getTextChannel().removeReactionById(e.getMessageId(), e.getReaction().getReactionEmote().getEmote(), e.getMember().getUser()).queue();
				return;
			}
			if (e.getReactionEmote().getId().equals("543564724021755916")) {
				e.getGuild().getController().addSingleRoleToMember(e.getChannel().getMessageById(e.getMessageId()).complete().getMember(), e.getGuild().getRoleById("543586002657345536")).reason("#support-a-creator auto role (Approved Evidence").queue();
				e.getChannel().sendMessage("<:tick:543564724021755916> "+e.getChannel().getMessageById(e.getMessageId()).complete().getMember().getAsMention()+", "
					+ "your evidence has been approved by "+e.getMember().getAsMention()+" and you have been given the **__Supporter__** role!").complete().delete().queueAfter(5, TimeUnit.MINUTES);
				e.getGuild().getTextChannelById("543591509639823390").sendMessage(
					e.getChannel().getMessageById(e.getMessageId()).complete().getMember().getAsMention()+", Thanks for supporting <@537750963138723840>, welcome to the cool club!").queue();
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.green);
				eb.setAuthor("Support-A-Creator Evidence Approved", null, e.getGuild().getIconUrl());
				eb.setDescription(e.getChannel().getMessageById(e.getMessageId()).complete().getMember().getAsMention()+" had their evidence approved by "+e.getMember().getAsMention());
				try {
					eb.addField("Evidence: ", "", true);
					eb.setImage(e.getChannel().getMessageById(e.getMessageId()).complete().getAttachments().get(0).getUrl());
				} catch (IndexOutOfBoundsException e1) {
					eb.addField("Evidence:", e.getChannel().getMessageById(e.getMessageId()).complete().getContentDisplay(), true);
				}
				eb.setTimestamp(Instant.from(ZonedDateTime.now()));
				e.getGuild().getTextChannelById("393796120930942986").sendMessage(eb.build()).queue();
				int s = new Date().getSeconds();
				while (s != new Date().getSeconds()) {}
				e.getTextChannel().getMessageById(e.getMessageId()).complete().delete().queue();
				
			} if (e.getReactionEmote().getId().equals("543567328311508995")) {
				e.getChannel().sendMessage("<:wrong:543567328311508995> "+e.getChannel().getMessageById(e.getMessageId()).complete().getMember().getAsMention()+", "
					+ "your evidence has been denied by "+e.getMember().getAsMention()+". Make sure that: \n> The evidence clearly shows the purchase\n> The evidence is not reused/distorted"
					+ "\nFor any other questions, contact "+e.getMember().getAsMention()+" in <#393516444991881217>").complete().delete().queueAfter(5, TimeUnit.MINUTES);
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.red);
				eb.setAuthor("Support-A-Creator Evidence Denied", null, e.getGuild().getIconUrl());
				eb.setDescription(e.getChannel().getMessageById(e.getMessageId()).complete().getMember().getAsMention()+" had their evidence denied by "+e.getMember().getAsMention());
				try {
					eb.addField("Evidence: ", "", true);
					eb.setImage(e.getChannel().getMessageById(e.getMessageId()).complete().getAttachments().get(0).getUrl());
				} catch (IndexOutOfBoundsException e1) {
					eb.addField("Evidence:", e.getChannel().getMessageById(e.getMessageId()).complete().getContentDisplay(), true);
				}
				eb.setTimestamp(Instant.from(ZonedDateTime.now()));
				e.getGuild().getTextChannelById("393796120930942986").sendMessage(eb.build()).queue();
				int s = new Date().getSeconds();
				while (s != new Date().getSeconds()) {}
				e.getTextChannel().getMessageById(e.getMessageId()).complete().delete().queue();
			}
			
			
			
		}
		
	}
	
}
