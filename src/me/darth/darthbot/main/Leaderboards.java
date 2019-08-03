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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Leaderboards extends ListenerAdapter {

	public static MessageEmbed lastUpdated = null;
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!leaderboard") || args[0].equalsIgnoreCase("!lb") || args[0].equalsIgnoreCase("!chatleaderboard") || args[0].equalsIgnoreCase("!cl")
				|| args[0].equalsIgnoreCase("!moneyleaderboard") || args[0].equalsIgnoreCase("!ml")) {
			try {
				if (args[0].equalsIgnoreCase("!cl") || args[0].equalsIgnoreCase("!chatleaderboard") || args[1].equalsIgnoreCase("chat")) {
					MessageEmbed me = ChatLeaderboard(e.getGuild());
					EmbedBuilder eb = new EmbedBuilder(me).setFooter("Snapshot of Chat Leaderboard - Accurate as of", null)
							.setTimestamp(Instant.from(ZonedDateTime.now()));
					if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
						eb.addField("*Looking to make an automatically updating leaderboard?*", "*Use `!setup ChatLeaderboard`*", false);
					}
					e.getChannel().sendMessage(eb.build()).queue();
				} else if (args[0].equalsIgnoreCase("!moneyleaderboard") || args[0].equalsIgnoreCase("!ml") || args[1].equalsIgnoreCase("money")) {
					GlobalLeaderboard();
					e.getChannel().sendMessage(lastUpdated).queue();
					
				} else {
					e.getChannel().sendMessage(":no_entry: You must choose whether you view the `CHAT` or `MONEY` leaderboard\n`!leaderboard CHAT`\n`!leaderboard MONEY`").queue();
					return;
				}
			} catch (ArrayIndexOutOfBoundsException e1) {
				e.getChannel().sendMessage(":no_entry: You must choose whether you view the `CHAT` or `MONEY` leaderboard!\n`!leaderboard CHAT`\n`!leaderboard MONEY`").queue();
				return;
			}
		}
		
	}

		
	
	public static MessageEmbed ChatLeaderboard(Guild g) {
		EmbedBuilder eb = new EmbedBuilder().setAuthor("Chat Leaderboard", null, g.getIconUrl()).setColor(Color.green).setTimestamp(Instant.from(ZonedDateTime.now()));
		try {
			eb.setThumbnail(g.getIconUrl());
		} catch (NullPointerException e1) {}
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildProfiles WHERE GuildID = "+g.getId()+" ORDER BY Level DESC, xp DESC");
		      int counter = 1;
		      while (counter <= 10 && rs.next()) {
		    	  String m = null;
		    	  int level = rs.getInt("Level");
		    	  boolean fU = true;
		    	  try {
		    		  m=g.getMemberById(rs.getLong("UserID")).getAsMention();
		    	  } catch (NullPointerException e1) {
		    		  fU=false;
		    	  }
		    	  
		    	  String visual = "";
		    	  int levelup = (level + 1) * 100;
		    	  int xp = rs.getInt("xp");
		    	  int xpcount = 0;
		    	  for (int x = 1 ; x <= 5 ; x++) {
		    		  xpcount = xpcount + ((levelup / 10) * 2);
		    		  if (xp >= xpcount) {
		    			  visual=visual+":white_small_square:";
		    		  } else {
		    			  visual=visual+":black_small_square:";
		    		  }
		    	  }
		    	  
		    	  if (fU) {
			    	  m=g.getMemberById(rs.getLong("UserID")).getAsMention();
			    	  if (counter == 1) {
			    		  eb.addField("🥇 1st Place 🥇", "👑**"+m+"** at Level `"+level+"`"+visual, false);
			    	  } else if (counter == 2) {
			    		  eb.addField("🥈 2nd Place 🥈", "**"+m+"** at Level `"+level+"`"+visual, false);
			    	  } else if (counter == 3) {
			    		  eb.addField("🥉 3rd Place 🥉", "**"+m+"** at Level `"+level+"`"+visual, false);
			    	  } else {
			    		  eb.addField(counter+"th Place", "**"+m+"** at Level `"+level+"`"+visual, false);
			    	  }
			    	  counter++;
		    	  }
	    	  }
		      rs.close();
		      con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return eb.build();
		
	}
	
	public static void Retali8Leaderboard() {
		Guild g = me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940");
		EmbedBuilder eb = new EmbedBuilder().setAuthor("Retali8 Leaderboard", null, g.getIconUrl()).setColor(Color.green).setThumbnail(g.getIconUrl())
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
		      while (rs.next()) {
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
		
	}
	
}
