package me.darth.darthbot.moderation;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Kick extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getGuild().getId().equals("393499439739961366")) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!kick")) {
			Member target = null;
			if (!e.getMessage().getMentionedMembers().isEmpty()) {
				target = e.getMessage().getMentionedMembers().get(0);
			} else {
				me.darth.darthbot.main.Main.findUser(args[1], e.getGuild());
			}
			if (target == null) {
				e.getChannel().sendMessage("User not found!").queue();
				return;
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
			      TextChannel logchannel = null;
			      while (rs.next())
			      {
			        long ModRoleID = rs.getLong("Moderator");
			        logchannel = e.getGuild().getTextChannelById(rs.getLong("LogChannel"));
			        if (ModRoleID == 0L) {
			        	e.getChannel().sendMessage("You must setup the staff role before using the moderation system! `(!setup StaffRole <role>)`").queue();
			        	return;
			        }
			        if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
			        	MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you must have the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role to use that command!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(eb).queue();
			        	return;
			        }
					if (target.getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
						MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you cannot kick users with the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(eb).queue();
			        	return;
					}
			      }
			      
			   
			      String reason = e.getMessage().getContentRaw().replace(args[0], "").replace(args[1], "");
			      if (reason.replace(" ", "").isEmpty()) {
			    	  reason = "No Reason Provided";
			      }
			      final String finalreason = reason;
			      
			     target.getUser().openPrivateChannel().queue((channel) ->
			        {
			        	EmbedBuilder eb = new EmbedBuilder().setAuthor("You have been kicked!", null, e.getGuild().getIconUrl()).setDescription("You have been "
			        		+ "kicked from **"+e.getGuild().getName()+"** by **"+e.getMember().getUser().getName()+"#"+e.getMember().getUser().getDiscriminator()
			        		+ "** because `"+finalreason+"`").setThumbnail(e.getGuild().getIconUrl()).setColor(Color.red);
						try {
							channel.sendMessage(eb.build()).embed(eb.build()).submit().get();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			
			        });
			      try {
			    	  e.getGuild().getController().kick(target, "[By "+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator()+"] "+reason).reason("[By "+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator()+"] "+reason).queue();
			        } catch (HierarchyException e1) {
			        	e.getChannel().sendMessage(":no_entry: I can't punish that user!").queue();
			        	return;
			        }
			    	 EmbedBuilder eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you have kicked **"+target.getUser().getName()
		  						+"#"+target.getUser().getDiscriminator()+"** due to `"+reason+"`").setColor(Color.green);
	  				con.prepareStatement("INSERT INTO ModHistory (Timestamp, GuildID, PunishedID, PunisherID, Type, Reason)  values "
				      		+ "("+System.currentTimeMillis()+", "+e.getGuild().getIdLong()+", "+target.getUser().getIdLong()+", "+e.getAuthor().getIdLong()
				      		+ ", 'KICK', '"+reason+"')").execute();
		
		          	e.getChannel().sendMessage(eb.build()).queue();
	  			    eb = new EmbedBuilder().setAuthor("Member Kicked", null, target.getUser().getEffectiveAvatarUrl()).setDescription("User "+target.getAsMention()+" "
	  			    		+ "has been kicked").addField("Kicked by", e.getMember().getAsMention(), true).addField("Reason", reason, false).setTimestamp(Instant.from(ZonedDateTime.now()))
	  			    		.setFooter("User ID"+target.getUser().getId(), null).setColor(Color.red);
	  			    logchannel.sendMessage(eb.build()).queue();
		          	
		          	
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
			
		}
		
	}

}
