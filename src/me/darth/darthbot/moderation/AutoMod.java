package me.darth.darthbot.moderation;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Invite;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AutoMod extends ListenerAdapter {

	@Override
	public void onMessageUpdate(MessageUpdateEvent e) {
		//chatcheck
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			//chatcheck
			if (e.getAuthor().isFake() || e.getAuthor().isBot()) {
				return;
			}
			String fstring = null;
			String[] args = e.getMessage().getContentRaw().split(" ");
			ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (rs.next()) {
				if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(rs.getLong("Moderator"))) && rs.getString("Filter") != null && !rs.getString("Filter").equals("")) {
					for (int x = 0 ; x < args.length ; x++) {
						String[] filter = rs.getString("Filter").split(",");
						fstring = rs.getString("Filter");
						for (int y = 1 ; y < filter.length ; y++) {
							if (args[x].replace("||", "").equalsIgnoreCase(filter[y])) {
								e.getMessage().delete().queue();
								e.getChannel().sendMessage(e.getMember().getAsMention()+", some of your message contained content blocked by the Chat Filter!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
							}
						}
					}
					String[] filter = rs.getString("Filter").split(",");
					fstring = rs.getString("Filter");
					
				}
			}
		} catch (SQLException e1) {e1.printStackTrace();}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		if (e.getAuthor().isFake() || e.getAuthor().isBot()) {
			return;
		}
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			
			//chatcheck
			String fstring = null;
			boolean blockInvites = false;
			String[] args = e.getMessage().getContentRaw().split(" ");
			ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (rs.next()) {
				blockInvites = rs.getBoolean("blockInvites");
				fstring = rs.getString("Filter");
				if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(rs.getLong("Moderator"))) && rs.getString("Filter") != null && !rs.getString("Filter").equals("")) {
					for (int x = 0 ; x < args.length ; x++) {
						String[] filter = rs.getString("Filter").split(",");
						for (int y = 1 ; y < filter.length ; y++) {
							if (args[x].equalsIgnoreCase(filter[y])) {
								e.getMessage().delete().queue();
								e.getChannel().sendMessage(e.getMember().getAsMention()+", some of your message contained content blocked by the Chat Filter!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
							}
						}
						
					}
					
				}
				if (blockInvites) {
					List<Invite> invites = e.getGuild().getInvites().complete();
					for (int y = 0 ; y < e.getMessage().getInvites().size() ; y++) {
						String i = e.getMessage().getInvites().get(y);
						boolean valid = false;
						for (int z = 0 ; z < invites.size() ; z++) {
							if (invites.get(z).getCode().equals(i)) {
								valid=true;
							}
						}
						if (!valid) {
							e.getChannel().sendMessage(e.getAuthor().getAsMention()+",:no_entry: You are not allowed to post server invite links!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
							e.getMessage().delete().queue();
						}
					}
				}
			}
			
			if (args[0].equalsIgnoreCase("!automod") || args[0].equalsIgnoreCase("!am")) {
				if (!e.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
					e.getChannel().sendMessage(":no_entry: You must have the **Administrator** permission to setup **Auto Moderation**!").queue();
					return;
				}
				String filtem = ":no_entry:";
				if (fstring != null && !fstring.isEmpty()) {
					filtem = ":white_check_mark:";
				}
				String blockinvs = ":no_entry:";
				if (blockInvites) {
					blockinvs = ":white_check_mark:";
				}
				EmbedBuilder help = new EmbedBuilder().setAuthor("Automatic Server Moderation", null, e.getGuild().getIconUrl()).setColor(Color.blue)
						.setFooter("[NOTE] Members with the Moderation Role will bypass all of the below rules!", null)
						.addField(filtem+"Chat Filter", "`!AutoMod Filter Add <Word>` Stops a word from being said in chat\n"
								+ "`!AutoMod Filter Remove <Word>` Allows a word to be said in chat again\n"
								+ "`!AutoMod Filter List` Lists all the words currently filtered", false)
						.addField(blockinvs+"Block Invites", "`!AutoMod BlockInvites Enable` Enable the blocking of server invites\n"
								+ "`!AutoMod BlockInvites Disable` Disable the blocking of server invites", false)
						.setDescription("*Automatic punishments will be released soon after data on the auto-mod system has been gathered!*");
						
				if (args.length <= 2) {
					e.getChannel().sendMessage(help.build()).queue();
					return;
				}
				if (args[1].toLowerCase().contains("blockinv")) {
					if (args.length < 3) {
						e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!AutoMod BlockInvites Enable/Disable`").queue();
						return;
					}
					if (args[2].equalsIgnoreCase("enable")) {
						con.prepareStatement("UPDATE GuildInfo SET blockInvites = 1 WHERE GuildID = "+e.getGuild().getId()).execute();
						e.getChannel().sendMessage(":white_check_mark: DarthBot will now block server invites!").queue();
					} else if (args[2].equalsIgnoreCase("disable")) {
						con.prepareStatement("UPDATE GuildInfo SET blockInvites = 0 WHERE GuildID = "+e.getGuild().getId()).execute();
						e.getChannel().sendMessage(":white_check_mark: DarthBot will no longer block server invites!").queue();
					} else {
						e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!AutoMod BlockInvites Enable/Disable`").queue();
						return;
					}
 				} else if (args[1].equalsIgnoreCase("filter") || args[1].equalsIgnoreCase("f")) {
					if (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("a")) {
						if (args.length <= 3) {
							e.getChannel().sendMessage(help.build()).queue();
							return;
						}
						String term = e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" "+args[2]+" ", "");
						ResultSet fa = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
						while (fa.next()) {
							String str = fa.getString("Filter");
							if (str != null && !str.equals("") && !str.isEmpty() && str.toLowerCase().contains(","+term.toLowerCase())) {
								e.getChannel().sendMessage(":no_entry: This term is already filtered!").queue();
							} else {
								String newstr = null;
								if (str == null || str.equals("") || str.isEmpty()) {
									newstr = ","+term;
								} else {
									newstr = str+","+term;
								}
								PreparedStatement ps = con.prepareStatement("UPDATE GuildInfo SET Filter = ? WHERE GuildID = "+e.getGuild().getId());
								ps.setString(1, newstr);
								ps.execute();
								e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.green).setDescription(":white_check_mark: Successfully added `"+term+"` to the filter!").build()).queue();
							}
							
							
						}
					} else if (args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("r")) {
						if (args.length <= 3) {
							e.getChannel().sendMessage(help.build()).queue();
							return;
						}
						String term = e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" "+args[2]+" ", "");
						ResultSet fr = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
						while (fr.next()) {
							String str = fr.getString("Filter");
							if (str != null && !str.equals("") && !str.isEmpty() && str.contains(","+term)) {
								String newstr = str.replace(","+term, "");
								PreparedStatement ps = con.prepareStatement("UPDATE GuildInfo SET Filter = ? WHERE GuildID = "+e.getGuild().getId());
								ps.setString(1, newstr);
								ps.execute();
								e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.red).setDescription(":white_check_mark: Successfully removed `"+term+"` from the filter!").build()).queue();
							} else {
								e.getChannel().sendMessage(":no_entry: That term is not filtered! *(Case Sensitive!)*").queue();
							}
							
							
						}
					} else if (args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
						ResultSet fr = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
						while (fr.next()) {
							String[] str = fr.getString("Filter").split(",");
							EmbedBuilder eb = new EmbedBuilder().setAuthor("Automatic Server Moderation", null, e.getGuild().getIconUrl()).setColor(Color.blue);
							for (int x = 1 ; x < str.length ; x++) {
								eb.addField(str[x], "", true);
							}
							e.getChannel().sendMessage(eb.build()).queue();
						}
					}
				} else {
					e.getChannel().sendMessage(help.build()).queue();
				}
				con.close();
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

}
