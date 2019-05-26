package me.darth.darthbot.moderation;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class History extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getGuild().getId().equals("393499439739961366")) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!history") || args[0].equalsIgnoreCase("!his")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
			      while (rs.next())
			      {
			        long ModRoleID = rs.getLong("Moderator");
			        if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
			        	MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you must have the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role to use that command!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(eb).queue();
			        	return;
			        }
			      }
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
			Member target = null;
			if (args.length < 2) {
				target = e.getMember();
			} else if (!e.getMessage().getMentionedMembers().isEmpty()) {
				target = e.getMessage().getMentionedMembers().get(0);
			} else {
				target = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" ", ""));
			}
			
			if (target == null) {
				e.getChannel().sendMessage(":no_entry: Member not found!").queue();
				return;
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM ModHistory WHERE PunishedID = "+target.getUser().getIdLong()+" AND GuildID = "+e.getGuild().getIdLong());
			      EmbedBuilder eb = new EmbedBuilder().setColor(Color.red);
			      Map<Integer, String> map = new HashMap<>();
			      int counter = 0;
			      while (rs.next()) {
			    	  String type = rs.getString("Type");
			    	  String punisher = "<@"+rs.getLong("PunisherID")+">";
			    	  String reason = rs.getString("Reason");
			    	  long expiry = rs.getLong("Expires");
			    	  Calendar cal = Calendar.getInstance();
			    	  cal.setTimeInMillis(rs.getLong("Timestamp"));
			    	  map.put(9, map.get(8));
			    	  map.put(8, map.get(7));
			    	  map.put(7, map.get(6));
			    	  map.put(6, map.get(5));
			    	  map.put(5, map.get(4));
			    	  map.put(4, map.get(3));
			    	  map.put(3, map.get(2));
			    	  map.put(2, map.get(1));
			    	  map.put(1, map.get(0));
			    	  map.put(0, type+","+cal.getTimeInMillis()+","+reason+","+punisher+","+expiry);
			    	  counter++;
			      }
			      eb.setAuthor(target.getEffectiveName()+"'s Punishment History ("+map.size()+"/"+counter+")", null, target.getUser().getEffectiveAvatarUrl());
			      for (int x = 0 ; x < 9 ; x++) {
			    	  try {
				    	  String[] data = map.get(x).split(",");
				    	  String type = data[0];
				    	  long time = Long.parseLong(data[1]);
				    	  Calendar cal = Calendar.getInstance();
				    	  cal.setTimeInMillis(time);
				    	  String reason = data[2];
				    	  String punisher = data[3];
				    	  String ft = cal.getTime().getDate()+"/"+(cal.getTime().getMonth() < 10 ? "0" : "") + cal.getTime().getMonth()+"/"+Math.subtractExact(cal.getTime().getYear(), 100)+" @ "+cal.getTime().getHours()+":"+(cal.getTime().getMinutes() < 10 ? "0" : "") + cal.getTime().getMinutes();
				    	  if (type.equals("BAN")) {
				    		  eb.addField("Banned on "+ft, "**Reason:** `"+reason+"` **by "+punisher+"**", false);
				    	  } else if (type.equals("KICK")) {
				    		  eb.addField("Kicked on "+ft, "**Reason:** `"+reason+"` **by "+punisher+"**", false);
				    	  } else if (type.equals("TEMPBAN")) {
				    		  Calendar exp = Calendar.getInstance();
				    		  exp.setTimeInMillis(Long.parseLong(data[4]));
				    		  boolean active = true;
				    		  if (new Date().getTime() > exp.getTimeInMillis()) {
				    			  active=false;
				    		  }
				    		  eb.addField("Temporarily Banned on "+ft+" (Active: "+active+")", "**Reason:** `"+reason+"` **by "+punisher+"**\n**Expires:** "+exp.getTime(), false);
				      	  } else {
				    		  eb.addField(type+" @ "+ft, "**Reason:** `"+reason+"` **by "+punisher+"**", false);
				    	  }
			    	  } catch (NullPointerException e1) {}
			      }
			      if (eb.getFields().size() == 0) {
			    	 eb.setTitle(":angel: No punishments found!");
			    	 eb.setColor(Color.green);
			      }
			      e.getChannel().sendMessage(eb.build()).queue();
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
		}
		
	}
}
