package me.darth.darthbot.main;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class PublicRooms extends ListenerAdapter {

	public static boolean availableChannel(Channel c, Category category) {
		List<VoiceChannel> channels = category.getVoiceChannels();
		for(int x = 0 ; x < channels.size() ; x++) {
			if (channels.get(x).getMembers().isEmpty() && !channels.get(x).equals(c)) {
				return true;
			}
		}
		return false;
	}
	
	public static int channelNum(Category category) {
		List<VoiceChannel> channels = category.getVoiceChannels();
		int num = 1;
		while (true) {
			int firstnum = num;
			for(int x = 0 ; x < channels.size() ; x++) {
				if (Integer.parseInt(channels.get(x).getName().replace("Voice #", "")) == num) {
					num++;
				}
			}
			if (firstnum == num) {
				return num;
			}
		}
	}
	
	public void deleteChannel(Channel c, Category category) {
		if (availableChannel(c, category) == true) {
			List<VoiceChannel> channels = category.getVoiceChannels();
			Channel toDelete = null;
			for(int x = 0 ; x < channels.size() ; x++) {
				if (channels.get(x).getMembers().isEmpty()) {
					if (toDelete == null || Integer.parseInt(channels.get(x).getName().replace("Voice #", "")) > Integer.parseInt(toDelete.getName().replace("Voice #", ""))) {
						toDelete = channels.get(x);
					}
				}
			}
			if (toDelete != null) {
				toDelete.delete().reason("[Auto] Public Voice Channel Remove").queue();
			}
		}
	}
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		Category category = null;
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
		      while (rs.next()) {
		    	  long categoryID = rs.getLong("PublicCategory");
		    	  if (categoryID != 0L) {
		    		  category = e.getGuild().getCategoryById(categoryID);
		    	  } else {
		    		  return;
		    	  }
		      }
		      rs.close();
		      con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (!category.getVoiceChannels().contains(e.getChannelJoined()) || e.getMember().getUser().isBot()) {
			return;
		}
		boolean availableChannel = availableChannel(e.getChannelJoined(), category);
		if (!availableChannel) {
			category.createVoiceChannel("Voice #"+channelNum(category)).reason("[Auto] Public Voice Channel Add").queue();
		}
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		Category category = null;
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
		      while (rs.next()) {
		    	  long categoryID = rs.getLong("PublicCategory");
		    	  if (categoryID != 0L) {
		    		  category = e.getGuild().getCategoryById(categoryID);
		    	  } else {
		    		  return;
		    	  }
		      }
		      rs.close();
		      con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (!category.getVoiceChannels().contains(e.getChannelLeft()) || e.getMember().getUser().isBot()) {
			return;
		}
		deleteChannel(e.getChannelLeft(), category);
		
	}
	
	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
		Category category = null;
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
		      while (rs.next()) {
		    	  long categoryID = rs.getLong("PublicCategory");
		    	  if (categoryID != 0L) {
		    		  category = e.getGuild().getCategoryById(categoryID);
		    	  } else {
		    		  return;
		    	  }
		      }
		      rs.close();
		      con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (category.getVoiceChannels().contains(e.getChannelJoined()) && !e.getMember().getUser().isBot()) {
			boolean availableChannel = availableChannel(e.getChannelJoined(), category);
			if (!availableChannel) {
				category.createVoiceChannel("Voice #"+channelNum(category)).reason("[Auto] Public Voice Channel Add").queue();
			}
		}
		if (category.getVoiceChannels().contains(e.getChannelLeft()) && !e.getMember().getUser().isBot()) {
			deleteChannel(e.getChannelLeft(), category);
		}
		
	}
	
}
