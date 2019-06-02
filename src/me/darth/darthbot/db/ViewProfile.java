package me.darth.darthbot.db;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ViewProfile extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!profile") || args[0].equalsIgnoreCase("!account") || args[0].equalsIgnoreCase("!p")
			|| args[0].equalsIgnoreCase("!balance") || args[0].equalsIgnoreCase("!bal")) {
			Member target = null;
			try {
				if (!e.getMessage().getMentionedMembers().isEmpty()) {
					target = e.getMessage().getMentionedMembers().get(0);
				} else {
					target = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" ", ""), e.getGuild());
				}
			} catch (ArrayIndexOutOfBoundsException e1) {
				target = e.getMember();
			}
			if (args.length < 2) {
				target = e.getMember();
			}
			if (target == null) {
				e.getChannel().sendMessage(":no_entry: User not found!").queue();
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {

				long xp = 0L;
				int level = 0;
				ResultSet msgs = con.createStatement().executeQuery("SELECT * FROM GuildProfiles");
			      while (msgs.next()) {
				      long UserID = msgs.getLong("UserID");
				      long GuildID = msgs.getLong("GuildID");
				      long rsxp = msgs.getLong("xp");
				      int rslevel = msgs.getInt("Level");
				      if (UserID == target.getUser().getIdLong() && GuildID == e.getGuild().getIdLong()) {
				    	  xp = rsxp;
				    	  level = rslevel;
				      }
			      }
				
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles");
			      boolean found = false;
			      EmbedBuilder eb = new EmbedBuilder(me.darth.darthbot.main.Main.affiliation(target));
			      while (rs.next()) {
				      long UserID = rs.getLong("UserID");
				      long DBux = rs.getLong("DBux");
				      if (UserID == target.getUser().getIdLong()) {
				    	  found = true;
				    	  Member m = e.getGuild().getMemberById(UserID);
				    	  eb.setColor(Color.green);
				    	  eb.setAuthor(target.getEffectiveName()+"'s Profile", null, target.getUser().getEffectiveAvatarUrl());
				    	  eb.setThumbnail(target.getUser().getEffectiveAvatarUrl());
				    	  eb.addField("User", target.getAsMention(), true);
				    	  List<Role> rolesRaw = m.getRoles();
				    	  List<String> roles = new ArrayList<String>();
				    	  int n2 = rolesRaw.size();
				    	  while (n2 > 0) {
				    		  n2 = n2 - 1;
				    		  Role r = rolesRaw.get(n2);
				    		  String mention = r.getAsMention();
								if (!r.equals(e.getGuild().getRoleById("560560525998817300"))) {
									roles.add(mention);
								}
				    	  }
				    	  Collections.reverse(roles);
				    	  if (!roles.isEmpty()) {
				    		  eb.addField("Server Rank", roles.get(0), true);
				    	  } else {
				    		  eb.addField("Server Rank", "Member", true);
				    	  }
				    	  if (DBux == -1337) {
				    		  eb.addField("DBux $$$", "$**"+Character.toString('\u221E')+"**", true);
				    	  } else {
				    		  eb.addField("DBux $$$", "**$"+DBux+"**", true);
				    	  }
				    	  int reqxp = (level + 1) * 100;
				    	  if (reqxp > xp) {
				    		  eb.addField("Chat Level", "Level "+level+" *("+xp+"/"+reqxp+"*xp*)*", true);
				    	  } else {
				    		  eb.addField("Chat Level", "⬆**Level Up!**⬆", true); 
				    	  }
				    	  eb.addField("What can I use DBux for?", "```> Win big at the !casino\n\nComing soon:\n- Work Jobs\n- Rob Others\n- Inventories\n- Store```", false);
				    	  PreparedStatement st = con.prepareStatement("UPDATE profiles SET Name = ? WHERE UserID = "+target.getUser().getIdLong());
				    	  st.setString(1, target.getEffectiveName());
				    	  st.executeUpdate();
				      }
			      }
			      if (found) {
			    	  e.getChannel().sendMessage(eb.build()).queue();
			    	  return;
			      } else {
					  Message msg = e.getChannel().sendMessage("I didn't find a profile for that user, creating one now: `Status: Creating...`").complete();
					  String query = " INSERT INTO profiles (UserID, Name, DBux)"
					    + " values (?, ?, ?)";
					  java.sql.PreparedStatement s = con.prepareStatement(query);
					  int bux = 0;
					  s.setLong(1, target.getUser().getIdLong());
					  s.setString(2, target.getEffectiveName());
					  s.setInt(3, bux);
					  s.execute();
					  msg.editMessage("I didn't find a profile for that user, creating one now: `Status: Profile Created`").queue();
						
			      }
			      rs.close();
			      con.close();
			
			} catch (SQLException e1) {
			    e.getChannel().sendMessage("<@159770472567799808> Error! ```"+e1+"```").queue();
			}
			
		}
	}
}
