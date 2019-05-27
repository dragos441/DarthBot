package me.darth.darthbot.commands;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class FindID extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!findid") || args[0].equalsIgnoreCase("!getid") || args[0].equalsIgnoreCase("!id")) {
			String target = e.getMessage().getContentStripped().replace(args[0]+" ", "");
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Color.blue);
			if (!e.getGuild().getCategoriesByName(target, true).isEmpty()) {
				Category c = e.getGuild().getCategoriesByName(target, true).get(0);
				eb.setAuthor("Information about "+c.getName(), null, e.getGuild().getIconUrl());
				eb.setDescription("**ID:** `"+c.getId()
					+"`\n**As Mention:** `<#"+c.getId()+">`");
				e.getChannel().sendMessage(eb.build()).queue();
			} else if (!e.getGuild().getEmotesByName(target, true).isEmpty() || !e.getMessage().getEmotes().isEmpty()) {
				Emote em = null;
				try {
					em = e.getGuild().getEmotesByName(target, true).get(0);
				} catch (IndexOutOfBoundsException e1) {
					em = e.getMessage().getEmotes().get(0);
				}
				eb.setAuthor("Information about :"+em.getName()+":", null, em.getImageUrl());
				eb.setDescription("**ID:** `"+em.getId()
				+"`\n**As Mention:** `"+em.getAsMention()+"`");
				e.getChannel().sendMessage(eb.build()).queue();
			} else if (!e.getGuild().getMembersByName(target, true).isEmpty() || !e.getMessage().getMentionedMembers().isEmpty()) {
				Member m = null;
				try {
					m = e.getGuild().getMembersByName(target, true).get(0);
				} catch (IndexOutOfBoundsException e1) {
					m = e.getMessage().getMentionedMembers().get(0);
				}
				eb.setAuthor("Information about "+m.getEffectiveName(), null, m.getUser().getEffectiveAvatarUrl());
				eb.setDescription(m.getAsMention()
					+ "\n**ID:** `"+m.getUser().getId()
					+"`\n**As Mention:** `"+m.getAsMention()+"`");
				e.getChannel().sendMessage(eb.build()).queue();
			} else if (!e.getGuild().getRolesByName(target, true).isEmpty() || !e.getMessage().getMentionedRoles().isEmpty()) {
				Role r = null;
				try {
					r = e.getGuild().getRolesByName(target, true).get(0);
				} catch (IndexOutOfBoundsException e1) {
					r = e.getMessage().getMentionedRoles().get(0);
				}
				eb.setColor(r.getColorRaw());
				eb.setAuthor("Information about "+r.getName(), null, e.getGuild().getIconUrl());
				eb.setDescription(r.getAsMention()
					+ "\n**ID:** `"+r.getId()
					+"`\n**As Mention:** `"+r.getAsMention()+"`");
				eb.setFooter("Color: "+r.getColorRaw(), null);
				e.getChannel().sendMessage(eb.build()).queue();
			} else if (!e.getGuild().getTextChannelsByName(target, true).isEmpty() || !e.getMessage().getMentionedChannels().isEmpty()) {
				TextChannel c = null;
				try {
					c = e.getGuild().getTextChannelsByName(target, true).get(0);
				} catch (IndexOutOfBoundsException e1) {
					c = e.getMessage().getMentionedChannels().get(0);
				}
				eb.setAuthor("Information about #"+c.getName(), null, e.getGuild().getIconUrl());
				eb.setDescription(c.getAsMention()
					+ "\n**ID:** `"+c.getId()
					+"`\n**As Mention:** `"+c.getAsMention()+"`");
				e.getChannel().sendMessage(eb.build()).queue();
			} else if (!e.getGuild().getVoiceChannelsByName(target, true).isEmpty()) {
				VoiceChannel c = e.getGuild().getVoiceChannelsByName(target, true).get(0);
				eb.setAuthor("Information about #"+c.getName(), null, e.getGuild().getIconUrl());
				eb.setDescription("**ID:** `"+c.getId()
					+"`\n**As Mention:** `<#"+c.getId()+">`");
				e.getChannel().sendMessage(eb.build()).queue();
			} else if (me.darth.darthbot.main.Main.findUser(target, e.getGuild()) != null) {
				Member m = me.darth.darthbot.main.Main.findUser(target, e.getGuild());
				eb.setAuthor("Information about "+m.getEffectiveName(), null, m.getUser().getEffectiveAvatarUrl());
				eb.setDescription(m.getAsMention()
					+ "\n**ID:** `"+m.getUser().getId()
					+"`\n**As Mention:** `"+m.getAsMention()+"`");
				e.getChannel().sendMessage(eb.build()).queue();
			} else {
				try {
					String f = args[1];
					f=f+"";
				} catch (ArrayIndexOutOfBoundsException e1) {
					e.getChannel().sendMessage(":no_entry: Invalid Syntax: `"+args[0]+" <Category/Custom Emote/Member/Role/Text Channel/Voice Channel>`").queue();
					return;
				}
				e.getChannel().sendMessage(":no_entry: I couldn't find the ID for that! Make sure the spelling is correct, and no additional symbols are used!").queue();
			}
		}
		
	}
	
}
