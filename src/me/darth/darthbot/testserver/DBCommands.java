package me.darth.darthbot.testserver;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.trello4j.Trello;
import org.trello4j.TrelloImpl;
import org.trello4j.model.Card;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DBCommands extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
	
		if (args[0].equalsIgnoreCase("!beta")) {
			if (!e.getGuild().getOwner().equals(e.getMember())) {
				e.getChannel().sendMessage(":no_entry: You are not the owner of this server! - You must execute this command on your own server!").queue();
				return;
			}
			if (!me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").isMember(e.getAuthor())) {
				e.getChannel().sendMessage(":no_entry: You are not a member of the DarthBot Discord!").queue();
				return;
			}
			if (e.getGuild().getMembers().size() < 10) {
				e.getChannel().sendMessage(":no_entry: To prevent abuse, your server must have 10 members to claim the role!").queue();
				return;
			}
			if (me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getMember(e.getAuthor()).getRoles().contains(me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getRoleById("581531284074135552"))) {
				e.getChannel().sendMessage(":no_entry: You already have the role!").queue();
				return;
			}
			me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getController().addSingleRoleToMember(me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getMember(e.getAuthor()), 
					me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getRoleById("581531284074135552")).queue();
			e.getChannel().sendMessage(":white_check_mark: You have succesfully claimed your Beta Tester role! Thanks for testing DarthBot!").queue();
		}
		if (args[0].equalsIgnoreCase("!changelog") && me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getMember(e.getMember().getUser())
				.getRoles().contains(e.getGuild().getRoleById("569463842552152094"))) {
			e.getGuild().getRoleById("571066563055321098").getManager().setMentionable(true).queue();
			me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getTextChannelById("569466661644795910").sendMessage(e.getMessage().getContentDisplay().replace(args[0]+" ", "")+"\n<@&571066563055321098> *(<#602255519348162570> to get Update Notifications!)*").queue();
			e.getGuild().getRoleById("571066563055321098").getManager().setMentionable(false).queueAfter(3, TimeUnit.SECONDS);
			e.getMessage().delete().queue();
		}
		if (args[0].equalsIgnoreCase("!ea") && me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getMember(e.getMember().getUser())
				.getRoles().contains(e.getGuild().getRoleById("592813831164657684"))) {
			e.getGuild().getRoleById("592816963261038646").getManager().setMentionable(true).queue();
			me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getTextChannelById("570342307870539838").sendMessage("\n^ <@&592816963261038646>^ *(<#602255519348162570> to get Event Notifications!)*").queue();
			e.getGuild().getRoleById("592816963261038646").getManager().setMentionable(false).queueAfter(3, TimeUnit.SECONDS);
			e.getMessage().delete().queue();
		}
		if (args[0].equalsIgnoreCase("!innovator") && e.getMember().getRoles().contains(e.getGuild().getRoleById("569464005416976394"))) {
			if (e.getMessage().getMentionedMembers().isEmpty()) {
				e.getChannel().sendMessage(":no_entry: You must mention a user that will recieve the innovator role!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
			} else {
				e.getGuild().getController().addSingleRoleToMember(e.getMessage().getMentionedMembers().get(0), e.getGuild().getRoleById("590190112688832522")).queue();
				e.getChannel().sendMessage(":confetti_ball: "+e.getMessage().getMentionedMembers().get(0).getAsMention()+" has earned the **Innovators** role for "
						+ "suggesting a popular idea or reporting a bug! Congrats!").queue();
				e.getMessage().delete().queue();
			}
		}
	}

}
