package me.darth.darthbot.main;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Leaderboards extends ListenerAdapter {

	public static MessageEmbed lastUpdated = null;
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!leaderboard")) {
			if (lastUpdated == null) {
				e.getChannel().sendMessage(":timer: The Leaderboard is updated every 5 minutes, please wait a few minutes and try this command again!").queue();
				return;
			}
			e.getChannel().sendMessage(lastUpdated).queue();
		}
		
	}

	public static void Retali8Leaderboard() {
		Guild g = me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940");
		EmbedBuilder eb = new EmbedBuilder().setAuthor("Retali8 Leaderboard", null, g.getIconUrl()).setColor(Color.blue).setThumbnail(g.getIconUrl())
				.setTimestamp(Instant.from(ZonedDateTime.now())).setFooter("Last Updated", null);
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles ORDER BY `DBux` DESC");
		      int counter = 1;
		      while (counter <= 15 && rs.next()) {
		    	  if (me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getMemberById(rs.getLong("UserID")) != null) {
		    		  String dbux = new DecimalFormat("#,###").format(rs.getLong("DBux"));
			    	  String m = null;
			    	  if (g.getMemberById(rs.getLong("UserID")) == null) {
			    		  m=rs.getString("Name");
			    	  } else {
			    		  m=g.getMemberById(rs.getLong("UserID")).getAsMention();
			    		  if (counter == 1) {
			    			  
			    			  List<Member> prevrichest = g.getMembersWithRoles(g.getRoleById("585903393382465537"));
			    			  if (prevrichest.isEmpty()) {
			    				  g.getController().addSingleRoleToMember(g.getMemberById(rs.getLong("UserID")), g.getRoleById("585903393382465537")).queue();
			    			  } else if (!prevrichest.get(0).equals(g.getMemberById(rs.getLong("UserID")))) {
			    				  g.getController().removeSingleRoleFromMember(prevrichest.get(0), g.getRoleById("585903393382465537")).queue();
				    			  g.getController().addSingleRoleToMember(g.getMemberById(rs.getLong("UserID")), g.getRoleById("585903393382465537")).queue();
			    			  }
			    		  }
			    	  }
			    	  if (counter == 1) {
			    		  eb.addField("🥇 1st Place 🥇", "👑**"+m+"** with `$"+dbux+"`", false);
			    	  } else if (counter == 2) {
			    		  eb.addField("🥈 2nd Place 🥈", "**"+m+"** with `$"+dbux+"`", false);
			    	  } else if (counter == 3) {
			    		  eb.addField("🥉 3rd Place 🥉", "**"+m+"** with `$"+dbux+"`", false);
			    	  } else {
			    		  eb.addField(counter+"th Place", "**"+m+"** with `$"+dbux+"`", false);
			    	  }
			    	  counter++; 
		    	  }
		      }
		      rs.close();
		      con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		me.darth.darthbot.main.Main.sm.getGuildById("393499439739961366").getTextChannelById("569277372789162015")
			.getMessageById("587353035546558474").complete().editMessage(eb.build()).queue();
		
	}
	
	public static void GlobalLeaderboard() {
		Guild g = me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940");
		EmbedBuilder eb = new EmbedBuilder().setAuthor("Global Leaderboard", null, g.getIconUrl()).setColor(Color.blue).setThumbnail(g.getIconUrl())
				.setTimestamp(Instant.from(ZonedDateTime.now())).setFooter("Last Updated", null);
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles ORDER BY `DBux` DESC LIMIT 15");
		      int counter = 1;
		      while (counter <= 15 && rs.next()) {
		    	  String dbux = new DecimalFormat("#,###").format(rs.getLong("DBux"));
		    	  String m = null;
		    	  if (g.getMemberById(rs.getLong("UserID")) == null) {
		    		  m=rs.getString("Name");
		    	  } else {
		    		  m=g.getMemberById(rs.getLong("UserID")).getAsMention();
		    		  if (counter == 1) {
		    			  
		    			  List<Member> prevrichest = g.getMembersWithRoles(g.getRoleById("585903393382465537"));
		    			  if (prevrichest.isEmpty()) {
		    				  g.getController().addSingleRoleToMember(g.getMemberById(rs.getLong("UserID")), g.getRoleById("585903393382465537")).queue();
		    			  } else if (!prevrichest.get(0).equals(g.getMemberById(rs.getLong("UserID")))) {
		    				  g.getController().removeSingleRoleFromMember(prevrichest.get(0), g.getRoleById("585903393382465537")).queue();
			    			  g.getController().addSingleRoleToMember(g.getMemberById(rs.getLong("UserID")), g.getRoleById("585903393382465537")).queue();
		    			  }
		    		  }
		    	  }
		    	  if (counter == 1) {
		    		  eb.addField("🥇 1st Place 🥇", "👑**"+m+"** with `$"+dbux+"`", false);
		    	  } else if (counter == 2) {
		    		  eb.addField("🥈 2nd Place 🥈", "**"+m+"** with `$"+dbux+"`", false);
		    	  } else if (counter == 3) {
		    		  eb.addField("🥉 3rd Place 🥉", "**"+m+"** with `$"+dbux+"`", false);
		    	  } else {
		    		  eb.addField(counter+"th Place", "**"+m+"** with `$"+dbux+"`", false);
		    	  }
		    	  counter++;
		      }
		      lastUpdated = eb.build();
		      rs.close();
		      con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getTextChannelById("584150264509104139")
			.getMessageById("584150350899052558").complete().editMessage(eb.build()).queue();
		
	}
	
}
