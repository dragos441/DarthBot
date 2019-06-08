package me.darth.darthbot.commands;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class EditMsg extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!editmsg") && e.getAuthor().getId().equals("159770472567799808")) {
			e.getChannel().getMessageById(args[1]).complete().editMessage(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", "")).queue();
		}
		e.getChannel().sendMessage(":white_check_mark: Message edited!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
		e.getMessage().delete().queue();
		
	}

}
