package me.darth.darthbot.main;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateNSFWEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateMFALevelEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNotificationLevelEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateRegionEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateVerificationLevelEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.role.RoleCreateEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateMentionableEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ServerLogs extends ListenerAdapter {
	
	@Override
	public void onMessageDelete(MessageDeleteEvent e) {
		if (e.getChannel().equals(e.getGuild().getTextChannelById("569465506369765396"))
				|| e.getChannel().equals(e.getGuild().getTextChannelById("577877034240311337"))) {
			return;
		}
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setAuthor("Message Deleted",null, "https://cdn4.iconfinder.com/data/icons/social-messaging-ui-coloricon-1/21/52-512.png");
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM messageLog");
		      while (rs.next())
		      {
		        long Timestamp = rs.getLong("Timestamp");
		        long MessageID = rs.getLong("MessageID");
		        long AuthorID = rs.getLong("AuthorID");
		        long ChannelID = rs.getLong("ChannelID");
		        String Content = rs.getString("Content");
		        try {
		        	if (MessageID == e.getMessageIdLong()) {
		        		eb.setAuthor("Message by "+e.getGuild().getMemberById(AuthorID).getEffectiveName()+" deleted in #"
		        		+e.getGuild().getTextChannelById(ChannelID).getName(), null, 
		        			e.getGuild().getMemberById(AuthorID).getUser().getAvatarUrl());
		        		eb.setDescription("**Message:** "+Content);
		        		eb.setFooter("Message ID: "+MessageID, null);
		        		eb.setTimestamp(Instant.ofEpochMilli(Timestamp));
		        		ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
		        		while (g.next()) {
		        			long GuildID = g.getLong("GuildID");
		        			if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
		        				long logchannel = g.getLong("LogChannel");
		        				e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
		        			}
		        		}
		        		rs.close();
		        		con.close();
		        		return;
		        	}
		        } catch (ArrayIndexOutOfBoundsException e2) {e2.printStackTrace();} catch (NullPointerException e3) {e3.printStackTrace(); return;}
		      }
		      rs.close();
		      con.close();
		} catch (SQLException e1) {
		    e1.printStackTrace();
		}
		
	}
	
	@Override
	public void onMessageUpdate(MessageUpdateEvent e) {
		if (e.getChannel().equals(e.getGuild().getTextChannelById("569465506369765396"))
				|| e.getChannel().equals(e.getGuild().getTextChannelById("577877034240311337"))) {
			return;
		}
		if (e.getMessage().getContentRaw() != null && e.getMessage().getContentRaw() != "" && !e.getAuthor().isBot()) {
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
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setAuthor("Message Edited",null, "https://cdn4.iconfinder.com/data/icons/social-messaging-ui-coloricon-1/21/52-512.png");
		String msg1 = null;
		String msg2 = null;
		boolean send = false;
		boolean pin = false;
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM messageLog");
		      while (rs.next())
		      {
		        long Timestamp = rs.getLong("Timestamp");
		        long MessageID = rs.getLong("MessageID");
		        long AuthorID = rs.getLong("AuthorID");
		        long ChannelID = rs.getLong("ChannelID");
		        String Content = rs.getString("Content");
		        
		        try {
		        	if (MessageID == e.getMessageIdLong()) {
		        		send=true;
		        		msg2=msg1;
		        		msg1=Content;
		        		if (msg1.equals(msg2)) {
		        			pin=true;
		        			String pinned = "pinned";
		        			if (!e.getMessage().isPinned()) {
		        				pinned="unpinned";
		        			}
		        			eb.setAuthor("Message by "+e.getGuild().getMemberById(AuthorID).getEffectiveName()+" "+pinned+" in #"
		    		        		+e.getGuild().getTextChannelById(ChannelID).getName(), null, 
		    		        			e.getGuild().getMemberById(AuthorID).getUser().getAvatarUrl());
		        		} else {
			        		eb.setAuthor("Message by "+e.getGuild().getMemberById(AuthorID).getEffectiveName()+" edited in #"
			        		+e.getGuild().getTextChannelById(ChannelID).getName(), null, 
			        			e.getGuild().getMemberById(AuthorID).getUser().getAvatarUrl());
		        		}
		        		eb.setFooter("Message ID: "+MessageID, null);
		        		eb.setTimestamp(Instant.ofEpochMilli(Timestamp));
		        	}
		        } catch (ArrayIndexOutOfBoundsException e2) {}
		   
		      }
		
		      if (send) {
		    	if (!pin) {
		        	eb.addField("Old Message", "**Message:** "+msg2, true);
		        	eb.addField("New Message", "**Message:** "+msg1, true);
		    	} else {
		    		eb.addField("Message", "**Message:** "+msg1, true);
		    	}
	        	ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
        		while (g.next()) {
        			long GuildID = g.getLong("GuildID");
        			if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
        				long logchannel = g.getLong("LogChannel");
        				e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
        			}
        		}
		      }
		      rs.close();
		      con.close();
			
		} catch (SQLException e1) {
		    e.getChannel().sendMessage("Error! ```"+e1+"```").queue();
		}
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {	
		if (e.getAuthor().equals(e.getJDA().getSelfUser())) {
			return;
		}
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setAuthor("Direct Message Recieved",null, e.getAuthor().getAvatarUrl());
		eb.setDescription("Private message recieved from "+e.getAuthor().getAsMention());
		eb.addField("Message:", "**"+e.getMessage().getContentRaw()+"**", true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		me.darth.darthbot.main.Main.jda.getGuildById("568849490425937940").getTextChannelById("569883444126023680").sendMessage(eb.build()).queue();
		
	}
	
	@Override
	public void onGuildBan(GuildBanEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("User Banned",null, e.getUser().getAvatarUrl());
		eb.setDescription("User "+e.getUser().getAsMention()+" has been banned ");
		eb.setFooter("Banned User ID: "+e.getUser().getId(), e.getUser().getAvatarUrl());
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
	}
	
	@Override
	public void onGuildUpdateIcon(GuildUpdateIconEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Server Icon Updated",null, e.getNewIconUrl());
		eb.setDescription("The server icon has been updated");
		eb.setFooter("Old Icon: ", e.getOldIconUrl());
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildUpdateMFALevel(GuildUpdateMFALevelEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Server MFA Level Updated",null, e.getGuild().getIconUrl());
		eb.setDescription("The Server's MFA level has been updated.");
		eb.addField("New MFA Level: ", ""+e.getNewMFALevel(), true);
		eb.addField("Old MFA Level: ", ""+e.getOldMFALevel(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildUpdateName(GuildUpdateNameEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Server Name Updated",null, e.getGuild().getIconUrl());
		eb.setDescription("The Server's name has been updated.");
		eb.addField("New Server Name: ", ""+e.getNewName(), true);
		eb.addField("Old Server Name: ", ""+e.getOldName(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildUpdateNotificationLevel(GuildUpdateNotificationLevelEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Server Notifications Updated",null, e.getGuild().getIconUrl());
		eb.setDescription("The Server's notification level has been updated.");
		eb.addField("New Notification Level: ", ""+e.getNewNotificationLevel(), true);
		eb.addField("Old Notification Level: ", ""+e.getOldNotificationLevel(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildUpdateOwner(GuildUpdateOwnerEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Server Owner Updated",null, e.getGuild().getOwner().getUser().getAvatarUrl());
		eb.setDescription("The Server's Owner has been updated");
		eb.addField("New Owner: ", ""+e.getNewOwner().getAsMention(), true);
		eb.addField("Old Owner: ", ""+e.getOldOwner().getAsMention(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildUpdateRegion(GuildUpdateRegionEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Server Region Updated",null, e.getGuild().getIconUrl());
		eb.setDescription("The Server's Host Region has been updated");
		eb.addField("New Region: ", ""+e.getNewRegion(), true);
		eb.addField("Old Region: ", ""+e.getOldRegion(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildUpdateVerificationLevel(GuildUpdateVerificationLevelEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Server Verification Level Updated",null, e.getGuild().getIconUrl());
		eb.setDescription("The Server's Verification Level has been updated");
		eb.addField("New Level: ", ""+e.getNewVerificationLevel(), true);
		eb.addField("Old Region: ", ""+e.getOldVerificationLevel(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setAuthor("Member Joined", null, e.getMember().getUser().getEffectiveAvatarUrl());
		eb.setDescription(""+e.getMember().getAsMention()+" ("+e.getMember().getEffectiveName()+"#"+e.getMember().getUser().getDiscriminator()+")");
		eb.setThumbnail(e.getUser().getAvatarUrl());
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Member Left",null, e.getMember().getUser().getEffectiveAvatarUrl());
		eb.setDescription(""+e.getMember().getAsMention()+" ("+e.getMember().getEffectiveName()+"#"+e.getMember().getUser().getDiscriminator()+")");
		eb.setThumbnail(e.getUser().getAvatarUrl());
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildMemberNickChange(GuildMemberNickChangeEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setAuthor("Nickname Changed",null, e.getUser().getAvatarUrl());
		eb.addField("New Nickname: ", ""+e.getNewNick(), true);
		eb.addField("Old Nickname: ", ""+e.getPrevNick(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setAuthor("Role Assigned",null, e.getUser().getAvatarUrl());
		eb.setDescription(e.getMember().getAsMention()+ " was given the `"+e.getRoles().get(0).getName()+"` role");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Role Removed",null, e.getUser().getAvatarUrl());
		eb.setDescription(e.getMember().getAsMention()+ " was removed from the `"+e.getRoles().get(0).getName()+"` role");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setAuthor("Voice Channel Join",null, e.getMember().getUser().getAvatarUrl());
		eb.setDescription(e.getMember().getAsMention()+" joined **#"+e.getChannelJoined().getName()+"**");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Voice Channel Leave",null, e.getMember().getUser().getAvatarUrl());
		eb.setDescription(e.getMember().getAsMention()+" left **#"+e.getChannelLeft().getName()+"**");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setAuthor("Voice Channel Move",null, e.getMember().getUser().getAvatarUrl());
		eb.setDescription(e.getMember().getAsMention()+" left **#"+e.getChannelLeft().getName()+"**, heading to **#"+e.getChannelJoined().getName()+"**");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onTextChannelCreate(TextChannelCreateEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setAuthor("Text Channel Created",null, e.getGuild().getIconUrl());
		eb.setDescription("Channel **"+e.getChannel().getAsMention()+"** has been created");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Text Channel Deleted",null, e.getGuild().getIconUrl());
		eb.setDescription("Channel **"+e.getChannel().getName()+"** has been deleted");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onTextChannelUpdateName(TextChannelUpdateNameEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setAuthor("Text Channel Name Edited",null, e.getGuild().getIconUrl());
		eb.setDescription("Channel **"+e.getChannel().getAsMention()+"** had its name changed.");
		eb.addField("Old Name", ""+e.getOldName(), true);
		eb.addField("New Name", e.getNewName(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onTextChannelUpdateNSFW(TextChannelUpdateNSFWEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setAuthor("Text Channel NSFW Status",null, e.getGuild().getIconUrl());
		eb.setDescription("Channel **"+e.getChannel().getAsMention()+"** had its NSFW Status changed");
		eb.addField("Old NSFW Status", ""+e.getOldNSFW(), true);
		eb.addField("New NSFW Status", ""+e.getNewValue(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setAuthor("Text Channel Topic Change",null, e.getGuild().getIconUrl());
		eb.setDescription("Channel **"+e.getChannel().getAsMention()+"** had its topic changed");
		eb.addField("Old Topic", ""+e.getOldTopic(), true);
		eb.addField("New Topic", ""+e.getNewTopic(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		if (e.getNewTopic() != null) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
				while (g.next()) {
					long GuildID = g.getLong("GuildID");
					if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
						long logchannel = g.getLong("LogChannel");
						e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void onVoiceChannelCreate(VoiceChannelCreateEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setAuthor("Voice Channel Created",null, e.getGuild().getIconUrl());
		eb.setDescription("Channel #**"+e.getChannel().getName()+"** has been created");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onVoiceChannelDelete(VoiceChannelDeleteEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Voice Channel Deleted",null, e.getGuild().getIconUrl());
		eb.setDescription("Channel #**"+e.getChannel().getName()+"** has been deleted");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onVoiceChannelUpdateName(VoiceChannelUpdateNameEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setAuthor("Voice Channel Name Edited",null, e.getGuild().getIconUrl());
		eb.setDescription("Channel #**"+e.getChannel().getName()+"** had its name changed.");
		eb.addField("Old Name", ""+e.getOldName(), true);
		eb.addField("New Name", e.getNewName(), true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onRoleCreate(RoleCreateEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setAuthor("Role Created",null, e.getGuild().getIconUrl());
		eb.setDescription("Role **"+e.getRole().getAsMention()+"** has been created");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onRoleDelete(RoleDeleteEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Role Deleted",null, e.getGuild().getIconUrl());
		eb.setDescription("Role **"+e.getRole().getName()+"** has been deleted");
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void onRoleUpdateColor(RoleUpdateColorEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setAuthor("Role Colour Changed",null, e.getGuild().getIconUrl());
		eb.setDescription("Role **"+e.getRole().getAsMention()+"** changed colour");
		eb.addField("Old Colour", e.getOldColorRaw()+"", true);
		eb.addField("New Colour", e.getNewColorRaw()+"", true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onRoleUpdateMentionable(RoleUpdateMentionableEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Role Mentionable Status",null, e.getGuild().getIconUrl());
		eb.setDescription("Role **"+e.getRole().getAsMention()+"** has changed its mentionable status");
		eb.addField("Old Status", e.getOldValue()+"", true);
		eb.addField("New Status", e.getNewValue()+"", true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		if (e.getNewValue() == true) {
			eb.setColor(Color.green);
		} else {
			eb.setColor(Color.red);
		}
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onRoleUpdateName(RoleUpdateNameEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setAuthor("Role Name Change",null, e.getGuild().getIconUrl());
		eb.setDescription("Role **"+e.getRole().getAsMention()+"**'s name has been edited");
		eb.addField("Old Name", e.getOldName()+"", true);
		eb.addField("New Name", e.getNewName()+"", true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setAuthor("Role Permission Change",null, e.getGuild().getIconUrl());
		eb.setDescription("Role **"+e.getRole().getAsMention()+"**'s permissions have been edited");
		eb.addField("Old Permissions", e.getOldPermissions()+"", true);
		eb.addField("New Permissions", e.getNewPermissions()+"", true);
		eb.setTimestamp(Instant.from(ZonedDateTime.now()));
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("LogChannel") != 0) {
					long logchannel = g.getLong("LogChannel");
					e.getGuild().getTextChannelById(logchannel).sendMessage(eb.build()).queue();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

}




