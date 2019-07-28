package me.darth.darthbot.natter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Project_Natter extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (!e.getChannel().getId().equals("602116618780147712") || e.getAuthor().isBot() || e.getAuthor().isFake()) {
			return;
		}
		String resp = null;
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			
			String message = e.getMessage().getContentRaw().replace(" ", "").replace(".", "").replace("?", "").replace("!", "").replace(",", "").replace("'", "").replace("\"", "").replace("/", "").toLowerCase();
			if (!message.isEmpty()) {
				ResultSet rs = con.createStatement().executeQuery("SELECT * FROM ChatBot WHERE Statement = '"+message+"'");
				boolean found = false;
				ArrayList<String> responselist = new ArrayList<String>();
				while (rs.next()) {
					int score = rs.getInt("Score");
					found=true;
					if (score > 0) {
						while (score > 0) {
							responselist.add(rs.getString("Response"));
						}
						score=score-1;
					} else {
						responselist.add(rs.getString("Response"));
					}
					int rand = new Random().nextInt(responselist.size());
					resp = responselist.get(rand);
				}
				if (!found) {
					ResultSet responses = con.createStatement().executeQuery("SELECT * FROM ChatBot ORDER BY RAND() LIMIT 1");
					while (responses.next()) {
						resp = responses.getString("Response");
					}
				}
				e.getGuild().getTextChannelById("602140465789141014").sendMessage(responselist.toString()).queue();
			}
			e.getChannel().sendMessage(resp).queue();
			Message msg = e.getChannel().getHistory().retrievePast(3).complete().get(2);
			String botlog = msg.getContentRaw();
			if (msg.getContentRaw() == null) {
				return;
			}
			if (botlog != null && !message.isEmpty()) {
				ResultSet rs = con.createStatement().executeQuery("SELECT * FROM ChatBot WHERE Statement = '"+botlog+"'");
				boolean found = false;
				while (rs.next()) {
					String formattedfield = rs.getString("Response").replace(" ", "").replace(".", "").replace("?", "").replace("!", "").replace(",", "").replace("'", "").replace("\"", "").replace("/", "").toLowerCase();
					if (formattedfield.equalsIgnoreCase(message)) {
						found=true;
						con.prepareStatement("UPDATE ChatBot SET Score = Score + 1 WHERE Statement = '"+botlog+"' AND Response = '"+message+"'").execute();
					}
				}
				if (!found) {
					con.prepareStatement("INSERT INTO ChatBot (Statement, Response, Score) VALUES ('"+botlog+"', '"+e.getMessage().getContentRaw()+"', 0)").execute();
				}
				System.out.print(found);
			}
		} catch (SQLException e1) {e1.printStackTrace();}
		
		
	}
	
}
