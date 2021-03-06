package me.darth.darthbot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Daily extends ListenerAdapter {
	
	@SuppressWarnings("deprecation")
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!daily")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getIdLong());
			      Calendar cal = Calendar.getInstance();
			      cal.add(Calendar.DAY_OF_MONTH, -1);
			      while (rs.next())
			      {
			        long bux = rs.getLong("DBux");
			        long daily = rs.getLong("DailyBonus");
			        if (daily < cal.getTimeInMillis()) {
				        int rand = new Random().nextInt((100 - 80) + 1) + 80;
				        long newbux = bux + rand;
				        if (bux != -1337) {
				        	con.prepareStatement("UPDATE profiles SET DBux = "+newbux+" WHERE UserID = "+e.getMember().getUser().getIdLong()).execute();
				        }
				        con.prepareStatement("UPDATE profiles SET DailyBonus = "+Calendar.getInstance().getTimeInMillis()+" WHERE UserID = "+e.getMember().getUser().getIdLong()).execute();
				        e.getChannel().sendMessage("You claimed your daily reward of $**"+rand+"**").queue();
			        } else {
			        	Calendar claimed = Calendar.getInstance();
			        	claimed.setTimeInMillis(daily);
			        	long mins = ChronoUnit.MINUTES.between(cal.toInstant(), claimed.toInstant());
			        	int hours = 0;
			        	while (mins >= 60) {
			        		hours++;
			        		mins = mins - 60;
			        	}
			        	e.getChannel().sendMessage("You have already claimed your daily reward for today! "
			        			+ "You can claim another one in **"+hours+"** hours and **"+mins+"** minutes!").queue();
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
