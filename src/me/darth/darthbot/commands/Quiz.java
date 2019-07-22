package me.darth.darthbot.commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Quiz extends ListenerAdapter {

	boolean enabled = false;
	
	HashMap<Long, String> ansmap = new HashMap<Long, String>();
	HashMap<Long, Long> msgmap = new HashMap<Long, Long>();
	HashMap<Long, Boolean> isactive = new HashMap<Long, Boolean>();
	HashMap<Long, Long> timestarted = new HashMap<Long, Long>();
	
	@SuppressWarnings("deprecation")
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (ansmap.get(e.getGuild().getIdLong()) != null && !e.getAuthor().isBot()) {
			if (ansmap.get(e.getGuild().getIdLong()).equalsIgnoreCase(e.getMessage().getContentRaw())) {
				Message msg = e.getChannel().getMessageById(msgmap.get(e.getGuild().getIdLong())).complete();
				MessageEmbed me = msg.getEmbeds().get(0);
				long time = (System.currentTimeMillis() / 1000) - msg.getCreationTime().toEpochSecond();
				msg.editMessage(new EmbedBuilder(me).addField("Correct Answer", "`"
				+ansmap.get(e.getGuild().getIdLong())+"` by "+e.getMember().getAsMention()+" in `"+time+"secs`", false).setColor(Color.green).build()).queue();
				ansmap.remove(e.getGuild().getIdLong());
				msgmap.remove(e.getGuild().getIdLong());
				timestarted.remove(e.getGuild().getIdLong());
				int prize = Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText().replace("Type the correct answer in chat to win a prize! | $", ""));
				try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getIdLong());
				      while (rs.next())
				      {
				        long bux = rs.getLong("DBux");
				        long newbux = bux + prize;
				        if (bux != -1337) {
				        	con.prepareStatement("UPDATE profiles SET DBux = "+newbux+" WHERE UserID = "+e.getMember().getUser().getIdLong()).execute();
				        }
				        con.prepareStatement("UPDATE profiles SET DailyBonus = "+new Date().getDate()+" WHERE UserID = "+e.getMember().getUser().getIdLong()).execute();
				        e.getChannel().sendMessage(e.getMember().getAsMention()+", you won the quiz and the prize of **$"+prize+"**!").queue();
				      }
				      rs.close();
				      con.close();
				} catch (SQLException e1) {
				   e1.printStackTrace();
				}
				isactive.put(e.getGuild().getIdLong(), false);
			}
		}
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!quiz")) {
			if (!enabled) {
				e.getChannel().sendMessage(":no_entry: This command is temporarily globally disabled due to internal issues. Sorry for the inconvenience!").queue();
				return;
			}
			if (isactive.get(e.getGuild().getIdLong()) != null && isactive.get(e.getGuild().getIdLong()) && new Date().getTime() - timestarted.get(e.getGuild().getIdLong()) > 60000) {
				isactive.remove(e.getGuild().getIdLong());
				timestarted.remove(e.getGuild().getIdLong());
			}
			URL url;
			try {
				try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getIdLong());
				      while (rs.next())
				      {
				        int quiz = rs.getInt("Quiz");
				        if (quiz == new Date().getMinutes()) {
				        	e.getChannel().sendMessage(e.getMember().getAsMention()+", you must wait a minute between uses of the `!quiz` command!").queue();
				        	return;
				        }
				        con.prepareStatement("UPDATE profiles SET Quiz = "+new Date().getMinutes()+" WHERE UserID = "+e.getMember().getUser().getIdLong()).execute();
				      }
				      rs.close();
				      con.close();
				} catch (SQLException e1) {
				   e1.printStackTrace();
				}
				if (isactive.get(e.getGuild().getIdLong()) == null || isactive.get(e.getGuild().getIdLong()) == false) {
					isactive.put(e.getGuild().getIdLong(), true);
				} else {
					e.getChannel().sendMessage("There is already a quiz in progress on this server!").queue();
					return;
				}
				url = new URL("https://opentdb.com/api.php?amount=1&difficulty=easy");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}
				in.close();
				JSONObject obj = new JSONObject(content.toString());
				JSONArray arr = obj.getJSONArray("results");
				String question = null;
				String answer = null;
				String incorrect = null;
				for (int x = 0 ; x < arr.length() ; x++) {
					question = URLDecoder.decode(arr.getJSONObject(x).get("question").toString(), StandardCharsets.US_ASCII.name()).replaceAll("&quot;", "\"").replaceAll("&sup2", "²").replaceAll("&#039;", "'").replaceAll("&eacute;", "e").replaceAll("&amp;", "").replaceAll("&Uuml;", "");
					answer = URLDecoder.decode(arr.getJSONObject(x).get("correct_answer").toString(), StandardCharsets.US_ASCII.name()).replaceAll("&quot;", "\"").replaceAll("&sup2", "²").replaceAll("&#039;", "'").replaceAll("&eacute;", "e").replaceAll("&amp;", "").replaceAll("&Uuml;", "");
					incorrect = URLDecoder.decode(arr.getJSONObject(x).get("incorrect_answers").toString(), StandardCharsets.US_ASCII.name()).replaceAll("&quot;", "\"").replaceAll("&sup2", "²").replaceAll("&#039;", "'").replaceAll("&eacute;", "e").replaceAll("&amp;", "").replaceAll("&Uuml;", "");
				}
				
				ArrayList<String> possible = new ArrayList<>(Arrays.asList(incorrect.replace("[", "").replace("]", "").split(",")));
				possible.add(answer);
				int prize = possible.toString().length() / 2;
				Collections.shuffle(possible);
				EmbedBuilder eb = new EmbedBuilder().setAuthor("Quiz", null, e.getGuild().getIconUrl()).addField("Question", question, false)
						.addField("Possible Answers", "• "+possible.toString().replace("[", "").replace("]", "").replace("\"", "").replace(", ", "\n• "), false).setColor(Color.yellow)
						.setFooter("Type the correct answer in chat to win a prize! | $"+prize, null);
				Message msg = e.getChannel().sendMessage(eb.build()).complete();
				ansmap.put(e.getGuild().getIdLong(), answer);
				msgmap.put(e.getGuild().getIdLong(), msg.getIdLong());
				timestarted.put(e.getGuild().getIdLong(), new Date().getTime());
				//e.getChannel().sendMessage(answer).queue();
				
				
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
		}
	}

}
