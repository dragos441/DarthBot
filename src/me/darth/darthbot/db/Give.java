package me.darth.darthbot.db;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Give extends ListenerAdapter{

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!give") || args[0].equalsIgnoreCase("!donate") || args[0].equalsIgnoreCase("!transfer") || args[0].equalsIgnoreCase("!pay")) {
			
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
			long amount = -1;
			try {
				amount = Integer.parseInt(args[2].replace("$", ""));
			} catch (NumberFormatException e1) {
				e.getChannel().sendMessage("Invalid Syntax: `"+args[0]+" <User> <Amount>`").queue();
			}
			if (amount < 1) {
				e.getChannel().sendMessage(":no_entry: You must give at least $1!").queue();
				return;
			}
			long tax = 0;
			String msgadd = "";
			if (amount >= 100000 && amount < 1000000) {
				
				tax = amount / 20;
				amount = amount - tax;
				msgadd="\n*The amount was taxed by **5%** due to it being >= `$100,000`!*";
				
			} else if (amount >= 1000000) {
				
				tax = amount / 10;
				amount = amount - tax;
				msgadd="\n*The amount was taxed by **10%** due to it being >= `$1,000,000`!*";
				
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet from = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getIdLong());
				while (from.next()) {
					if (amount > from.getLong("DBux")) {
						e.getChannel().sendMessage(":no_entry: You don't have that much to give!").queue();
						return;
					}
					long lastGiven = from.getLong("Given");
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.HOUR, -1);
					if (lastGiven > cal.getTimeInMillis()) {
						Calendar claimed = Calendar.getInstance();
			        	claimed.setTimeInMillis(lastGiven);
			        	long mins = ChronoUnit.MINUTES.between(cal.toInstant(), claimed.toInstant()) - 1;
			        	int hours = 0;
			        	while (mins >= 60) {
			        		hours++;
			        		mins = mins - 60;
			        	}
						e.getChannel().sendMessage(":no_entry: You've already sent money this hour! "
								+ "You can give another user money in **"+hours+"** hours and **"+mins+"** minutes!").queue();
						return;
					} else {
						long newtotal = from.getLong("DBux") - amount - tax;
						con.prepareStatement("UPDATE profiles SET DBux = "+newtotal+", Given = "+Calendar.getInstance().getTimeInMillis()+" WHERE UserID = "+e.getAuthor().getId()).execute();
					}
				}
				ResultSet to = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+m.getUser().getIdLong());
				while (to.next()) {
					if (to.getLong("DBux") == -1337) {
						e.getChannel().sendMessage(":no_entry: You can't give money to this user!").queue();
						return;
					}
					long newtotal = to.getLong("DBux") + amount;
					con.prepareStatement("UPDATE profiles SET DBux = "+newtotal+" WHERE UserID = "+m.getUser().getId()).execute();
				}
				e.getChannel().sendMessage("Successfully given "+m.getAsMention()+" `$"+amount+"`!"+msgadd).queue();
				EmbedBuilder eb = new EmbedBuilder().setAuthor("Money Given", null, e.getGuild().getIconUrl()).setDescription(e.getMember().getAsMention()+" has "
						+ "given "+m.getAsMention()+" `$"+amount+"`").setColor(Color.green).setFooter(e.getGuild()+"", e.getGuild().getIconUrl()).setTimestamp(Instant.from(ZonedDateTime.now()));
				me.darth.darthbot.main.Main.sm.getTextChannelById("590155719790166082").sendMessage(eb.build()).queue();
			} catch (SQLException e1) {e1.printStackTrace();}
			
		}
	}
	
}
