package me.darth.darthbot.moderation;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Lock extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!lock")) {
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
			        try {
				        if (!e.getChannel().getPermissionOverride(e.getGuild().getPublicRole()).getAllowed().contains(Permission.MESSAGE_WRITE)) {
				        	e.getChannel().sendMessage(":no_entry: This channel is already locked!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
				        	return;
				        }
			        } catch (NullPointerException e1) {}
			        String reason = "No Reason Provided";
			        if (args.length >= 2) {
			        	reason = e.getMessage().getContentRaw().replace(args[0]+" ", "");
			        }
        			EmbedBuilder eb = new EmbedBuilder().setAuthor("Channel Locked", null, e.getGuild().getIconUrl()).setDescription("This channel has been locked")
        					.addField("Reason", reason, false).addField("Locked By", e.getMember().getAsMention(), false).setColor(Color.red);
        			e.getChannel().sendMessage(eb.build()).queue();
			        if (e.getChannel().getPermissionOverride(e.getGuild().getPublicRole()) == null) {
			        	e.getChannel().createPermissionOverride(e.getGuild().getPublicRole()).complete().getManager().deny(Permission.MESSAGE_WRITE).queue();
			        } else {
			        	e.getChannel().getPermissionOverride(e.getGuild().getPublicRole()).getManager().deny(Permission.MESSAGE_WRITE).queue();
			        }
			        
			        if (e.getChannel().getPermissionOverride(e.getGuild().getRoleById(ModRoleID)) == null) {
			        	e.getChannel().createPermissionOverride(e.getGuild().getRoleById(ModRoleID)).complete().getManager().grant(Permission.MESSAGE_WRITE).queue();
			        } else {
			        	e.getChannel().getPermissionOverride(e.getGuild().getRoleById(ModRoleID)).getManager().grant(Permission.MESSAGE_WRITE).queue();
			        }
			        
			        if (e.getChannel().getPermissionOverride(e.getGuild().getMember(e.getJDA().getSelfUser())) == null) {
			        	e.getChannel().createPermissionOverride(e.getGuild().getMember(e.getJDA().getSelfUser())).complete().getManager().grant(Permission.MESSAGE_WRITE).queue();
			        } else {
			        	e.getChannel().getPermissionOverride(e.getGuild().getMember(e.getJDA().getSelfUser())).getManager().grant(Permission.MESSAGE_WRITE).queue();
			        }
			     
			      }
		          e.getMessage().delete().queue();
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
		}
		if (args[0].equalsIgnoreCase("!unlock")) {
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
			        try {
				        if (e.getChannel().getPermissionOverride(e.getGuild().getPublicRole()).getAllowed().contains(Permission.MESSAGE_WRITE)) {
				        	e.getChannel().sendMessage(":no_entry: This channel is already unlocked!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
				        	return;
				        }
			        } catch (NullPointerException e1) {}
			        e.getChannel().getPermissionOverride(e.getGuild().getPublicRole()).getManager().grant(Permission.MESSAGE_WRITE).queue();
			        try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			        String reason = "No Reason Provided";
        			EmbedBuilder eb = new EmbedBuilder().setAuthor("Channel Unlocked", null, e.getGuild().getIconUrl()).setDescription("This channel has been unlocked")
        					.addField("Unlocked By", e.getMember().getAsMention(), false).setColor(Color.green);
        			e.getChannel().sendMessage(eb.build()).queue();
			      }
		          e.getMessage().delete().queue();
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
		}
	}
}
