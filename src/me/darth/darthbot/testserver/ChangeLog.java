package me.darth.darthbot.testserver;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChangeLog  extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!changelog") && me.darth.darthbot.main.Main.jda.getGuildById("545700502747349022").getMember(e.getMember().getUser())
				.getRoles().contains(e.getGuild().getRoleById("569463842552152094"))
			|| args[0].equalsIgnoreCase("!cl") && e.getMember().getRoles().contains(e.getGuild().getRoleById("569463842552152094"))) {
			e.getGuild().getRoleById("571066563055321098").getManager().setMentionable(true).queue();
			e.getChannel().sendMessage(e.getMessage().getContentDisplay().replace(args[0]+" ", "")+"\n<@&571066563055321098> *(<#569465554079842306> to get Update Notifications!)*").queue();
			e.getGuild().getRoleById("571066563055321098").getManager().setMentionable(false).queueAfter(1, TimeUnit.SECONDS);
			me.darth.darthbot.main.Main.jda.getGuildById("545700502747349024").getTextChannelById("545700502747349024").sendMessage(e.getMessage().getContentDisplay()).queue();
			e.getMessage().delete().queue();
		}
	}

}
