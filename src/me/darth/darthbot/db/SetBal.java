package me.darth.darthbot.db;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SetBal extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!setbalance") && me.darth.darthbot.main.Main.g.getMember(e.getMember().getUser()).getRoles().contains(me.darth.darthbot.main.Main.g.getRoleById("569463842552152094"))
			|| args[0].equalsIgnoreCase("!setbal") && me.darth.darthbot.main.Main.g.getMember(e.getMember().getUser()).getRoles().contains(me.darth.darthbot.main.Main.g.getRoleById("569463842552152094"))) {
			Member target = null;
			long bux = 0;
			try {
				bux = Long.parseLong(args[2]);
			} catch (NumberFormatException e1) {
				e.getChannel().sendMessage("Invalid Syntax: `!setbal <user> <money>`").queue();
				return;
			} catch (ArrayIndexOutOfBoundsException e2) {
				e.getChannel().sendMessage("Invalid Syntax: `!setbal <user> <money>`").queue();
				return;
			}
			if (!e.getMessage().getMentionedMembers().isEmpty()) {
				target = e.getMessage().getMentionedMembers().get(0);
			} else {
				target = me.darth.darthbot.main.Main.findUser(args[1]);
				if (target == null) {
					e.getChannel().sendMessage("User not found!").queue();
				}
			}
			if (target == null) { 
				e.getChannel().sendMessage("User not found!").queue();
				return;
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getIdLong());
			      while (rs.next())
			      {
			        long UserID = rs.getLong("UserID");
			        if (UserID == e.getMember().getUser().getIdLong()) {
			        	con.prepareStatement("UPDATE profiles SET DBux = "+bux+" WHERE UserID = "+target.getUser().getIdLong()).execute();
			        	MessageEmbed success = new EmbedBuilder().setAuthor("DarthBot ~ User Management", null, e.getGuild().getIconUrl()).setColor(Color.orange)
			        			.setDescription(e.getMember().getAsMention()+" *("+e.getAuthor().getId()+")* set "+target.getAsMention()+"'s balance to `$"+bux+"`").build();
			        	e.getChannel().sendMessage(new EmbedBuilder().setDescription(":white_check_mark: Successfully updated "+target.getAsMention()+"'s balance to `$"+bux+"`").build()).queue();
			        	me.darth.darthbot.main.Main.g.getTextChannelById("569883444126023680").sendMessage(success).queue();
			        }
			      }
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			    e.getChannel().sendMessage("<@159770472567799808> Error! ```"+e1+"```").queue();
			}
		}
		
	}
}
