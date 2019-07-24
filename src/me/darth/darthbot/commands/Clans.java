package me.darth.darthbot.commands;

import java.awt.Color;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Clans extends ListenerAdapter {
	
	final static int season = 2;
	
	public static MessageEmbed viewClan(String clanID, User bot) {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
			while (rs.next()) {
				try {
					if (rs.getString("Name").equalsIgnoreCase(clanID) || rs.getInt("ID") == Integer.parseInt(clanID)) {
						EmbedBuilder eb = new EmbedBuilder();
						eb.setFooter("[S"+season+"] Clan Created", null);
						Color c = null;
						if (rs.getString("Colour") != null) {
							eb.setColor(rs.getInt("Colour"));
						}
						eb.setAuthor("["+rs.getInt("ID")+"] "+rs.getString("Name")+" [$"+new DecimalFormat("#,###").format(rs.getLong("Bank"))+"]");
						if (rs.getString("Picture") != null) {
							eb.setThumbnail(rs.getString("Picture"));
						}
						User founder = me.darth.darthbot.main.Main.sm.getUserById(rs.getLong("Founder"));
						String members = "";
						String[] memberssplit = rs.getString("Members").split(",");
						for (int x = 0 ; x < memberssplit.length ; x++) {
							try {
								User m = me.darth.darthbot.main.Main.sm.getUserById(memberssplit[x]);
								members = members+"\n"+m.getAsMention();
							} catch (IllegalArgumentException | NullPointerException e1) {}
						}
						String officers = "";
						if (rs.getString("Officers") != null && !rs.getString("Officers").isEmpty()) {
							String[] officerssplit = rs.getString("Officers").split(",");
							for (int x = 0 ; x < officerssplit.length ; x++) {
								try {
									User m = me.darth.darthbot.main.Main.sm.getUserById(officerssplit[x]);
									officers = officers+"\n"+m.getAsMention();
								} catch (IllegalArgumentException e1) {}
							}
						}
						//eb.addField("Founder", founder.getAsMention(), true);
						eb.addField("Members "+Math.subtractExact(memberssplit.length, 1)+"/25", members, true);
						if (officers.equals("")) {
							eb.addField("Officers", "*No Officers*", true);
						} else {
							eb.addField("Officers", officers, true);
						}
						eb.addField("Join Status", rs.getString("Status"), true);
						String desc = "**Founder:** "+founder.getAsMention();
						if (rs.getString("Description") != null) {
							desc=desc+"\nDescription: "+rs.getString("Description");
							eb.setDescription(desc);
						}
						eb.setTimestamp(Instant.ofEpochMilli(rs.getLong("Created")));
						return eb.build();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (NumberFormatException e2) {}
			}
		} catch (SQLException e1) {}
		
		return null;
		
	}
	
	@SuppressWarnings("unused")
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!clan") || args[0].equalsIgnoreCase("!clans") && args.length == 1 || args.length > 1 && args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("list")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				EmbedBuilder help = new EmbedBuilder().setAuthor("Clans Commands", null, "http://icons.iconarchive.com/icons/google/noto-emoji-objects/256/62963-crossed-swords-icon.png")
						.setColor(Color.blue);
				help.setDescription("**General Commands**\n"
						+ "`!clan` View the Clan that you're in\n"
						+ "`!clan create <Name>` Create a clan\n"
						+ "`!clan join <Name/ID>` Joins a clan\n"
						+ "`!clan leave` Leaves a clan\n"
						+ "`!clan deposit <Amount>` Deposit an amount to your Clan Bank\n"
						+ "`!clan withdraw <Amount>` Withdraw an amount to your Clan Bank `[OFFICER]`\n"
						+ "`!clans` View all clans\n"
						+ "\n**Management**\n"
						+ "`!clan open` Opens the clan to the public\n"
						+ "`!clan close` Closes the clan (invite only)\n"
						+ "`!clan invite <User>` Invites a user to the Clan\n"
						+ "`!clan promote <User>` Promotes a member to Officer\n"
						+ "`!clan demote <User>` Demotes a member from Officer\n"
						+ "`!clan kick <User>` Kicks a user from the Clan\n"
						+ ":new:`!clan description <Description>` Set a description for the Clan `2+ MEMBERS`\n"
						+ ":new:`!clan colour <Colour>` Set a colour for your Clan `5+ MEMBERS`\n"
						+ ":new:`!clan avatar <URL>` Set an avatar for your Clan `10+ MEMBERS`");
				if (args[0].equalsIgnoreCase("!clan") && args.length == 1) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs.next()) {
						if (rs.getString("Members").contains(","+e.getAuthor().getIdLong())) {
							if (rs.getString("Members").contains(","+e.getAuthor().getId())) {
								e.getChannel().sendMessage(viewClan(rs.getInt("ID")+"", e.getJDA().getSelfUser())).queue();
							}
							return;
						}
					}
					e.getChannel().sendMessage(":no_entry: You're not currently in a clan. To join one, use `!clans join <Clan>`!").queue();
					return;
				} else if (args[0].equalsIgnoreCase("!clans") && args.length > 1) {
	 				e.getChannel().sendMessage(":no_entry: The `!clans` command is just for viewing the Clan List! Did you mean `!clan`?").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].toLowerCase().contains("perk")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs.next()) {
						if (rs.getString("Members").contains(","+e.getAuthor().getId())) {
							
							EmbedBuilder eb = new EmbedBuilder().setAuthor("Clan Perks", null, "http://icons.iconarchive.com/icons/google/noto-emoji-objects/256/62963-crossed-swords-icon.png");
							eb.setDescription("Bank Size: `$max` :white_small_square::black_small_square::black_small_square::black_small_square::black_small_square:`UPGRADE: $UPGRADE`\n"
									+ "Clan Size: `$max` :white_small_square::white_small_square::black_small_square::black_small_square::black_small_square: ` UPGRADE: $UPGRADE`").setColor(Color.blue);
							int members = rs.getString("Members").split(",").length - 1;
							String desc = "🔒";
							String colour = "🔒";
							String picture = "🔒";
							if (members >= 2) {
								desc = "🔓";
							} if (members >= 5) {
								colour = "🔓";
							} if (members >= 10) {
								picture = "🔓";
							}
							eb.addField("Cosmetics", "**Clan Description** `2 Members` "+desc
									+ "\n**Clan Colour** `5 Members` "+colour
									+ "\n**Clan Avatar** `10 Members` "+picture, true);
							eb.setFooter("Clans Season "+season+" | !upgrade <BANK / SIZE>", null);
							e.getChannel().sendMessage(eb.build()).queue();
						}
					}
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("desc") || args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("description")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					while (rs.next()) {
						int members = Math.subtractExact(rs.getString("Members").split(",").length, 1);
						if (members < 2) {
							e.getChannel().sendMessage("You must have `2` Clan members to use this command!").queue();
							return;
						}
						if (args.length < 3) {
							e.getChannel().sendMessage(":no_entry: Invalid Syntax `!clan description <Clan Description>`").queue();
							return;
						} else if (e.getMessage().getContentRaw().length() > 200) {
							e.getChannel().sendMessage(":no_entry: Your description is too long").queue();
							return;
						} else {
							PreparedStatement ps = con.prepareStatement("UPDATE Clans SET Description =? WHERE ID = "+rs.getInt("ID"));
							ps.setString(1, e.getMessage().getContentRaw().replace(args[0]+" "+args[1], ""));
							ps.execute();
							e.getChannel().sendMessage(":white_check_mark: Successfully updated Clan's description!").queue();
							return;
						}
					}
					e.getChannel().sendMessage(":no_entry: You don't own a Clan!").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("colour") || args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("color")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					while (rs.next()) {
						int members = Math.subtractExact(rs.getString("Members").split(",").length, 1);
						if (members < 5) {
							e.getChannel().sendMessage("You must have `5` Clan members to use this command!").queue();
							return;
						}
						if (args.length < 3) {
							e.getChannel().sendMessage(":no_entry: Invalid Syntax `!clan colour <Clan Description>`").queue();
						} else {
							Color c = null;
							try {
								Field f = Color.class.getField(args[2]);
								c = (Color)f.get(null);
							} catch (Exception e1) {}
							if (c == null) {
								e.getChannel().sendMessage(":no_entry: That is not a valid Colour!\n`[RED, GREEN, BLUE, MAGENTA, CYAN, YELLOW, BLACK, WHITE, GRAY, DARK_GRAY, LIGHT_GRAY, ORANGE, PINK]`").queue();
								return;
							}
							PreparedStatement ps = con.prepareStatement("UPDATE Clans SET Colour = ? WHERE ID = "+rs.getInt("ID"));
							ps.setString(1, c.getRGB()+"");
							ps.execute();
							e.getChannel().sendMessage(":white_check_mark: Successfully updated Clan's colour!").queue();
						}
						return;
					}
					e.getChannel().sendMessage(":no_entry: You don't own a Clan!").queue();			
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("avatar") || args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("picture") || args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("image")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					while (rs.next()) {
						int members = Math.subtractExact(rs.getString("Members").split(",").length, 1);
						if (members < 10) {
							e.getChannel().sendMessage("You must have `10` Clan members to use this command!").queue();
							return;
						}
						if (args.length < 3) {
							e.getChannel().sendMessage(":no_entry: Invalid Syntax `!clan avatar <Clan Description>`").queue();
						} else {

							try {
								EmbedBuilder test = new EmbedBuilder().setImage(args[2]);
							} catch (IllegalArgumentException e1) {
								e.getChannel().sendMessage(":no_entry: That is not a valid image URL!").queue();
								return;
							}
							
							PreparedStatement ps = con.prepareStatement("UPDATE Clans SET Picture = ? WHERE ID = "+rs.getInt("ID"));
							ps.setString(1, args[2]);
							ps.execute();
							e.getChannel().sendMessage(":white_check_mark: Successfully updated Clan's avatar!").queue();
						}
						return;
					}
					e.getChannel().sendMessage(":no_entry: You don't own a Clan!").queue();			
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("promote")) {
					
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					while (rs.next()) {
						Member m = null;
						if (!e.getMessage().getMentionedMembers().isEmpty()) {
							m = e.getMessage().getMentionedMembers().get(0);
						} else {
							m = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), e.getGuild());
						}
						if (m == null) {
							e.getChannel().sendMessage(":no_entry: Member not found!").queue();
							return;
						} else {
							if (!rs.getString("Members").contains(","+m.getUser().getId())) {
								e.getChannel().sendMessage(":no_entry: That user is not a member of your Clan!").queue();
								return;
							}
							String officers = rs.getString("Officers");
							if (officers != null && officers.contains(","+m.getUser().getId())) {
								e.getChannel().sendMessage(":no_entry: That user is already an Officer of the Clan!").queue();
							} else {
								if (officers == null) {
									officers=","+m.getUser().getId();
								} else {
									officers=officers+","+m.getUser().getId();
								}
								con.prepareStatement("UPDATE Clans SET Officers = '"+officers+"' WHERE ID = "+rs.getInt("ID")).execute();
								e.getChannel().sendMessage(":white_check_mark: Successfully promoted member to Officer!").queue();
								return;
							}
						}
					}
					e.getChannel().sendMessage(":no_entry: You do not own a clan!").queue();
					
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("demote")) {
					
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					while (rs.next()) {
						Member m = null;
						if (!e.getMessage().getMentionedMembers().isEmpty()) {
							m = e.getMessage().getMentionedMembers().get(0);
						} else {
							m = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), e.getGuild());
						}
						if (m == null) {
							e.getChannel().sendMessage(":no_entry: Member not found!").queue();
							return;
						} else {
							String officers = rs.getString("Officers");
							if (!officers.contains(","+m.getUser().getId())) {
								e.getChannel().sendMessage(":no_entry: That user is not an Officer of the Clan!").queue();
							} else {
								officers = officers.replace(","+m.getUser().getId(), "");
								con.prepareStatement("UPDATE Clans SET Officers = '"+officers+"' WHERE ID = "+rs.getInt("ID")).execute();
								e.getChannel().sendMessage(":white_check_mark: Successfully demoted member from Officer!").queue();
								return;
							}
						}
					}
					e.getChannel().sendMessage(":no_entry: You do not own a clan!").queue();
					
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("invite")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					while (rs.next()) {
						Member m = null;
						if (!e.getMessage().getMentionedMembers().isEmpty()) {
							m = e.getMessage().getMentionedMembers().get(0);
						} else {
							m = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), e.getGuild());
						}
						if (m == null) {
							e.getChannel().sendMessage(":no_entry: Member not found!").queue();
							return;
						} else {
							final Member target = m;
							String invites = rs.getString("Invites");
							if (invites != null && invites.contains(","+m.getUser().getId())) {
								e.getChannel().sendMessage(":no_entry: This user is already invited!").queue();
								return;
							}
							if (invites == null) {
								invites = ","+m.getUser().getId();
							} else {
								invites = invites+","+m.getUser().getId();
							}
							con.prepareStatement("UPDATE Clans SET Invites = '"+invites+"' WHERE ID = "+rs.getInt("ID")).execute();
							e.getChannel().sendMessage("Successfully invited user to the Clan!").queue();
							String name = rs.getString("Name");
							m.getUser().openPrivateChannel().queue((channel) ->
					        {
					        	Message message = null;
					        	
								try {
									message = channel.sendMessage("You have been invited to join the **"+name+"** Clan! Type `!clan join "+name+"` in a server to join!").submit().get();
								} catch (InterruptedException e1) {} catch (ExecutionException e2) {}
								if (message == null) {
									e.getChannel().sendMessage(target.getAsMention()).embed(new EmbedBuilder().setColor(Color.orange)
											.setDescription("You have been invited to join the **"+name+"** Clan! Type `!clan join "+name+"` in a server to join!").build()).queue();
								}
					        });
							return;
					      
						}
					}
					e.getChannel().sendMessage(":no_entry: You do not own a Clan!").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("kick")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					
					while (rs.next()) {
						if (rs.getString("Members").contains(","+e.getAuthor().getId())) {
							Member m = null;
							if (!e.getMessage().getMentionedMembers().isEmpty()) {
								m = e.getMessage().getMentionedMembers().get(0);
							} else {
								m = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), e.getGuild());
							}
							if (m == null) {
								e.getChannel().sendMessage(":no_entry: Member not found!").queue();
								return;
							} else {
								if (m.equals(e.getMember())) {
									e.getChannel().sendMessage("Don't kick yourself, silly.").queue();
									return;
								}
								String members = rs.getString("Members");
								members = members.replace(","+m.getUser().getId(), "");
								con.prepareStatement("UPDATE Clans SET Members = '"+members+"' WHERE ID = "+rs.getInt("ID")).execute();
								e.getChannel().sendMessage(":white_check_mark: Successfully kicked user!").queue();
								return;
							}
						}
					}
					e.getChannel().sendMessage(":no_entry: You do not own a clan!").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("join")) {
					ResultSet rs2 = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs2.next()) {
						if (rs2.getString("Members").contains(","+e.getAuthor().getId())) {
							e.getChannel().sendMessage(":no_entry: You are already in a Clan!").queue();
							return;
						}
					}
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs.next()) {
						try {
							String invites = rs.getString("Invites");
							if (invites == null) {
								invites = "";
							}
							if (e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", "").equalsIgnoreCase(rs.getString("Name")) || Integer.parseInt(args[2]) == rs.getInt("ID")) {
								if (rs.getString("Status").equals("CLOSED") && !invites.contains(","+e.getAuthor().getId()) && rs.getLong("Founder") != e.getAuthor().getIdLong()) {
									e.getChannel().sendMessage(":no_entry: That clan is invite only!").queue();
									return;
								} else {
									String members = rs.getString("Members");
									if (Math.subtractExact(members.split(",").length, 1) >= 25) {
										e.getChannel().sendMessage(":no_entry: This Clan is full!").queue();
										return;
									}
									members=members+","+e.getAuthor().getId();
									invites=invites.replace(","+e.getAuthor().getId(), "");
									con.prepareStatement("UPDATE Clans SET Members = '"+members+"', Invites = '"+invites+"' WHERE ID = "+rs.getInt("ID")).execute();
									e.getChannel().sendMessage(":white_check_mark: You successfully joined the Clan!").queue();
								}
								return;
							}
						} catch (NumberFormatException e1) {}
					}
					e.getChannel().sendMessage(":no_entry: No clan exists with that ID or Name!").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("leave")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs.next()) {
						if (rs.getString("Members").contains(","+e.getAuthor().getId())) {
							String members = rs.getString("Members");
							members = members.replace(","+e.getAuthor().getIdLong(), "");
							System.out.print(members);
							con.prepareStatement("UPDATE Clans SET Members = '"+members+"' WHERE ID = "+rs.getInt("ID")).execute();
							e.getChannel().sendMessage(":white_check_mark: You successfully left the Clan!").queue();
							return;
						}
					}
					e.getChannel().sendMessage(":no_entry: You are not in a Clan!").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("close")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					while (rs.next()) {
						if (rs.getString("Status") != null && rs.getString("Status").equalsIgnoreCase("CLOSED")) {
							e.getChannel().sendMessage(":no_entry: Your clan is already closed!").queue();
							return;
						}
						con.prepareStatement("UPDATE Clans SET Status = 'CLOSED' WHERE Founder = "+e.getAuthor().getId()).execute();
						e.getChannel().sendMessage(":white_check_mark: Succesfully closed your Clan to invite only!").queue();
						return;
					}
					e.getChannel().sendMessage(":no_entry: You don't own a clan!").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("withdraw")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs.next()) {
						if (rs.getString("Members").contains(","+e.getAuthor().getId())) {
							if (rs.getString("Officers") == null || !rs.getString("Officers").contains(","+e.getAuthor().getId())) {
								if (rs.getLong("Founder") != e.getAuthor().getIdLong()) {
									e.getChannel().sendMessage(":no_entry: You must be an **Officer** to withdraw from the Clan Bank!").queue();
									return;
								}
							}
							long amount = -1L;
							try {
								amount = Long.parseLong(args[2]);
								if (amount < 1) {
									e.getChannel().sendMessage(":no_entry: The amount must be greater than $1!").queue();
									return;
								}
							} catch (NumberFormatException e1) {
								e.getChannel().sendMessage(":no_entry: Incorrect Syntax: `!clan withdraw <Amount>`").queue();
								return;
							}
							ResultSet money = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getId());
							while (money.next()) {
								if (rs.getInt("Bank") < amount) {
									e.getChannel().sendMessage(":no_entry: There is not that amount of money in the bank to withdraw!").queue();
									return;
								} else {
									con.prepareStatement("UPDATE profiles SET DBux = DBux + "+amount+" WHERE UserID = "+e.getAuthor().getId()).execute();
									con.prepareStatement("UPDATE Clans SET Bank = Bank - "+amount+" WHERE ID = "+rs.getInt("ID")).execute();
									e.getChannel().sendMessage(":white_check_mark: Successfully withdrew $**"+amount+"** from the Clans bank!").queue();
								}
							}
							
						}
					}
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("deposit")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs.next()) {
						if (rs.getString("Members").contains(","+e.getAuthor().getId())) {
							long amount = -1L;
							try {
								amount = Long.parseLong(args[2]);
								if (amount < 1) {
									e.getChannel().sendMessage(":no_entry: The amount must be greater than $1!").queue();
									return;
								}
							} catch (NumberFormatException e1) {
								e.getChannel().sendMessage(":no_entry: Incorrect Syntax: `!clan deposit <Amount>`").queue();
								return;
							}
							ResultSet money = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getId());
							while (money.next()) {
								if (money.getLong("DBux") < amount) {
									e.getChannel().sendMessage(":no_entry: You don't have that amount to deposit!").queue();
									return;
								} else {
									con.prepareStatement("UPDATE profiles SET DBux = DBux - "+amount+" WHERE UserID = "+e.getAuthor().getId()).execute();
									con.prepareStatement("UPDATE Clans SET Bank = Bank + "+amount+" WHERE ID = "+rs.getInt("ID")).execute();
									e.getChannel().sendMessage(":white_check_mark: Successfully added $**"+amount+"** to the Clans bank!").queue();
								}
							}
							
						}
					}
				} else if (args.length > 1 && args[1].equalsIgnoreCase("help") || args.length > 1 && args[1].equalsIgnoreCase("commands")) {
					e.getChannel().sendMessage(help.build()).queue();
				} else if (args.length > 1 && args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("open")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					while (rs.next()) {
						if (rs.getString("Status").equalsIgnoreCase("OPEN")) {
							e.getChannel().sendMessage(":no_entry: Your clan is already open!").queue();
							return;
						}
						con.prepareStatement("UPDATE Clans SET Status = 'OPEN' WHERE Founder = "+e.getAuthor().getId()).execute();
						e.getChannel().sendMessage(":white_check_mark: Succesfully opened your Clan to all users!").queue();
						return;
					}
					e.getChannel().sendMessage(":no_entry: You don't own a clan!").queue();
				} else if (args.length > 1 && args[1].equalsIgnoreCase("create")) {
					ResultSet user = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getId());
					while (user.next()) {
						if (user.getLong("DBux") < 100000) {
							e.getChannel().sendMessage(":no_entry: A clan costs **$100,000** to create!").queue();
							return;
						}
						if (args.length < 3) {
							e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!clan create <Name>`").queue();
							return;
						}
						ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
						while (rs.next()) {
							if (rs.getLong("Founder") == e.getAuthor().getIdLong()) {
								e.getChannel().sendMessage(":no_entry: You already own a Clan!").queue();
								return;
							} else if (rs.getString("Members").contains(","+e.getAuthor().getId())){
								e.getChannel().sendMessage(":no_entry: You cannot create a Clan while being a member of one!").queue();
								return;
							} else if (rs.getString("Name").equalsIgnoreCase(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""))) {
								e.getChannel().sendMessage(":no_entry: A Clan already exists with this name!").queue();
								return;
							}
						}
						PreparedStatement ps = con.prepareStatement("INSERT INTO Clans (Name, Members, Status, Founder, Bank, Created) values (?, ?, ?, ?, ?, ?)");
						ps.setString(1, e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""));
						ps.setString(2, ","+e.getAuthor().getId());
						ps.setString(3, "OPEN");
						ps.setString(4, e.getAuthor().getId());
						ps.setLong(5, 0);
						ps.setString(6, Calendar.getInstance().getTimeInMillis()+"");
						ps.execute();
						con.prepareStatement("UPDATE profiles SET DBux = DBux - 100000 WHERE UserID = "+e.getAuthor().getIdLong()).execute();
						e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.green).setDescription(":white_check_mark: Clan successfully created!").build()).queue();
					}
										
 				} else if (args[0].equalsIgnoreCase("!clan")) {
 					try {
 						e.getChannel().sendMessage(viewClan(e.getMessage().getContentRaw().replace(args[0]+" ", ""), e.getJDA().getSelfUser())).queue();
 					} catch (NullPointerException | IllegalArgumentException e1) {
 						e.getChannel().sendMessage(":no_entry: That Clan couldn't be found! Type `!clans` for a list of Clans!").queue();
 					}
 				
  				} else if (args[0].equalsIgnoreCase("!clans") && args.length == 1) {
 					EmbedBuilder eb = new EmbedBuilder().setAuthor("Clan List", null, "http://icons.iconarchive.com/icons/google/noto-emoji-objects/256/62963-crossed-swords-icon.png")
						.setColor(Color.blue);
 					ResultSet rs = con.prepareStatement("SELECT * FROM Clans ORDER BY Bank DESC, LENGTH(MEMBERS) DESC").executeQuery();
 					int counter = 0;
 					int total = 0;
 					while (rs.next()) {
 						if (counter < 10) {
 							int pos = counter + 1;
	 						eb.addField("#"+pos+" "+rs.getString("Name"), "**Members:** `"+new DecimalFormat("#,###").format(Math.subtractExact(rs.getString("Members").split(",").length , 1))+"`"
	 								+ "\n**Bank:** `$"+new DecimalFormat("#,###").format(rs.getLong("Bank"))+"`", false);
	 						counter++;
 						}
 						total++;
 					}
 					eb.setFooter(counter+"/"+total, null);
 					Message msg = e.getChannel().sendMessage(eb.build()).complete();
 					msg.addReaction("⬅").queue();
 					msg.addReaction("➡").queue();
				} else {
 					e.getChannel().sendMessage(help.build()).queue();
 				}
				
			} catch (SQLException e1) {e1.printStackTrace();}
		}
		
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getUser().isBot() || e.getUser().isFake()) {
			return;
		}
		Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
		try {
			if (e.getReaction().getReactionEmote().getName().equals("⬅") && msg.getEmbeds().get(0).getAuthor().getName().equals("Clan List")) { //left
				int max = Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText().split("/")[0]) - 10;
				if (max < 10) {
					max = 10;
				}
				EmbedBuilder eb = new EmbedBuilder().setAuthor("Clan List", null, "http://icons.iconarchive.com/icons/google/noto-emoji-objects/256/62963-crossed-swords-icon.png")
						.setColor(Color.blue);
				try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
 					ResultSet rs = con.prepareStatement("SELECT * FROM Clans ORDER BY Bank DESC, LENGTH(MEMBERS) DESC").executeQuery();
 					
 					int counter = max - 10;
 					int total = 0;
 					while (rs.next()) {
 						if (counter < max && total < max) {
 							int pos = counter + 1;
	 						eb.addField("#"+pos+" "+rs.getString("Name"), "**Members:** `"+new DecimalFormat("#,###").format(Math.subtractExact(rs.getString("Members").split(",").length , 1))+"`"
	 								+ "\n**Bank:** `$"+new DecimalFormat("#,###").format(rs.getLong("Bank"))+"`", false);
	 						counter++;
 						}
 						total++;
 					}
 					/*if (max - 10 >= total) {
 						return;
 					}*/
 					eb.setFooter(max+"/"+total, null);
 					msg.editMessage(eb.build()).queue();
				} catch (SQLException e1) {e1.printStackTrace();}
			}
		} catch (NullPointerException e1) {}
		try {
			if (e.getReaction().getReactionEmote().getName().equals("➡") && msg.getEmbeds().get(0).getAuthor().getName().equals("Clan List")) { //right
				int max = Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText().split("/")[0]) + 10;
				if (max < 10) {
					max = 10;
				}
				EmbedBuilder eb = new EmbedBuilder().setAuthor("Clan List", null, "http://icons.iconarchive.com/icons/google/noto-emoji-objects/256/62963-crossed-swords-icon.png")
						.setColor(Color.blue);
				try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
 					ResultSet rs = con.prepareStatement("SELECT * FROM Clans ORDER BY Bank DESC, LENGTH(MEMBERS) DESC").executeQuery();
 					
 					int counter = max - 10;
 					int min = counter;
 					int total = 0;
 					while (rs.next()) {
 						if (counter < max && total >= min) {
 							int pos = counter + 1;
	 						eb.addField("#"+pos+" **"+rs.getString("Name")+"**", "**Members:** `"+new DecimalFormat("#,###").format(Math.subtractExact(rs.getString("Members").split(",").length , 1))+"`"
	 								+ "\n**Bank:** `$"+new DecimalFormat("#,###").format(rs.getLong("Bank"))+"`", false);
	 						counter++;
 						}
 						total++;
 					}
 					if (max - 10 >= total) {
 						return;
 					}
 					eb.setFooter(max+"/"+total, null);
 					msg.editMessage(eb.build()).queue();
				} catch (SQLException e1) {e1.printStackTrace();}
			}
		} catch (NullPointerException e1) {}
		
		
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		
		if (e.getUser().isBot() || e.getUser().isFake()) {
			return;
		}
		Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
		try {
			if (e.getReaction().getReactionEmote().getName().equals("⬅") && msg.getEmbeds().get(0).getAuthor().getName().equals("Clan List")) { //left
				int max = Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText().split("/")[0]) - 10;
				if (max < 10) {
					max = 10;
				}
				EmbedBuilder eb = new EmbedBuilder().setAuthor("Clan List", null, "http://icons.iconarchive.com/icons/google/noto-emoji-objects/256/62963-crossed-swords-icon.png")
						.setColor(Color.blue);
				try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
 					ResultSet rs = con.prepareStatement("SELECT * FROM Clans ORDER BY Bank DESC, LENGTH(MEMBERS) DESC").executeQuery();
 					
 					int counter = max - 10;
 					int total = 0;
 					while (rs.next()) {
 						if (counter < max && total < max) {
 							int pos = counter + 1;
	 						eb.addField("#"+pos+" "+rs.getString("Name"), "**Members:** `"+new DecimalFormat("#,###").format(Math.subtractExact(rs.getString("Members").split(",").length , 1))+"`"
	 								+ "\n**Bank:** `$"+new DecimalFormat("#,###").format(rs.getLong("Bank"))+"`", false);
	 						counter++;
 						}
 						total++;
 					}
 					/*if (max - 10 >= total) {
 						return;
 					}*/
 					eb.setFooter(max+"/"+total, null);
 					msg.editMessage(eb.build()).queue();
				} catch (SQLException e1) {e1.printStackTrace();}
			}
		} catch (NullPointerException e1) {}
		try {
			if (e.getReaction().getReactionEmote().getName().equals("➡") && msg.getEmbeds().get(0).getAuthor().getName().equals("Clan List")) { //right
				int max = Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText().split("/")[0]) + 10;
				if (max < 10) {
					max = 10;
				}
				EmbedBuilder eb = new EmbedBuilder().setAuthor("Clan List", null, "http://icons.iconarchive.com/icons/google/noto-emoji-objects/256/62963-crossed-swords-icon.png")
						.setColor(Color.blue);
				try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
 					ResultSet rs = con.prepareStatement("SELECT * FROM Clans ORDER BY Bank DESC, LENGTH(MEMBERS) DESC").executeQuery();
 					
 					int counter = max - 10;
 					int min = counter;
 					int total = 0;
 					while (rs.next()) {
 						if (counter < max && total >= min) {
 							int pos = counter + 1;
	 						eb.addField("#"+pos+" **"+rs.getString("Name")+"**", "**Members:** `"+new DecimalFormat("#,###").format(Math.subtractExact(rs.getString("Members").split(",").length , 1))+"`"
	 								+ "\n**Bank:** `$"+new DecimalFormat("#,###").format(rs.getLong("Bank"))+"`", false);
	 						counter++;
 						}
 						total++;
 					}
 					if (max - 10 >= total) {
 						return;
 					}
 					eb.setFooter(max+"/"+total, null);
 					msg.editMessage(eb.build()).queue();
				} catch (SQLException e1) {e1.printStackTrace();}
			}
		} catch (NullPointerException e1) {}
	}

}
