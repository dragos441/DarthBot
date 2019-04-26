package me.darth.darthbot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Daily extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!daily")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getIdLong());
			      while (rs.next())
			      {
			        long UserID = rs.getLong("UserID");
			        long bux = rs.getLong("DBux");
			        int daily = rs.getInt("DailyBonus");
			        if (daily != new Date().getDate()) {
				        int rand = new Random().nextInt((100 - 80) + 1) + 80;
				        long newbux = bux + rand;
				        if (bux != -1337) {
				        	con.prepareStatement("UPDATE profiles SET DBux = "+newbux+" WHERE UserID = "+e.getMember().getUser().getIdLong()).execute();
				        }
				        con.prepareStatement("UPDATE profiles SET DailyBonus = "+new Date().getDate()+" WHERE UserID = "+e.getMember().getUser().getIdLong()).execute();
				        e.getChannel().sendMessage("You claimed your daily reward of $**"+rand+"**").queue();
			        } else {
			        	e.getChannel().sendMessage("You have already claimed your daily reward for today! Try again tomorrow!").queue();
			        }
			      }
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
		}
	}

}
