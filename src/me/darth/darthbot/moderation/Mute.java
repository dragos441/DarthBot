package me.darth.darthbot.moderation;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Mute extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getGuild().getId().equals("393499439739961366")) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!unmute")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
			      while (rs.next())
			      {
			        long ModRoleID = rs.getLong("Moderator");
			        if (ModRoleID == 0L) {
			        	e.getChannel().sendMessage("You must setup the staff role before using the moderation system!").queue();
			        	return;
			        }
			        if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
			        	MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you must have the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role to use that command!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(eb).queue();
			        	return;
			        }
			        
			        Member target = null;
			       if (!e.getMessage().getMentionedMembers().isEmpty()) {
			    	   target = e.getMessage().getMentionedMembers().get(0);
			       } else {
			    	   target = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" ", ""));
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
			       e.getChannel().sendMessage(new EmbedBuilder().setDescription(":white_check_mark: **"+target.getAsMention()+"** has been been unmuted.").setColor(Color.green).build()).queue();
			       con.prepareStatement("UPDATE ModHistory SET Active = 0 WHERE GuildID = "+e.getGuild().getIdLong()+" AND PunishedID = "
			       +target.getUser().getIdLong()+" AND Type = 'TEMPMUTE' OR GuildID = "+e.getGuild().getIdLong()+" AND PunishedID = "
					       +target.getUser().getIdLong()+" AND Type = 'MUTE'").execute();
			      }
		          	
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
		}
		if (args[0].equalsIgnoreCase("!mute")) {
			Member target = null;
			if (!e.getMessage().getMentionedMembers().isEmpty()) {
				target = e.getMessage().getMentionedMembers().get(0);
			} else {
				me.darth.darthbot.main.Main.findUser(args[1]);
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
			      while (rs.next())
			      {
			        long ModRoleID = rs.getLong("Moderator");
			        if (ModRoleID == 0L) {
			        	e.getChannel().sendMessage("You must setup the staff role before using the moderation system!").queue();
			        	return;
			        }
			        if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
			        	MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you must have the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role to use that command!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(eb).queue();
			        	return;
			        }
					if (target.getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
						MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you cannot mute users with the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
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
			      String reason = null;
			      if (args.length < 4) {
			    	  temp=false;
			      }
			      if (temp) {
			    	  reason = e.getMessage().getContentRaw().replace(args[0], "").replace(args[1], "").replace(args[2], "");
			      } else {
			    	  reason = e.getMessage().getContentRaw().replace(args[0], "").replace(args[1], "");
			      }
			      if (reason.replace(" ", "").isEmpty()) {
			    	  reason = "No Reason Provided";
			      }
		        	EmbedBuilder send = new EmbedBuilder().setAuthor("You have been muted!", null, e.getGuild().getIconUrl()).setDescription("You have been "
			        		+ "muted on **"+e.getGuild().getName()+"** by **"+e.getMember().getUser().getName()+"#"+e.getMember().getUser().getDiscriminator()
			        		+ "** due to `"+reason+"`").setThumbnail(e.getGuild().getIconUrl()).setColor(Color.red);
			        	if (temp) {
			        		send.setDescription("You have been temporarily muted on **"+e.getGuild().getName()+"** by **"+e.getMember().getUser().getName()
			        				+"#"+e.getMember().getUser().getDiscriminator()+"** for **"+durstring+unit+"** due to `"+reason+"`");
			        	}
			  
			     	Role r = null;
			     	if (e.getGuild().getRolesByName("Muted", true).isEmpty()) {
			     		r = e.getGuild().getController().createRole().setName("Muted").setColor(000001).setMentionable(true).setPermissions(Permission.EMPTY_PERMISSIONS).complete();
			     		for (int x = 0 ; x < e.getGuild().getRoles().size() ; x++) {
			     			try {
			     				e.getGuild().getController().modifyRolePositions().selectPosition(r).moveTo(Math.subtractExact(e.getGuild().getRoles().size(), x)).queue();
			     				break;
			     			} catch (IllegalArgumentException | IllegalStateException e1) {}
			     		}
			     	} else {
			     		r = e.getGuild().getRolesByName("Muted", true).get(0);
			     	}
			     	if (target.getRoles().contains(r)) {
			     		e.getChannel().sendMessage(":no_entry: This user is already muted!").queue();
			     		return;
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
			     	
			     	e.getGuild().getController().addSingleRoleToMember(target, r).queue();
		  			if (args.length < 3) {
		  				eb.setDescription("⛔ "+e.getMember().getAsMention()+", you have permanently muted **"+target.getUser().getName()
		  						+"#"+target.getUser().getDiscriminator()+"**");
		  			} else {
		  				if (temp) {
		  					eb.setDescription("⛔ "+e.getMember().getAsMention()+", you have temporarily muted **"+target.getUser().getName()
			  						+"#"+target.getUser().getDiscriminator()+"** for **"+durstring+unit+"** due to `"+e.getMessage().getContentRaw().split(args[2])[1]+"`");
		  				} else {
		  					eb.setDescription("⛔ "+e.getMember().getAsMention()+", you have permanently muted **"+target.getUser().getName()
			  						+"#"+target.getUser().getDiscriminator()+"** due to `"+e.getMessage().getContentRaw().split(args[1])[1]+"`");
		  				}
		  			}
		  			String type = "MUTE";
		  			if (temp) {
		  				type = "TEMPMUTE";
		  			}
		  			con.prepareStatement("INSERT INTO ModHistory (Timestamp, GuildID, PunishedID, PunisherID, Type, Reason, Expires, Active)  values "
					      		+ "("+System.currentTimeMillis()+", "+e.getGuild().getIdLong()+", "+target.getUser().getIdLong()+", "+e.getAuthor().getIdLong()
					      		+ ", '"+type+"', '"+reason+"', "+expires+", 1)").execute();
		          	e.getChannel().sendMessage(eb.build()).queue();
		          	for (int x = 0 ; x < e.getGuild().getTextChannels().size() ; x++) {
		          		TextChannel c = e.getGuild().getTextChannels().get(x);
		          		c.putPermissionOverride(r).setDeny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).queue();
		          	}
		          	
		          	
			      rs.close();
			      con.close();
			      
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
			
		}
		
	}

}
