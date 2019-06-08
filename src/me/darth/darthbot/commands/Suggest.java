package me.darth.darthbot.commands;

import java.util.TreeMap;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Suggest extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!trello")) {
			e.getChannel().sendMessage("Here is the Development Trello Board :smile:: https://trello.com/b/EndaJ5Op/DarthBot").queue();
		}
		if (args[0].equalsIgnoreCase("!suggest")) {
			if (!e.getGuild().getId().equals("568849490425937940")) {
				if (me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").isMember(e.getAuthor())) {
					e.getChannel().sendMessage(":no_entry: This command can only be executed on the DarthBot Server. Luckily for you though, you're already a member, so simply copy and paste it in <#569465464590172180>!").queue();
				} else {
					e.getChannel().sendMessage(":no_entry: This command can only be executed on the DarthBot server! Join it by using the link in the `!DarthBot` command!").queue();	
				}
				return;
			}
			try {
				String test = args[1];
				test=test+"";
			} catch (ArrayIndexOutOfBoundsException e1) {
				e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!suggest Idea - Description`").queue();
				return;
			}
			if (e.getMessage().getContentRaw().contains("\n")) {
				e.getChannel().sendMessage(":no_entry: Please don't include line-breaks in your suggestion!\n(eg\nthese\nthings)").queue();
				return;
			}
			EmbedBuilder dupes = new EmbedBuilder().setAuthor("Possible Duplicates Found", null, e.getGuild().getIconUrl());
			dupes.setDescription("Please check that the below cards aren't a duplicate of what you're submitting. If none of them are, **react with a :white_check_mark:"
					+ "to your message above to submit it!**");
			TreeMap<Integer, String> map = me.darth.darthbot.commands.SearchTrello.searchTrello(args);
			int counter = 1;
			while(!map.isEmpty() && counter <= 5) {
				float acc = map.firstKey();
				if (acc > 30) {
					dupes.addField("Accuracy: "+acc+"%", map.firstEntry().getValue(), false);
				}
				map.remove(map.firstKey());
				counter++;
			}
			if (dupes.getFields().size() > 0) {
				e.getChannel().sendMessage(e.getMember().getAsMention()).embed(dupes.build()).queue();
				e.getMessage().addReaction("✅").queue();
				return;
			}
			me.darth.darthbot.commands.ReportBug.makeCard(args, e.getMessage(), e.getAuthor(), e.getChannel(), false);
			
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
		if (e.getUser().isBot()) {
			return;
		}
		if (msg.getContentRaw().split(" ")[0].equalsIgnoreCase("!suggest") && e.getReactionEmote().getName().equals("✅")
				|| msg.getContentRaw().split(" ")[0].equalsIgnoreCase("!suggestion") && e.getReactionEmote().getName().equals("✅")) { 
			if (!e.getMember().equals(msg.getMember()) && !e.getMember().getUser().equals(e.getJDA().getSelfUser())) {
				e.getTextChannel().removeReactionById(e.getMessageId(), "✅", e.getMember().getUser()).queue();
				return;
			} else if (e.getMember().equals(msg.getMember())) {
				me.darth.darthbot.commands.ReportBug.makeCard(msg.getContentRaw().split(" "), msg, msg.getAuthor(), e.getTextChannel(), false);
			}
		}
	}
	
	
}
