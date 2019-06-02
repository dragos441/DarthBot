package me.darth.darthbot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Experience extends ListenerAdapter {

	@SuppressWarnings("deprecation")
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getMessage().getAuthor().isFake()) {
			return;
		}
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {

			ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (rs.next()) {
				if (rs.getInt("doExperience") == 0) {
					return;
				}
			}
			ResultSet msgs = con.createStatement().executeQuery("SELECT * FROM GuildProfiles WHERE GuildID = "+e.getGuild().getId());
		    while (msgs.next()) {
		    	long UserID = msgs.getLong("UserID");
				long GuildID = msgs.getLong("GuildID");
				long lastearned = msgs.getLong("xpGained");
				long xp = msgs.getLong("xp");
				int level = msgs.getInt("Level");
				if (UserID == e.getMember().getUser().getIdLong() && GuildID == e.getGuild().getIdLong() && e.getMessage().toString().toCharArray()[0] != '!') {
					if (new Date().getMinutes() != lastearned) {
				    	  lastearned = new Date().getMinutes();
				    	  long newxp = new Random().nextInt((12 - 8) + 1) + 12;
				    	  long reqxp = (level + 1) * 100;
				    	  if (xp >= reqxp) {
				    		  int prevlevel = level;
				    		  level++;
				    		  xp = xp - reqxp;
				    		  e.getChannel().sendMessage("Congrats "+e.getMember().getAsMention()+", you advanced from **Level "+prevlevel+"** :arrow_right: **Level "+level+"**!").complete().delete().queueAfter(2, TimeUnit.MINUTES);
				    	  }
				    	  xp = xp + newxp;
				    	  con.prepareStatement("UPDATE GuildProfiles SET xp = "+xp+", xpGained = "+lastearned+", Level = "+level+" WHERE UserID = "+e.getMember().getUser().getIdLong()+" AND GuildID = "+e.getGuild().getIdLong()).execute();
				      }
				}
		     }
		      
		      
		      
		      con.close();
		
		} catch (SQLException e1) {
		    e.getChannel().sendMessage("<@159770472567799808> Error! ```"+e1+"```").queue();
		}
	}
	
}
