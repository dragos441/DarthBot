package me.darth.darthbot.db;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class LevelRoles extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!rolerewards") || args[0].equalsIgnoreCase("!rr")) {
			if (!e.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
				e.getChannel().sendMessage(":no_entry: You must have the **Administrator** permission to manage the bot!").queue();
				return;
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				EmbedBuilder commands = new EmbedBuilder().setAuthor("Level Role Rewards", null, e.getGuild().getIconUrl())
						.setDescription("`!rr add <Level> <Role>` Adds a role reward to a level"
								+ "\n`!rr remove <Level>` Removes a role reward from a level"
								+ "\n`!rr list` Lists all the current role rewards for levels"
								+ "\n`!rr help` Displays this list of commands").setColor(Color.blue);
				if (args.length <= 1 || args[1].equalsIgnoreCase("help")) {
					e.getChannel().sendMessage(commands.build()).queue();
					return;
				}
				if (args[1].equalsIgnoreCase("list")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM RoleRewards WHERE GuildID = "+e.getGuild().getIdLong()+" ORDER BY Level ASC");
					EmbedBuilder eb = new EmbedBuilder().setAuthor("Level Role Rewards - Rewards List", null, e.getGuild().getIconUrl()).setColor(Color.blue);
					while (rs.next()) {
						Role r = e.getGuild().getRoleById(rs.getLong("RoleID"));
						if (r != null) {
							eb.addField("Level "+rs.getInt("Level"), r.getAsMention(), true);
						} else {
							eb.addField("Level "+rs.getInt("Level"), "Role Not Found! ("+rs.getLong("RoleID")+")", true);
						}
					}
					e.getChannel().sendMessage(eb.build()).queue();
				}
				if (args[1].equalsIgnoreCase("remove")) {
					if (args.length <= 2) {
						e.getChannel().sendMessage(commands.build()).queue();
						return;
					}
					int level = -1;
					try {
						level = Integer.parseInt(args[2]);
						if (level > 100) {
							Integer.parseInt("INT_TOO_LARGE");
						}
					} catch (NumberFormatException e1) {
						e.getChannel().sendMessage(":no_entry: That is not a valid level! (0-100)").queue();
					}
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM RoleRewards WHERE GuildID = "+e.getGuild().getIdLong());
					while (rs.next()) {
						if (rs.getInt("Level") == level) {
							con.prepareStatement("DELETE FROM RoleRewards WHERE GuildID = "+e.getGuild().getIdLong()+" AND Level = "+level).execute();
							e.getChannel().sendMessage(":white_check_mark: Removed the role reward for **Level "+rs.getInt("Level")+"**").queue();
							return;
						}
					}
					e.getChannel().sendMessage(":no_entry: Couldn't find a role reward for **Level "+level+"**").queue();
				}
				if (args[1].equalsIgnoreCase("add")) {
					if (args.length <= 3) {
						e.getChannel().sendMessage(commands.build()).queue();
						return;
					}
					int level = -1;
					try {
						level = Integer.parseInt(args[2]);
						if (level > 100) {
							Integer.parseInt("INT_TOO_LARGE");
						}
					} catch (NumberFormatException e1) {
						e.getChannel().sendMessage(":no_entry: That is not a valid level! (0-100)").queue();
					}
					Role role = null;
					if (!e.getMessage().getMentionedRoles().isEmpty()) {
						role = e.getMessage().getMentionedRoles().get(0);
					} else if (!e.getGuild().getRolesByName(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" "+args[2]+" ", ""), true).isEmpty()) {
						role = e.getGuild().getRolesByName(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" "+args[2]+" ", ""), true).get(0);
					} else {
						try {
							role = e.getGuild().getRoleById(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" "+args[2]+" ", ""));
						} catch (NumberFormatException e1) {role=null;}
					}
					if (role == null) {
						e.getChannel().sendMessage(":no_entry: Couldn't find role!").queue();
						return;
					}
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM RoleRewards WHERE GuildID = "+e.getGuild().getIdLong());
					while (rs.next()) {
						if (rs.getInt("Level") == level) {
							e.getChannel().sendMessage(":no_entry: That role already have a role reward! Remove it with `!rr remove "+level+"`").queue();
							return;
						}
					}
					con.prepareStatement("INSERT INTO RoleRewards (GuildID, Level, RoleID) values ("+e.getGuild().getIdLong()+", "+level+", "+role.getIdLong()+")").execute();
					e.getChannel().sendMessage(":white_check_mark: When users achieve **Level "+level+"**, they will recieve the `"+role.getName()+"` role!").queue();
				}
				
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
	}

}
