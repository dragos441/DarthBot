package me.darth.darthbot.commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReportBug extends ListenerAdapter {
	
	public static void makeCard(String[] args, Message msg, User author, TextChannel chnl, Boolean bug) {
		String [] cardsplit = msg.getContentStripped().replace(args[0], "").split(" - ");
		String desc = null;
		try {
			desc = cardsplit[1];
		} catch (ArrayIndexOutOfBoundsException e1) {
			desc = "No Description Set";
		}
		Trello trello = new TrelloImpl("36c6ca5833a315746f43a1d6eee885b4", "dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847", new ApacheHttpClient());
		Card c = new Card();
		if (bug) {
			c.setName("[BUG]"+cardsplit[0]);
			c.setDesc("**Bug:**"+cardsplit[0]
					+ "\n**Details:** "+desc
					+ "\n\n---\n\n"
					+ "> Reported by `"+author.getName()+"#"+author.getDiscriminator()+"`"
					+ "\n> Reporter ID: `"+author.getId()+"`");
			c = trello.createCard("5cbc6c61a0685f1423e3055d", c);
		} else {
			c.setName(cardsplit[0]);
			c.setDesc("**Suggestion:** "+cardsplit[0]
					+ "\n**Details:** "+desc
					+ "\n\n---\n\n"
					+ "> Submitted by `"+author.getName()+"#"+author.getDiscriminator()+"`"
					+ "\n> Submitter ID: `"+author.getId()+"`\n\n---\n\nVotes: ,");
			c.setPos(-9999);;
			Card nc = trello.createCard("5cbc6c5a24c96885903fde3e", c);
			me.darth.darthbot.main.Main.jda.getGuildById("568849490425937940").getTextChannelById("575440909018202136").sendMessage("**NEW SUGGESTION:** "+nc.getShortUrl()).complete().delete().queueAfter(15, TimeUnit.SECONDS);
		}
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor("Successfully submitted to Trello!", c.getShortUrl(), "https://cdn3.iconfinder.com/data/icons/budicon-chroma-communication/24/email-forward-512.png");
		eb.addField("Submission", cardsplit[0], true);
		eb.addField("Description", desc, true);
		eb.addField("Submitted by", author.getName()+"#"+author.getDiscriminator(), true);
		eb.setDescription("**"+cardsplit[0]+"** has been successfully forwarded to Trello. View your card here: "+c.getShortUrl());
		eb.setColor(Color.blue);
		chnl.sendMessage(eb.build()).queue();
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!bug") || args[0].equalsIgnoreCase("!reportbug")) {
			try {
				String test = args[1];
			} catch (ArrayIndexOutOfBoundsException e1) {
				e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!reportbug Bug - Description`").queue();
				return;
			}
			URL url;
			String desc = null;
			String[] cardsplit = null;
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
			makeCard(args, e.getMessage(), e.getAuthor(), e.getChannel(), true);
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
		if (msg.getContentRaw().split(" ")[0].equalsIgnoreCase("!reportbug") && e.getReactionEmote().getName().equals("✅")
				|| msg.getContentRaw().split(" ")[0].equalsIgnoreCase("!bug") && e.getReactionEmote().getName().equals("✅")) { 
			if (!e.getMember().equals(msg.getMember()) && !e.getMember().getUser().equals(e.getJDA().getSelfUser())) {
				e.getTextChannel().removeReactionById(e.getMessageId(), "✅", e.getMember().getUser()).queue();
				return;
			} else if (e.getMember().equals(msg.getMember())) {
				makeCard(msg.getContentRaw().split(" "), msg, msg.getAuthor(), e.getTextChannel(), true);
			}
		}
	}
}
