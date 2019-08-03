package me.darth.darthbot.db;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ProfileGen extends ListenerAdapter {
	
	
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			
			ResultSet rs = con.prepareStatement("SELECT * FROM GuildProfiles WHERE UserID = "+e.getUser().getId()+" AND GuildID = "+e.getGuild().getIdLong()).executeQuery();
			ResultSet rewards = con.prepareStatement("SELECT * FROM RoleRewards WHERE GuildID = "+e.getGuild().getId()).executeQuery();
			while (rs.next()) {
				List<Role> roles = new ArrayList<Role>();
				int level = rs.getInt("Level");
				while (rewards.next()) {
					if (rewards.getInt("Level") <= level) {
						roles.add(e.getGuild().getRoleById(rewards.getLong("RoleID")));
					}
				}
				if (!roles.isEmpty()) {
					e.getGuild().getController().addRolesToMember(e.getMember(), roles).queue();
				}
			}
			
			ResultSet punished = con.prepareStatement("SELECT * FROM ModHistory WHERE PunishedID = "+e.getUser().getId()+" AND GuildID = "+e.getGuild().getId()+" AND Active = 1").executeQuery();
			while (punished.next()) {
				if (punished.getString("Type").equalsIgnoreCase("MUTE") || punished.getString("Type").equalsIgnoreCase("TEMPMUTE")) {
					e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRolesByName("Muted", true).get(0)).queue();
				}
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		/*if (e.getMessage().getContentRaw().contains("gen")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/retali8", "root", "57d9c35a8160b5644e032d8a10d37324")) {

				List<Member> members = e.getGuild().getMembers();
				for(int x = 0 ; x < members.size() ; x++) {
					Member m = members.get(x);
					String query = " INSERT INTO profiles (UserID, Name, GuildProfiles, RetaliBux)"
					        + " values (?, ?, ?, ?)";
		    	  	java.sql.PreparedStatement s = con.prepareStatement(query);
					s.setLong(1, m.getUser().getIdLong());
					s.setString(2, m.getEffectiveName());
					s.setInt(3, 0);
					s.setInt(4, 0);
					s.execute();
				}
				e.getChannel().sendMessage("All user profiles generated!").queue();
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}*/
		
		try {
			if (e.getAuthor().isBot()) {
				return;
			}
		}
		catch (NullPointerException e1) {
			return;
		}
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {

			ResultSet guild = con.createStatement().executeQuery("SELECT * FROM GuildProfiles");
			long GuildID = -1;
		      boolean found = false;
		      while (guild.next())
		      {
		        long UserID = guild.getLong("UserID");
		        GuildID = guild.getLong("GuildID");
		        if (UserID == e.getAuthor().getIdLong() && GuildID == e.getGuild().getIdLong()) {
		        	found = true;
		        }
		      }
		      if (!found) {
		    	  con.prepareStatement("INSERT INTO GuildProfiles (GuildID, UserID)"
					        + " values ('"+e.getGuild().getIdLong()+"', '"+e.getMember().getUser().getIdLong()+"');").execute();
		    	  	EmbedBuilder eb = new EmbedBuilder().setAuthor("Database - Message Account Generated", null, e.getMember().getUser().getEffectiveAvatarUrl())
							.setDescription(e.getMember().getAsMention()+"'s profile was generated").setColor(Color.green)
							.setFooter("Guild ID: "+e.getGuild().getId()+" ("+e.getGuild().getName()+")", null);
							me.darth.darthbot.main.Main.sm.getTextChannelById("569883444126023680").sendMessage(eb.build()).queue();
		      }
	    	  ResultSet rewards = con.createStatement().executeQuery("SELECT * FROM RoleRewards WHERE GuildID = "+e.getGuild().getId()+" AND Level = 0");
	    	  while (rewards.next()) {
	    		  e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById(rewards.getLong("RoleID"))).queue();
	    	  }
		      found=false;
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles");
		      while (rs.next())
		      {
		        long UserID = rs.getLong("UserID");
		        if (UserID == e.getAuthor().getIdLong()) {
		        	found = true;
		        	if (!rs.getString("Name").equalsIgnoreCase(e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator())) {
		        		PreparedStatement ps = con.prepareStatement("UPDATE profiles SET Name = ? WHERE UserID = "+e.getAuthor().getIdLong());
		        		ps.setString(1, e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator());
		        		ps.execute();
		        	}
		        }
		      }
		      if (!found) {
		    	  	con.prepareStatement("INSERT INTO profiles (UserID, Name, DBux)"
					        + " values ('"+e.getAuthor().getIdLong()+"', '"+e.getMember().getEffectiveName()+"', '100');").execute();
		    	  	EmbedBuilder eb = new EmbedBuilder().setAuthor("Database - Account Generated", null, e.getMember().getUser().getEffectiveAvatarUrl())
							.setDescription(e.getMember().getAsMention()+"'s profile was generated (Method 1)").setColor(Color.green)
							.setFooter("User ID: "+e.getMember().getUser().getId(), null);
							me.darth.darthbot.main.Main.sm.getTextChannelById("569883444126023680").sendMessage(eb.build()).queue();
					
		      }
		      rs.close();
		      con.close();
			
		} catch (SQLException e1) {
		   e1.printStackTrace();
		}
		
	}

}
