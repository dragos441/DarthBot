package me.darth.darthbot.moderation;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class History extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!history")) {
			Member target = null;
			if (args.length < 2) {
				target = e.getMember();
			} else if (!e.getMessage().getMentionedMembers().isEmpty()) {
				target = e.getMessage().getMentionedMembers().get(0);
			} else {
				target = me.darth.darthbot.main.Main.findUser(args[1]);
			}
			
			if (target == null) {
				e.getChannel().sendMessage(":no_entry: Member not found!").queue();
				return;
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM ModHistory WHERE PunishedID = "+target.getUser().getIdLong()+" AND GuildID = "+e.getGuild().getIdLong());
			      EmbedBuilder eb = new EmbedBuilder().setAuthor(target.getEffectiveName()+"'s Punishment History", null, target.getUser().getEffectiveAvatarUrl()).setColor(Color.red)
			    		  .setFooter("Requested by "+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator()+" (Local Search)", e.getAuthor().getEffectiveAvatarUrl());
			      while (rs.next()) {
			    	  String type = rs.getString("Type");
			    	  Calendar cal = Calendar.getInstance();
			    	  cal.setTimeInMillis(rs.getLong("Timestamp"));
			    	  String punisher = "<@"+rs.getLong("PunisherID")+">";
			    	  String reason = rs.getString("Reason");
			    	  if (type.equals("BAN")) {
			    		  eb.addField("Banned on "+cal.getTime(), "**Reason:** `"+reason+"` **by "+punisher+"**", false);
			    	  } else {
			    		  eb.addField(type+" @ "+cal.getTime(), "**Reason:** `"+reason+"` **by "+punisher+"**", false);
			    	  }
			      }
			      if (eb.getFields().size() == 0) {
			    	 eb.setTitle(":angel: No punishments found!");
			      }
			      e.getChannel().sendMessage(eb.build()).queue();
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
		}
		
	}
}
