package me.darth.darthbot.moderation;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Purge extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!purge")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
			      while (rs.next())
			      {
			        long ModRoleID = rs.getLong("Moderator");
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
					if (args.length < 2) {
						e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!purge <number>`").queue();
					}
			      }
			      int num = 0;
			      
			      try {
			    	  num = Integer.parseInt(args[1]);
			      } catch (NumberFormatException e1) {
			    	  e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!purge <number>`").queue();
			      }
			      if (num == 99 || num == 100) {
			    	  num=num-1;
			      } else if (num > 100) {
			    	  e.getChannel().sendMessage(":no_entry: You may only purge up to **100** messages at a time!").queue();
			      }
			      num=num+1;
			      List<Message> todelete = e.getChannel().getHistory().retrievePast(num).complete();
			      for (int x = 0 ; x < todelete.size() ; x++) {
						if (todelete.get(x).isPinned()) {
							todelete.remove(x);
							x=x-1;
						}
					}
			      e.getChannel().deleteMessages(todelete).queue();
			      num=num-1;
			      e.getChannel().sendMessage(":white_check_mark: Successfully purged `"+num+"` messages!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
			
		}
		
	}

}
