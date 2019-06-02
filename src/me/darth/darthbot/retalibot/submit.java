package me.darth.darthbot.retalibot;

import java.awt.Color;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class submit extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (!e.getGuild().getId().equals("393499439739961366")) {
			return;
		}
		if (args[0].equalsIgnoreCase("!submit") || args[0].equalsIgnoreCase("!submitvideo") || args[0].equalsIgnoreCase("!submitglitch")
			|| args[0].equalsIgnoreCase("!sv") || args[0].equalsIgnoreCase("!sg") || args[0].equalsIgnoreCase("!submitvid") 
			|| args[0].equalsIgnoreCase("!submitg") || args[0].equalsIgnoreCase("!submitv") || args[0].equalsIgnoreCase("!cancel")) {
			if (args[0].equalsIgnoreCase("!cancel")) {
				try {
					String name = e.getChannel().getMessageById(args[1]).complete().getEmbeds().get(0).getAuthor().getName().replace("Glitch Report by ", "")
							.replace("Video Suggestion by ", "");
					long mid = Long.parseLong(e.getChannel().getMessageById(args[1]).complete().getEmbeds().get(0).getFooter().getText().replace("!cancel ", ""));
					if (name.equals(e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator())
						|| e.getMember().getRoles().contains(e.getGuild().getRoleById("557702978455339009"))) {
						e.getChannel().getMessageById(mid).complete().delete().queue();
						e.getChannel().sendMessage(e.getMember().getAsMention()+", you have successfully cancelled  #`"+mid+"`!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
					} else {
						e.getChannel().sendMessage(e.getMember().getAsMention()+", that is either an Invalid ID or you don't have permission to remove the suggestion!").complete().delete().queueAfter(10, TimeUnit.SECONDS);
					}
				} catch (ArrayIndexOutOfBoundsException e1) {
					e.getChannel().sendMessage("Invalid Syntax: `!cancel <Message ID>`").complete().delete().queueAfter(10, TimeUnit.SECONDS);
				} catch (ErrorResponseException e1) {
					e.getChannel().sendMessage(e.getMember().getAsMention()+", that is either an Invalid ID or not your suggestion!").complete().delete().queueAfter(10, TimeUnit.SECONDS);
				}
				e.getMessage().delete().queue();
				return;
			}
			if (e.getMessage().getContentRaw().replace(args[0], "").isEmpty()) {
				if (args[0].toLowerCase().contains("vid")) {
					e.getChannel().sendMessage("Incorrect Synax: `!submitvideo <Video Idea>`\n").complete().delete().queueAfter(2, TimeUnit.MINUTES);
				} else if (args[0].toLowerCase().contains("glitch")) {
					e.getChannel().sendMessage("Incorrect Synax: `!submitglitch <Glitch Description> | Step 1 to reproduce - Step 2 - Step 3 etc`").complete().delete().queueAfter(2, TimeUnit.MINUTES);
				} else {
					e.getChannel().sendMessage("Incorrect Synax\n`!submitvideo <Video Idea>`\n"
							+ "`!submitglitch <Glitch Description> | Step 1 to reproduce - Step 2 - Step 3 etc`").complete().delete().queueAfter(2, TimeUnit.MINUTES);
				}
				
				if (e.getChannel().equals(e.getGuild().getTextChannelById("558052568631345152"))) {
					e.getMessage().delete().queue();
				}
				return;
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setDescription("**Status: :stopwatch: Pending**\n*User ID: "+e.getAuthor().getIdLong()+"*");
			if (e.getMember().getRoles().contains(e.getGuild().getRoleById("551807867343339550"))) {
				eb.setColor(Color.ORANGE);
			} else {
				eb.setColor(Color.GRAY);
			}
			String[] split = null;
			if (e.getMessage().getContentRaw().contains("|") && !args[0].equalsIgnoreCase("!submitvideo") || args[0].equalsIgnoreCase("!submitglitch")) {
				eb.setAuthor("Glitch Report by "+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator(), null, e.getAuthor().getAvatarUrl());
				split = e.getMessage().getContentRaw().split("\\|");
				eb.addField("**Description**", split[0].replace(args[0], ""), true);
				if (split.length >= 2) {
					eb.addField("**Steps to reproduce**", "**-** "+e.getMessage().getContentRaw().replace(split[0], "").replaceAll("-", "\n**-** ").replaceAll(" :", "\n**-** ").replaceFirst("| ", "").replace("|", "\n**-** "), false);
				}
			} else {
				eb.setAuthor("Video Suggestion by "+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator(), null, e.getAuthor().getAvatarUrl());
				eb.addField("**Description**", e.getMessage().getContentRaw().replace(args[0], "").replace("|", "").replace("_", ""), true);
			}
			Message msg = null;
			try {
				msg = e.getGuild().getTextChannelById("558052568631345152").sendMessage(eb.build()).complete();
				EmbedBuilder neweb = new EmbedBuilder(eb);
				neweb.setFooter("!cancel "+msg.getId(), null);
				msg.editMessage(neweb.build()).queue();
				if (!e.getChannel().equals(e.getGuild().getTextChannelById("558052568631345152"))) {
					e.getChannel().sendMessage("<:tick:543564724021755916> Successfully posted in <#558052568631345152>").complete().delete().queueAfter(30, TimeUnit.SECONDS);
				}
			} catch (ErrorResponseException e1) {
				e.getChannel().sendMessage(e.getMember().getAsMention()
					+", Incorrect Syntax! `!submit [Brief Description of Glitch] | Step 1 to reproduce - Step 2 - Step 3`")
					.complete().delete().queueAfter(30, TimeUnit.SECONDS);
				e.getMessage().delete().queue();
				return;
			}
			msg.addReaction(e.getGuild().getEmoteById("543564724021755916")).queue();
			msg.addReaction(e.getGuild().getEmoteById("543567328311508995")).queue();
			e.getMessage().delete().queue();
		} else if (e.getChannel().equals(e.getGuild().getTextChannelById("558052568631345152")) && !e.getAuthor().isBot()) {
			e.getChannel().sendMessage(e.getMember().getAsMention()+", read the top message on how to format the `!submit` command. If you're struggling, use this form: https://darth176599.typeform.com/to/bgKdLa").complete().delete().queueAfter(10, TimeUnit.SECONDS);
			e.getMessage().delete().queue();
		}
	}
	
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (!e.getGuild().getId().equals("393499439739961366")) {
			return;
		}
		if (!e.getChannel().equals(e.getGuild().getTextChannelById("558052568631345152")) || e.getMember().getUser().isBot()) {
			return;
		}
		if (!e.getMember().getRoles().contains(e.getGuild().getRoleById("557702978455339009"))) {
			e.getTextChannel().removeReactionById(e.getMessageId(), e.getReaction().getReactionEmote().getEmote(), e.getMember().getUser()).queue();
			return;
		}
		EmbedBuilder eb = new EmbedBuilder(e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0));
		long mid = Long.parseLong(e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getDescription().split("User ID: ")[1].replace("*", ""));
		Member m = e.getGuild().getMemberById(mid);
		eb.setDescription("**Status: <:tick:543564724021755916> Approved by** **"+e.getMember().getAsMention()+"**");
		if (e.getReaction().getReactionEmote().getId().equals("543564724021755916")) {
			eb.setDescription("**Status: <:tick:543564724021755916> Approved by** **"+e.getMember().getAsMention()+"**");
			eb.setColor(Color.GREEN);
			e.getChannel().getMessageById(e.getMessageId()).complete().editMessage(eb.build()).queue();
			if (e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getAuthor().getName().contains("Video Suggestion")) {
				e.getChannel().sendMessage(m.getAsMention()+", your suggestion/glitch report has been approved by "+e.getMember().getAsMention()+"!").queue();
			} else {
				e.getChannel().sendMessage(m.getAsMention()+", your suggestion/glitch report has been approved by "+e.getMember().getAsMention()+"! You have earned the **Glitch Hunter** role!")
				.complete().delete().queueAfter(2, TimeUnit.MINUTES);
				e.getGuild().getController().addSingleRoleToMember(m, e.getGuild().getRoleById("551807867343339550")).queue();
				e.getGuild().getTextChannelById("559344427898765313").sendMessage(m.getAsMention()+", congrats on earning the **Glitch Hunter**"
						+ " role!").queue();
			}
			e.getGuild().getTextChannelById("559845222910132225").sendMessage(eb.build()).queue();
			e.getMember().getUser().openPrivateChannel().queue((channel) ->
	        {
	        	Message message = null;
	        	eb.setFooter("Allow messages from Server Members to get these kind of messages permanently DMed to you!", null);
				try {
					message = channel.sendMessage("Your suggestion in <#558052568631345152> was accepted! Below is a copy of it!").embed(eb.build()).submit().get();
				} catch (InterruptedException e1) {} catch (ExecutionException e2) {}
				if (message == null) {
					try {
						e.getGuild().getTextChannelById("558052568631345152").sendMessage("Your suggestion in <#558052568631345152> was accepted! Below is a copy of it!").embed(eb.build()).submit().get().delete().queueAfter(2, TimeUnit.MINUTES);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
	        });
			e.getGuild().getTextChannelById("393796120930942986").sendMessage(eb.build()).queue();
			e.getChannel().getMessageById(e.getMessageId()).complete().delete().queue();
		}
		if (e.getReaction().getReactionEmote().getId().equals("543567328311508995")) {
			eb.setDescription("**Status: <:incorrect:543567328311508995> Denied by** **"+e.getMember().getAsMention()+"**");
			eb.setColor(Color.red);
			e.getChannel().getMessageById(e.getMessageId()).complete().editMessage(eb.build())
			.complete().delete().queueAfter(2, TimeUnit.MINUTES);
			String msg = "**"+m.getAsMention()+", your suggestion/report has been denied by "+e.getMember().getAsMention()+".** This may be because:\n"
					+ "> It's not relevant enough\n"
					+ "> It could not be reproduced\n"
					+ "> Retali8 is already aware of it\n\nIf any further details about your report are needed, "
					+ "you will be contacted in <#393516444991881217>. Below is a copy of your suggestion:";
			m.getUser().openPrivateChannel().queue((channel) ->
	        {
	        	Message message = null;
	        	eb.setFooter("Allow messages from Server Members to get these kind of messages permanently DMed to you!", null);
				try {
					message = channel.sendMessage(msg).embed(eb.build()).submit().get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (message == null) {
					try {
						e.getGuild().getTextChannelById("558052568631345152").sendMessage(msg).embed(eb.build()).submit().get().delete().queueAfter(2, TimeUnit.MINUTES);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
	        });
			e.getGuild().getTextChannelById("393796120930942986").sendMessage(eb.build()).queue();
			e.getChannel().getMessageById(e.getMessageId()).complete().delete().queueAfter(2, TimeUnit.SECONDS);
		}
	}
	
}
