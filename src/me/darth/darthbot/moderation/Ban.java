package me.darth.darthbot.moderation;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Ban extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		if (e.getGuild().getId().equals("393499439739961366")) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!unban")) {
			if (args.length < 2) {
				e.getChannel().sendMessage("Invalid Syntax: `!unban <User>`").queue();
				return;
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
			      while (rs.next())
			      {
			        long ModRoleID = rs.getLong("Moderator");
			        if (ModRoleID == 0L) {
			        	e.getChannel().sendMessage("You must setup the staff role before using the moderation system! `(!setup Moderation <role>)`").queue();
			        	return;
			        }
			        if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
			        	MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you must have the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role to use that command!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(eb).queue();
			        	return;
			        }
			        List<net.dv8tion.jda.core.entities.Guild.Ban> banlist = e.getGuild().getBanList().complete();
			        String tounban = e.getMessage().getContentRaw().replace(args[0]+" ", "");
		        	for (int x = 0 ; x < banlist.size() ; x++) {
		        		User m = banlist.get(x).getUser();
		        		if (m.getName().equalsIgnoreCase(tounban) || m.getId().equals(tounban)) {
		        			e.getGuild().getController().unban(m).queue();
		        			e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.green).setDescription(":white_check_mark: **"+m.getName()+"#"+m.getDiscriminator()+"** has been unbanned!").build()).queue();
		        			con.prepareStatement("UPDATE ModHistory SET Active = 0 WHERE GuildID = "+e.getGuild().getIdLong()+" AND PunishedID = "
		        				+m.getIdLong()+" AND Type = 'BAN' OR GuildID = "+e.getGuild().getIdLong()+" AND PunishedID = "
		        				+m.getIdLong()+" AND Type = 'TEMPBAN'").execute();
			  			    TextChannel logchannel = e.getGuild().getTextChannelById(rs.getLong("LogChannel"));
			  			    EmbedBuilder eb = new EmbedBuilder().setAuthor("Member Unbanned", null, m.getEffectiveAvatarUrl()).setDescription("User "+m.getAsMention()+" "
			  			    		+ "has been unbanned").addField("Unbanned by", e.getMember().getAsMention(), true).setTimestamp(Instant.from(ZonedDateTime.now()))
			  			    		.setFooter("User ID"+m.getId(), null).setColor(Color.green);
			  			    logchannel.sendMessage(eb.build()).queue();
			  			    rs.close();
			  			    con.close();
			  			    return;
		        		}
		        	}
        			e.getChannel().sendMessage(":no_entry: Could not find user! (Make sure it is their __exact__ username, or try using their ID!)").queue();
			      }
		          	
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
		}
		if (args[0].equalsIgnoreCase("!ban")) {
			if (args.length < 2) {
				e.getChannel().sendMessage("Invalid Syntax: `!ban <User> (Time) <Reason>`").queue();
				return;
			}
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
			boolean temp = false;
			long expires = 0L;
			char unit = 'x';
			String durstring = "";
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
			      TextChannel logchannel = null;
			      while (rs.next())
			      {
			    	logchannel = e.getGuild().getTextChannelById(rs.getLong("LogChannel"));
			        long ModRoleID = rs.getLong("Moderator");
			        if (ModRoleID == 0L) {
			        	e.getChannel().sendMessage("You must setup the staff role before using the moderation system! `(!setup Moderation <role>)`").queue();
			        	return;
			        }
			        if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
			        	MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you must have the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role to use that command!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(eb).queue();
			        	return;
			        }
					if (target.getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
						MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you cannot ban users with the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(eb).queue();
			        	return;
					}
					try {
						int lastnum = args[2].length() - 1;
						unit = args[2].toCharArray()[lastnum];
						for (int x = 0 ; x < args[2].length() ; x++) {
							if (x != lastnum) {
								durstring = durstring + args[2].toCharArray()[x];
							}
						}
						int duration = Integer.parseInt(durstring);
						Calendar cal = Calendar.getInstance();
						if (unit == 'm') {
							cal.add(Calendar.MINUTE, duration);
						} else if (unit == 'h') {
							cal.add(Calendar.HOUR, duration);
						} else if (unit == 'd') {
							cal.add(Calendar.DAY_OF_MONTH, duration);
						} else if (unit == 'w') {
							cal.add(Calendar.WEEK_OF_MONTH, duration);
						} else if (unit == 'y') {
							cal.add(Calendar.YEAR, duration);
						} else {
							e.getChannel().sendMessage(":no_entry: Invalid Duration, `1m, 2h, 3d, 4w, 5y`").queue();
						}
						temp=true;
						expires=cal.getTimeInMillis();
					} catch (NumberFormatException | IndexOutOfBoundsException e2) {
						
					}
					
					
			      }
			      String reason = "";
			      if (temp) {
			    	  reason = e.getMessage().getContentRaw().replace(args[0], "").replace(args[1], "").replace(args[2], "");
			      } else {
			    	  reason = e.getMessage().getContentRaw().replace(args[0], "").replace(args[1], "");
			      }
			      if (reason.replace(" ", "").isEmpty()) {
			    	  reason = "No Reason Provided";
			      }
		        	EmbedBuilder send = new EmbedBuilder().setAuthor("You have been banned!", null, e.getGuild().getIconUrl()).setDescription("You have been "
			        		+ "banned from **"+e.getGuild().getName()+"** by **"+e.getMember().getUser().getName()+"#"+e.getMember().getUser().getDiscriminator()
			        		+ "** due to `"+reason+"`").setThumbnail(e.getGuild().getIconUrl()).setColor(Color.red);
			        	if (temp) {
			        		send.setDescription("You have been temporarily banned from **"+e.getGuild().getName()+"** by **"+e.getMember().getUser().getName()
			        				+"#"+e.getMember().getUser().getDiscriminator()+"** for **"+durstring+unit+"** due to `"+reason+"`");
			        	}
			        List<net.dv8tion.jda.core.entities.Guild.Ban> bans = e.getGuild().getBanList().complete();
			        for (int x = 0 ; x < bans.size() ; x++) {
			        	if (bans.get(0).getUser().equals(target.getUser())) {
			        		e.getChannel().sendMessage(":no_entry: This user is already banned!").queue();
			        		return;
			        	}
			        }
			        target.getUser().openPrivateChannel().queue((channel) ->
			        {
						try {
							channel.sendMessage(send.build()).submit().get();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			
			        });
			        EmbedBuilder eb = new EmbedBuilder().setColor(Color.green);
		  			if (args.length < 3) {
		  				eb.setDescription("⛔ "+e.getMember().getAsMention()+", you have permanently banned **"+target.getUser().getName()
		  						+"#"+target.getUser().getDiscriminator()+"**");
		  			} else {
		  				if (temp) {
		  					eb.setDescription("⛔ "+e.getMember().getAsMention()+", you have temporarily banned **"+target.getUser().getName()
			  						+"#"+target.getUser().getDiscriminator()+"** for **"+durstring+unit+"** due to `"+e.getMessage().getContentRaw().split(args[2])[1]+"`");
		  				} else {
		  					eb.setDescription("⛔ "+e.getMember().getAsMention()+", you have permanently banned **"+target.getUser().getName()
			  						+"#"+target.getUser().getDiscriminator()+"** due to `"+e.getMessage().getContentRaw().split(args[1])[1]+"`");
		  				}
		  			}
			        try {
			        	e.getGuild().getController().ban(target, 0,  "[By "+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator()+"] "+reason).reason("[By "+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator()+"] "+reason).queue();
			        } catch (HierarchyException e1) {
			        	e.getChannel().sendMessage(":no_entry: I can't punish that user!").queue();
			        	return;
			        }
		  			String type = "BAN";
		  			if (temp) {
		  				type = "TEMPBAN";
		  			}
	  				PreparedStatement ps = con.prepareStatement("INSERT INTO ModHistory (Timestamp, GuildID, PunishedID, PunisherID, Type, Reason, Expires, Active)  values "
				      		+ "("+System.currentTimeMillis()+", "+e.getGuild().getIdLong()+", "+target.getUser().getIdLong()+", "+e.getAuthor().getIdLong()
				      		+ ", '"+type+"', , "+expires+", 1)");
	  				ps.setString(1, reason);
	  				ps.execute();
		          	e.getChannel().sendMessage(eb.build()).queue();
		          	if (temp) {
		  			    eb = new EmbedBuilder().setAuthor("Member Banned", null, target.getUser().getEffectiveAvatarUrl()).setDescription("User "+target.getAsMention()+" "
		  			    		+ "has been Banned").addField("Length", durstring+unit, false).addField("Banned by", e.getMember().getAsMention(), true).addField("Reason", reason, false).setTimestamp(Instant.from(ZonedDateTime.now()))
		  			    		.setFooter("User ID"+target.getUser().getId(), null).setColor(Color.red);
		  			    logchannel.sendMessage(eb.build()).queue();
		          	} else {
		  			    eb = new EmbedBuilder().setAuthor("Member Banned", null, target.getUser().getEffectiveAvatarUrl()).setDescription("User "+target.getAsMention()+" "
		  			    		+ "has been Banned").addField("Banned by", e.getMember().getAsMention(), true).addField("Reason", reason, false).setTimestamp(Instant.from(ZonedDateTime.now()))
		  			    		.setFooter("User ID"+target.getUser().getId(), null).setColor(Color.red);
		  			    logchannel.sendMessage(eb.build()).queue();
		          	}
		          	
			      rs.close();
			      con.close();
			      
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
			
		}
		
	}

}
