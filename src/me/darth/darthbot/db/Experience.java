package me.darth.darthbot.db;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Experience extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {

			ResultSet msgs = con.createStatement().executeQuery("SELECT * FROM GuildProfiles");
		    while (msgs.next()) {
		    	long UserID = msgs.getLong("UserID");
				long GuildID = msgs.getLong("GuildID");
				long lastearned = msgs.getLong("xpGained");
				long xp = msgs.getLong("xp");
				int level = msgs.getInt("Level");
				if (UserID == e.getMember().getUser().getIdLong() && GuildID == e.getGuild().getIdLong()) {
					if (new Date().getMinutes() != lastearned) {
				    	  lastearned = new Date().getMinutes();
				    	  long newxp = new Random().nextInt((12 - 8) + 1) + 12;
				    	  long reqxp = (level + 1) * 100;
				    	  if (xp >= reqxp) {
				    		  int prevlevel = level;
				    		  level++;
				    		  xp = xp - reqxp;
				    		  e.getChannel().sendMessage("Congrats "+e.getMember().getAsMention()+", you advanced from **Level "+prevlevel+"** :arrow_right: **Level "+level+"**!").queue();
				    	  }
				    	  xp = xp + newxp;
				    	  con.prepareStatement("UPDATE GuildProfiles SET xp = "+xp+", xpGained = "+lastearned+", Level = "+level+" WHERE UserID = "+e.getMember().getUser().getIdLong()+" AND GuildID = "+e.getGuild().getIdLong()).execute();
				      }
				}
		     }
		      
		      
		      
		      con.close();
		
		} catch (SQLException e1) {
		    e.getChannel().sendMessage("<@393796810918985728> Error! ```"+e1+"```").queue();
		}
	}
	
}
