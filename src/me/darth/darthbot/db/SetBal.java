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
		if (args[0].equalsIgnoreCase("!setbalance") || args[0].equalsIgnoreCase("!setbal")) {
			if (!me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getMember(e.getMember().getUser()).getRoles().contains(me.darth.darthbot.main.Main.sm.getRoleById("569463842552152094"))
					&& !me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getMember(e.getMember().getUser()).getRoles().contains(me.darth.darthbot.main.Main.sm.getRoleById("589550625537392643"))) {
				return;
			}
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
				target = me.darth.darthbot.main.Main.findUser(args[1], e.getGuild());
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
			        	MessageEmbed success = new EmbedBuilder().setAuthor("Balance Set", null, e.getGuild().getIconUrl()).setColor(Color.red)
			        			.setDescription(e.getMember().getAsMention()+" set "+target.getAsMention()+"'s balance to `$"+bux+"`").setFooter(e.getGuild().toString(), null).build();
			        	e.getChannel().sendMessage(new EmbedBuilder().setDescription(":white_check_mark: Successfully updated "+target.getAsMention()+"'s balance to `$"+bux+"`").build()).queue();
			        	me.darth.darthbot.main.Main.sm.getTextChannelById("590155925407531008").sendMessage(success).queue();
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
