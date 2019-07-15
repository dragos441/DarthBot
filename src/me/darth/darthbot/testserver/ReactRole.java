package me.darth.darthbot.testserver;

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReactRole extends ListenerAdapter {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) { 
		if (e.getMessageId().equals("599323255752491048") && e.getReactionEmote().getName().equals("🗒")) { //add changelog
			e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("571066563055321098")).reason("Adding ReactRole").queue();;
		}
		if (e.getMessageId().equals("599323255752491048") && e.getReactionEmote().getName().equals("🎊")) { //add events
			e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("592816963261038646")).reason("Adding ReactRole").queue();;
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) { //rev changelog
		if (e.getMessageId().equals("599323255752491048") && e.getReactionEmote().getName().equals("🗒")) { 
			e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("571066563055321098")).reason("Removing ReactRole").queue();
		}
		if (e.getMessageId().equals("599323255752491048") && e.getReactionEmote().getName().equals("🎊")) { 
			e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("592816963261038646")).reason("Removing ReactRole").queue();
		}
	}
	
	
}
