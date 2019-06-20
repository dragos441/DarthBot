package me.darth.darthbot.db;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Give extends ListenerAdapter{

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!give") || args[0].equalsIgnoreCase("!donate") || args[0].equalsIgnoreCase("!transfer") || args[0].equalsIgnoreCase("!pay")) {
			
			e.getChannel().sendMessage("The `!give` command has been temporarily disabled due to ongoing internal issues! Sorry for the inconvinence!\n*You can use the `!rob` command a substitute!*").queue();
			return;
			/*
			if (args.length < 3) {
				e.getChannel().sendMessage("Invalid Syntax: `"+args[0]+" <User> <Amount>`").queue();
				return;
			}
			Member m = null;
			if (!e.getMessage().getMentionedMembers().isEmpty()) {
				m = e.getMessage().getMentionedMembers().get(0);
			} else {
				m = me.darth.darthbot.main.Main.findUser(args[1], e.getGuild());
			}
			if (m == null) {
				e.getChannel().sendMessage(":no_entry: User not found!").queue();
				return;
			}
			
			if (m.equals(e.getMember())) {
				e.getChannel().sendMessage(":no_entry: You cannot give money to yourself!").queue();
				return;
			}
			int amount = -1;
			try {
				amount = Integer.parseInt(args[2].replace("$", ""));
			} catch (NumberFormatException e1) {
				e.getChannel().sendMessage("Invalid Syntax: `"+args[0]+" <User> <Amount>`").queue();
			}
			if (amount < 1) {
				e.getChannel().sendMessage(":no_entry: You must give at least $1!").queue();
				return;
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet to = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+m.getUser().getIdLong());
				while (to.next()) {
					if (to.getLong("DBux") == -1337) {
						e.getChannel().sendMessage(":no_entry: You can't give money to this user!").queue();
						return;
					}
					long newtotal = to.getLong("DBux") + amount;
					con.prepareStatement("UPDATE profiles SET DBux = "+newtotal+" WHERE UserID = "+m.getUser().getId()).execute();
				}
				ResultSet from = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getIdLong());
				while (from.next()) {
					if (amount > from.getLong("DBux")) {
						e.getChannel().sendMessage(":no_entry: You don't have that much to give!").queue();
						return;
					}
					long newtotal = from.getLong("DBux") - amount;
					con.prepareStatement("UPDATE profiles SET DBux = "+newtotal+" WHERE UserID = "+e.getAuthor().getId()).execute();
				}
				e.getChannel().sendMessage("Successfully given "+m.getAsMention()+" `$"+amount+"`!").queue();
				EmbedBuilder eb = new EmbedBuilder().setAuthor("Money Given", null, e.getGuild().getIconUrl()).setDescription(e.getMember().getAsMention()+" has "
						+ "given "+m.getAsMention()+" `$"+amount+"`").setColor(Color.green).setFooter(e.getGuild()+"", null);
				me.darth.darthbot.main.Main.sm.getTextChannelById("590155719790166082").sendMessage(eb.build()).queue();
			} catch (SQLException e1) {e1.printStackTrace();}*/
			
		}
	}
	
}
