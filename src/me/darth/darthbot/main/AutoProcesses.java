package me.darth.darthbot.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class AutoProcesses {

	public static void chatLeaderboards() {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM ChatLeaderboards");
		      while (rs.next()) {
		    	  Guild g = me.darth.darthbot.main.Main.sm.getGuildById(rs.getLong("GuildID"));
		    	  if (g != null) {
			    	  MessageEmbed leaderboard = me.darth.darthbot.main.Leaderboards.ChatLeaderboard(g);
			    	  Message msg = me.darth.darthbot.main.Main.sm.getTextChannelById(rs.getLong("ChannelID")).getMessageById(rs.getLong("MessageID")).complete();
			    	  msg.editMessage(leaderboard).queue();
		    	  }
		      }
		      rs.close();
		      con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}	
	}
	
	public static void removePunishments() {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM ModHistory WHERE Active = 1 AND Expires <= "+Calendar.getInstance().getTimeInMillis());
		      while (rs.next()) {
		    	  int active = rs.getInt("Active");
		    	  long expires = rs.getLong("Expires");
		    	  long guildID = rs.getLong("GuildID");
		    	  long punishedID = rs.getLong("PunishedID");
		    	  String type = rs.getString("Type");
	    		  try {
		    		  if (type.equals("TEMPMUTE")) {
		    			  Guild g = me.darth.darthbot.main.Main.sm.getGuildById(guildID);
		    			  g.getController().removeSingleRoleFromMember(g.getMemberById(punishedID), g.getRolesByName("Muted", true).get(0)).queue();
		    		  } else if (type.equals("TEMPBAN")) {
		    			  Guild g = me.darth.darthbot.main.Main.sm.getGuildById(guildID);
		    			  g.getController().unban(punishedID+"").queue();
		    		  }
	    		  } catch (IllegalArgumentException | NullPointerException | IndexOutOfBoundsException e1) {e1.printStackTrace();}
	    		  con.prepareStatement("UPDATE ModHistory SET Active = 0 WHERE GuildID = "+guildID+" AND PunishedID = "+punishedID+" AND Type = '"+type+"'").execute();
		      }
		      rs.close();
		      con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}	
	}
	
	public static void purgeMessages() {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
		    con.prepareStatement("DELETE FROM messageLog WHERE Timestamp < "+cal.getTimeInMillis()).execute();
		    con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}	
	}
	
	public static void leaveEmptyChannels() {
		List<Guild> guilds = me.darth.darthbot.main.Main.sm.getGuilds();
		for (int x = 0 ; x < guilds.size() ; x++) {
			Guild g = guilds.get(x);
			if (g.getAudioManager().isConnected() && g.getAudioManager().getConnectedChannel().getMembers().size() == 1) {
				g.getAudioManager().closeAudioConnection();
			}
		}
	}
	
}
