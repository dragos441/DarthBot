package me.darth.darthbot.commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.TreeMap;
import org.json.JSONObject;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReportBug extends ListenerAdapter {
	
	@SuppressWarnings("deprecation")
	public static void makeCard(String[] args, Message msg, User author, TextChannel chnl, Boolean bug) {
		String [] cardsplit = msg.getContentStripped().replace(args[0], "").split("\\|");
		String desc = null;
		try {
			desc = cardsplit[1];
		} catch (ArrayIndexOutOfBoundsException e1) {
			desc = "*No Description Set*";
		}
		String shortUrl = null;
		String cardname = null;
		String carddesc = null;
		String board = null;
		EmbedBuilder eb = new EmbedBuilder();
		if (bug) {
			cardname = URLEncoder.encode("[BUG]"+cardsplit[0]);
			carddesc = URLEncoder.encode("**Bug:**"+cardsplit[0]
					+ "\n**Details:** "+desc
					+ "\n\n---\n\n"
					+ "> Reported by `"+author.getName()+"#"+author.getDiscriminator()+"`"
					+ "\n> Reporter ID: `"+author.getId()+"`"
					+ "\n\n**Information:**\nGuild: `"+msg.getGuild()+"`"
					+ "\nPerms: `"+msg.getMember().getPermissions()+"`");
			board = "5cbc6c61a0685f1423e3055d";
		} else {
			cardname = URLEncoder.encode(cardsplit[0]);
			carddesc = URLEncoder.encode("**Suggestion:** "+cardsplit[0]
					+ "\n**Details:** "+desc
					+ "\n\n---\n\n"
					+ "> Submitted by `"+author.getName()+"#"+author.getDiscriminator()+"`"
					+ "\n> Submitter ID: `"+author.getId()+"`\n\n---\n\n**Vote ID's:** ");
			board = "5cbc6c5a24c96885903fde3e";
			eb.setFooter("Your suggestion will automatically appear in #suggestions shortly!", null);
		}
		URL url;
		try {
			url = new URL("https://api.trello.com/1/cards?name="+cardname+"&desc="+carddesc+"&pos=bottom&idList="+board+"&keepFromSource=all"
					+ "&key=36c6ca5833a315746f43a1d6eee885b4&token=dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			JSONObject obj = new JSONObject(content.toString());
			shortUrl = obj.getString("shortUrl").toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eb.setAuthor("Successfully submitted to Trello!", shortUrl, "https://cdn3.iconfinder.com/data/icons/budicon-chroma-communication/24/email-forward-512.png");
		eb.addField("Submission", cardsplit[0], true);
		eb.addField("Description", desc, true);
		eb.addField("Submitted by", author.getName()+"#"+author.getDiscriminator(), true);
		eb.setDescription("**"+cardsplit[0]+"** has been successfully forwarded to Trello. View your card here: "+shortUrl);
		eb.setColor(Color.blue);
		chnl.sendMessage(eb.build()).queue();
		if (!bug) {
			me.darth.darthbot.main.Main.sm.getTextChannelById("575440909018202136").sendMessage(shortUrl).queue();
		} else {
			me.darth.darthbot.main.Main.sm.getTextChannelById("590252086189621258").sendMessage(shortUrl).queue();
		}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
          if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!bug") || args[0].equalsIgnoreCase("!reportbug")) {
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
				e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!reportbug Bug | Description`").queue();
				return;
			}
			if (e.getMessage().getContentRaw().contains("\n")) {
				e.getChannel().sendMessage(":no_entry: Please don't include line-breaks in your report!\n(eg\nthese\nthings)").queue();
				return;
			}
			String title = e.getMessage().getContentRaw();
			if (e.getMessage().getContentRaw().contains("|")) {
				title = e.getMessage().getContentRaw().split("\\|")[0];
			} else if (title.length() > 50) {
				e.getChannel().sendMessage(":no_entry: Your suggestion title is too long! Make sure to split the title and description using the `|` symbol!\n*Eg: `!reportbug This is the title | This is the description`*").queue();
				return;
			}
			EmbedBuilder dupes = new EmbedBuilder().setAuthor("Possible Duplicates Found", null, e.getGuild().getIconUrl()).setColor(Color.red);
			dupes.setDescription("Please check that the below cards aren't a duplicate of what you're submitting. If none of them are, **react with a :white_check_mark:"
					+ "to your message above to submit it!**");
			TreeMap<Integer, String> map = me.darth.darthbot.commands.SearchTrello.searchTrello(title.split(" "));
			while(map != null && !map.isEmpty()) {
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