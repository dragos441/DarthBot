package me.darth.darthbot.testserver;

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
}
