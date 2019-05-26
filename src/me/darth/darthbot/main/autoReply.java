package me.darth.darthbot.main;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class autoReply extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getMessage().getMentionedMembers().contains(e.getGuild().getMemberById("569461469154902016"))) {
			e.getChannel().sendMessage("My ears are burning :fire: - You can use `!commands` for a list of bot commands :)").queue();
		}
		if (!e.getGuild().getId().equals("568849490425937940")) {
			return;
		}
		String msg = e.getMessage().getContentRaw().replace(" ", "");
		if (msg.contains("www.") || msg.contains("http://") || msg.contains("https://") || msg.contains(".com") || msg.contains(".co.uk")
			|| !e.getMessage().getInvites().isEmpty() || msg.contains(".gg")) {
			if (msg.contains("trello.com") || e.getMember().getRoles().contains(e.getGuild().getRoleById("569464005416976394"))) {
				return;
			} else {
				e.getMessage().delete().queue();
				e.getChannel().sendMessage(e.getMember().getAsMention()+", no links allowed!").queue();
			}
		}
		if (e.getMessage().getMentionedMembers().contains(e.getGuild().getMemberById("159770472567799808")) && e.getMessage().getContentRaw().contains("?")) {
			e.getChannel().sendMessage("That sounds like a question, why not ask it in the <#574246600570830853>?").queue();
		}
	}
	
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