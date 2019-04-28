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

import org.json.JSONPointer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReportBug extends ListenerAdapter {

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
			TreeMap<Integer, String> map = me.darth.darthbot.commands.search.searchTrello(args);
			while(!map.isEmpty()) {
				if (map.firstKey() >= (args.length / 2) - 1) {
					float acc = map.firstKey() * 100 / (args.length - 1);
					dupes.addField("Accuracy: "+acc+"%", map.firstEntry().getValue(), false);
				}
				map.remove(map.firstKey());
			}
			if (dupes.getFields().size() > 0) {
				e.getChannel().sendMessage(e.getMember().getAsMention()).embed(dupes.build()).queue();
				e.getMessage().addReaction("✅").queue();
				return;
			}
			try {
				cardsplit = e.getMessage().getContentRaw().replace(args[0], "").split(" - ");
				try {
					desc = cardsplit[1];
				} catch (ArrayIndexOutOfBoundsException e1) {
					desc = "No Description Set";
				}
				String cardname = URLEncoder.encode("[BUG] "+cardsplit[0]);
				String carddesc = URLEncoder.encode("**Bug:** "+cardsplit[0]
						+ "\n**Details:** "+desc
						+ "\n\n---\n\n"
						+ "> Reported by `"+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator()+"`"
						+ "\n> Reporter ID: `"+e.getAuthor().getId()+"`");
				Date d = new Date();
				int y = d.getYear() + 1900;
				url = new URL("https://api.trello.com/1/cards?name="+cardname+"&desc="+carddesc+"&idList=5cbc6c61a0685f1423e3055d"
						+ "&keepFromSource=all"
						+ "&key=68203d3c0219e66cb264d77cad3031de"
						+ "&token=6be68efc8c4017ca24c55ce3ccca7fff22d12d1a24406138dd17045139a0a25a");
			} catch (MalformedURLException e3) {return;}
			HttpURLConnection con = null;
			try {
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				JsonParser jp = new JsonParser();
				JsonElement root = jp.parse(new InputStreamReader((InputStream) con.getContent()));
				JsonObject rootobj = root.getAsJsonObject();
				System.out.println(rootobj);
				
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor("Bug Successfully Reported!", rootobj.get("shortUrl").getAsString(), "https://i.imgur.com/vEFegXx.png");
				eb.addField("Bug", cardsplit[0], true);
				eb.addField("Description", desc, true);
				eb.addField("Reported by", e.getMember().getAsMention()+" *"+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator()+"*", true);
				eb.addField("URL", rootobj.get("shortUrl").getAsString(), true);
				eb.setColor(Color.blue);
				e.getChannel().sendMessage(eb.build()).queue();
			    
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
		}
	}
}
