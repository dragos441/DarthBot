package me.darth.darthbot.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.mysql.jdbc.PreparedStatement;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class logMessages extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		try {
			if (e.getMessage().getContentRaw() != null && e.getMessage().getContentRaw() != "" && !e.getAuthor().isBot() 
					&& e.getMessage().getContentRaw().toCharArray()[0] != '!') { 
				try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
					String query1 = " INSERT INTO messageLog (GuildID, Timestamp, MessageID, AuthorID, ChannelID, Content)"
					        + " values (?, ?, ?, ?, ?, ?)";
					java.sql.PreparedStatement s1 = con.prepareStatement(query1);
					s1.setLong(1, e.getGuild().getIdLong());
					s1.setLong(2, System.currentTimeMillis());
					s1.setLong(3, e.getMessageIdLong());
					s1.setLong(4, e.getAuthor().getIdLong());
					s1.setLong(5, e.getChannel().getIdLong());
					s1.setString(6, e.getMessage().getContentRaw());
					s1.execute();
	
					
					con.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		} catch (ArrayIndexOutOfBoundsException e1) {return;}
		if (args[0].equalsIgnoreCase("!findmsg") || args[0].equalsIgnoreCase("!quote")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/retali8", "root", "57d9c35a8160b5644e032d8a10d37324")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM messageLog");
			      boolean found = false;
			      EmbedBuilder qeb = new EmbedBuilder();
			      while (rs.next())
			      {
			        long Timestamp = rs.getLong("Timestamp");
			        long MessageID = rs.getLong("MessageID");
			        long AuthorID = rs.getLong("AuthorID");
			        long ChannelID = rs.getLong("ChannelID");
			        String Content = rs.getString("Content");
			        
			        try {
			        	if (args[0].equalsIgnoreCase("!findmsg") && MessageID == Long.parseLong(args[1]) && !found) {
			        		found = true;
			        		EmbedBuilder eb = new EmbedBuilder();
			        		eb.setAuthor("Message by "+e.getGuild().getMemberById(AuthorID).getEffectiveName()+" in #"
			        		+e.getGuild().getTextChannelById(ChannelID).getName(), null, 
			        			e.getGuild().getMemberById(AuthorID).getUser().getAvatarUrl());
			        		eb.setDescription("**Message:** "+Content);
			        		eb.setFooter("Message ID: "+MessageID, null);
			        		eb.setTimestamp(Instant.ofEpochMilli(Timestamp));
			        		e.getChannel().sendMessage(eb.build()).queue();
			        		return;
			        	} if (args[0].equalsIgnoreCase("!quote") && !e.getGuild().getCategoryById("393520273015701536").getChannels()
					        	.contains(e.getGuild().getTextChannelById(ChannelID)) && e.getMessage().getContentRaw().replace(args[0]+" ", "").toLowerCase()
					        	.equals(Content.toLowerCase())) {
			        		found = true;
			        		try {
				        		qeb.setAuthor("Message by "+e.getGuild().getMemberById(AuthorID).getEffectiveName()+" deleted in #"
				        		+e.getGuild().getTextChannelById(ChannelID).getName(), null, 
				        			e.getGuild().getMemberById(AuthorID).getUser().getAvatarUrl());
			        		} catch (NullPointerException e1) {
			        			qeb.setAuthor("Message by "+e.getGuild().getMemberById(AuthorID).getEffectiveName()+" deleted in #"
						        		+e.getGuild().getTextChannelById(ChannelID).getName(), null, 
						        			e.getGuild().getIconUrl());
			        		}
			        		qeb.setDescription("**Message:** "+Content);
			        		qeb.setFooter("Message ID: "+MessageID, null);
			        		qeb.setTimestamp(Instant.ofEpochMilli(Timestamp));
			        	}
			        } catch (ArrayIndexOutOfBoundsException e2) {}
			   
			      }
			      rs.close();
			      con.close();
			      if (found) {
			    	  e.getChannel().sendMessage(qeb.build()).queue();
			      } else {
			    	  e.getChannel().sendMessage("Message not found!").queue();
			      }
				
				
			} catch (SQLException e1) {
			    e.getChannel().sendMessage("Error! ```"+e1+"```").queue();
			}
		}
	}
}
