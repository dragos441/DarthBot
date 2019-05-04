package me.darth.darthbot.testserver;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReactRole extends ListenerAdapter {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getMessageId().equals("571070192806002690") && e.getReactionEmote().getName().equals("🗒")) { 
			e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("571066563055321098")).reason("Adding ReactRole").queue();;
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		if (e.getMessageId().equals("571070192806002690") && e.getReactionEmote().getName().equals("🗒")) { 
			e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("571066563055321098")).reason("Removingg ReactRole").queue();;		}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getMessage().getMentionedMembers().contains(e.getGuild().getMemberById("159770472567799808")) && e.getMessage().getContentRaw().contains("?")) {
			e.getChannel().sendMessage("That sounds like a question, why not ask it in the <#574246600570830853>?").queue();
		}
	}
}
