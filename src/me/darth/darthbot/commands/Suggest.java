package me.darth.darthbot.commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

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
			try {
				String test = args[1];
			} catch (ArrayIndexOutOfBoundsException e1) {
				e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!suggest Idea - Description`").queue();
				return;
			}
			EmbedBuilder dupes = new EmbedBuilder().setAuthor("Possible Duplicates Found", null, e.getGuild().getIconUrl());
			dupes.setDescription("Please check that the below cards aren't a duplicate of what you're submitting. If none of them are, **react with a :white_check_mark:"
					+ "to your message above to submit it!**");
			TreeMap<Integer, String> map = me.darth.darthbot.commands.SearchTrello.searchTrello(args);
			while(!map.isEmpty()) {
				float acc = map.firstKey();
				if (acc > 50) {
					dupes.addField("Accuracy: "+acc+"%", map.firstEntry().getValue(), false);
				}
				map.remove(map.firstKey());
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
