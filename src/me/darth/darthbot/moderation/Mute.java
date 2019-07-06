package me.darth.darthbot.moderation;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Mute extends ListenerAdapter {

	public Role sortRole(Guild g) {
		Role r = null;
     	if (g.getRolesByName("Muted", true).isEmpty()) {
     		r = g.getController().createRole().setName("Muted").setColor(000001).setMentionable(true).setPermissions(Permission.EMPTY_PERMISSIONS).complete();
     		for (int x = 0 ; x < g.getRoles().size() ; x++) {
     			try {
     				g.getController().modifyRolePositions().selectPosition(r).moveTo(Math.subtractExact(g.getRoles().size(), x)).queue();
     				break;
     			} catch (IllegalArgumentException | IllegalStateException e1) {}
     		}
     	} else {
     		r = g.getRolesByName("Muted", true).get(0);
     	}
     	for (int x = 0 ; x < g.getTextChannels().size() ; x++) {
      		TextChannel c = g.getTextChannels().get(x);
      		try {
      			c.putPermissionOverride(r).setDeny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).queue();
      		} catch (InsufficientPermissionException e1) {}
      	}
     	return r;
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		EmbedBuilder eb = new EmbedBuilder().setAuthor("Mute Guide", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl()).setDescription("**Command Usage**\n"
				+ "`!mute @User (Optional Duration) (Reason)`\n*() = Optional Argument*").addField("Permanent Mute", "`!mute @User (Reason)`", false).addField("Temporary Mute", "`!mute @User 1m test`\n**1m** = 1 minute\n"
						+ "**2h** = 2 hours\n**3d** = 3 days\n**4w** = 4 weeks\n**5y** = 5 years", false).setColor(Color.red);
		if (args[0].equalsIgnoreCase("!unmute")) {
			if (args.length < 2) {
				e.getChannel().sendMessage("Invalid Syntax: `!unmute <User>`").queue();
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
			        	e.getChannel().sendMessage("You must setup the staff role before using the moderation system! `(!setup Moderation <role>)`").queue();
			        	return;
			        }
			        if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(ModRoleID)) && !e.getMember().getRoles().contains(e.getGuild().getRoleById("589796348711403520"))
			        		) {
			        	MessageEmbed meb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you must have the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role to use that command!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(meb).queue();
			        	return;
			        }
			        
			        Member target = null;
			       if (!e.getMessage().getMentionedMembers().isEmpty()) {
			    	   target = e.getMessage().getMentionedMembers().get(0);
			       } else {
			    	   target = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" ", ""), e.getGuild());
			       }
			       if (target == null) {
			    	   e.getChannel().sendMessage(":no_entry: User not found!").queue();
			    	   return;
			       }
			       if (!target.getRoles().contains(e.getGuild().getRolesByName("Muted", true).get(0))) {
			    	   e.getChannel().sendMessage(":no_entry: That user is not muted!").queue();
			    	   return;
			       }
			       e.getGuild().getController().removeSingleRoleFromMember(target, e.getGuild().getRolesByName("Muted", true).get(0)).queue();
			       e.getGuild().getController().setMute(target, false).queue();
			       e.getChannel().sendMessage(new EmbedBuilder().setDescription(":white_check_mark: **"+target.getAsMention()+"** has been been unmuted.").setColor(Color.green).build()).queue();
			       con.prepareStatement("UPDATE ModHistory SET Active = 0 WHERE GuildID = "+e.getGuild().getIdLong()+" AND PunishedID = "
			       +target.getUser().getIdLong()+" AND Type = 'TEMPMUTE' OR GuildID = "+e.getGuild().getIdLong()+" AND PunishedID = "
					       +target.getUser().getIdLong()+" AND Type = 'MUTE'").execute();
			       EmbedBuilder meb = new EmbedBuilder().setAuthor("Member Unmuted", null, target.getUser().getEffectiveAvatarUrl()).setDescription("User "+target.getAsMention()+" "
	  			    		+ "has been unmuted").addField("Unmuted by", e.getMember().getAsMention(), true).setTimestamp(Instant.from(ZonedDateTime.now()))
	  			    		.setFooter("User ID"+target.getUser().getId(), null).setColor(Color.red);
	  			    logchannel.sendMessage(meb.build()).queue();
	  			    
			      }
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
		}
		if (args[0].equalsIgnoreCase("!mute")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet canUse = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
				while (canUse.next()) {
					if (canUse.getLong("Moderator") == 0L) {
			        	e.getChannel().sendMessage(":no_entry: You must setup the staff role before using the moderation system! (`!setup Moderation <role>`)").queue();
			        	return;
			        }
					if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(canUse.getLong("Moderator"))) && !e.getMember().getRoles().contains(e.getGuild().getRoleById("589796348711403520"))) {
			        	MessageEmbed meb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you must have the "+e.getGuild().getRoleById(canUse.getLong("Moderator")).getAsMention()
			        			+ " role to use that command!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(meb).queue();
			        	return;
					}
				
					Member m = null;
					int lengthd = 0;
					char lengthc = 'n';
					String reason = "";
					
					if (args.length < 2) {
						e.getChannel().sendMessage(eb.build()).queue();
						return;
					}
					if (!e.getMessage().getMentionedMembers().isEmpty()) {
						m = e.getMessage().getMentionedMembers().get(0);
					} else {
						m = me.darth.darthbot.main.Main.findUser(args[1], e.getGuild());
					}
					if (m == null) {
						e.getChannel().sendMessage(":no_entry: Member not found!").embed(eb.build()).queue();
						return;
					}
					
					if (m.getRoles().contains(e.getGuild().getRoleById(canUse.getLong("Moderator")))) {
						MessageEmbed meb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you cannot mute users with the "+e.getGuild().getRoleById(canUse.getLong("Moderator")).getAsMention()
			        			+ " role!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(meb).queue();
			        	return;
					}
					
					try {
						lengthc = args[2].toCharArray()[(args[2].toCharArray().length - 1)];
						if (lengthc == 'm' || lengthc == 'h' || lengthc == 'd' || lengthc == 'w' || lengthc == 'y') {
							lengthd = Integer.parseInt(args[2].replace(lengthc+"", ""));
							reason = e.getMessage().getContentRaw().replace(args[0]+" ", "").replace(args[1]+" ", "").replace(args[2], "");
						} else {
							lengthc = 'n';
							reason = e.getMessage().getContentRaw().replace(args[0]+" ", "").replace(args[1], "");
						}
					} catch (ArrayIndexOutOfBoundsException e1) {
						
					} catch (NumberFormatException e1) {
						reason = e.getMessage().getContentRaw().replace(args[0], "").replace(args[1], "");
					}
					Role mutedrole = sortRole(e.getGuild());
					EmbedBuilder send = new EmbedBuilder().setAuthor("You have been muted!", null, e.getGuild().getIconUrl()).setColor(Color.red)
							.setFooter(null, e.getGuild().getIconUrl());
					Calendar cal = Calendar.getInstance();
					String charstring = null;
					if (lengthc == 'm') {
						cal.add(Calendar.MINUTE, lengthd);
						charstring = "minutes";
					} else if (lengthc == 'h') {
						cal.add(Calendar.HOUR, lengthd);
						charstring = "hours";
					} else if (lengthc == 'd') {
						cal.add(Calendar.DAY_OF_MONTH, lengthd);
						charstring = "days";
					} else if (lengthc == 'w') {
						cal.add(Calendar.WEEK_OF_MONTH, lengthd);
						charstring = "weeks";
					} else if (lengthc == 'y') {
						cal.add(Calendar.YEAR, lengthd);
						charstring = "years";
					}
					EmbedBuilder log = new EmbedBuilder().setAuthor("Member Muted", null, m.getUser().getEffectiveAvatarUrl()).setDescription("User "+m.getAsMention()+" "
	  			    		+ "has been muted").addField("Muted by", e.getMember().getAsMention(), true).addField("Reason", reason, false).setTimestamp(Instant.from(ZonedDateTime.now()))
	  			    		.setFooter("User ID"+m.getUser().getId(), null).setColor(Color.red);
					if (lengthc != 'n' && lengthd > 0) {
						if (reason.replace(" ", "").equals("")) {
							e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.green).setDescription(
									":white_check_mark: Successfully muted "+m.getAsMention()+" for "+lengthd+" "+charstring+"!").build()).queue();
							send.setDescription("You have been muted in **"+e.getGuild().getName()+"** for **"+lengthd+" "+charstring+"**!");
							log.addField("Length", lengthc+lengthd+"", false);
							reason="No Reason Provided";
						} else {
							e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.green).setDescription(
									":white_check_mark: Successfully muted "+m.getAsMention()+" for "+lengthd+" "+charstring+" due to `"+reason+"`").build()).queue();
							log.addField("Length", lengthc+lengthd+"", false);
							send.setDescription("You have been muted in **"+e.getGuild().getName()+"** for **"+lengthd+" "+charstring+"** due to `"+reason+"`");
						}
						con.prepareStatement("INSERT INTO ModHistory (Timestamp, GuildID, PunishedID, PunisherID, Type, Reason, Expires, Active)  values "
					      		+ "("+System.currentTimeMillis()+", "+e.getGuild().getIdLong()+", "+m.getUser().getIdLong()+", "+e.getAuthor().getIdLong()
					      		+ ", 'TEMPMUTE', '"+reason+"', "+cal.getTimeInMillis()+", 1)").execute();
					} else {
						if (reason.replace(" ", "").equals("")) {
							e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.green).setDescription(
									":white_check_mark: Successfully permanently muted "+m.getAsMention()+"!").build()).queue();
							send.setDescription("You have been permanently muted in **"+e.getGuild().getName()+"**!");
							reason="No Reason Provided";
						} else {
							e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.green).setDescription(
									":white_check_mark: Successfully permanently muted "+m.getAsMention()+" due to `"+reason+"`!").build()).queue();
							send.setDescription("You have been permanently muted in **"+e.getGuild().getName()+"**! due to `"+reason+"`");
						}
						con.prepareStatement("INSERT INTO ModHistory (Timestamp, GuildID, PunishedID, PunisherID, Type, Reason, Expires, Active)  values "
					      		+ "("+System.currentTimeMillis()+", "+e.getGuild().getIdLong()+", "+m.getUser().getIdLong()+", "+e.getAuthor().getIdLong()
					      		+ ", 'TEMPMUTE', '"+reason+"', 0, 0)").execute();
					}
					e.getGuild().getController().addSingleRoleToMember(m, mutedrole).queue();
					e.getGuild().getController().setMute(m, true).queue();
					if (canUse.getLong("LogChannel") != 0L) {
						e.getGuild().getTextChannelById(canUse.getLong("LogChannel")).sendMessage(log.build()).queue();
					}
					m.getUser().openPrivateChannel().queue((channel) ->
			        {
			            channel.sendMessage(send.build()).queue();
			        });
					
					
				}
				con.close();
				
			} catch (SQLException e1) {e1.printStackTrace();}
			
			
			
			
			
			
		}
	}
}
