package me.darth.darthbot.commands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Clans extends ListenerAdapter {
	
	final static int season = 1;
	
	public static MessageEmbed viewClan(String clanID, User bot) {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
			while (rs.next()) {
				try {
					if (rs.getString("Name").equalsIgnoreCase(clanID) || rs.getInt("ID") == Integer.parseInt(clanID)) {
						EmbedBuilder eb = new EmbedBuilder().setAuthor("["+rs.getInt("ID")+"] "+rs.getString("Name")+"   ~   $"+rs.getLong("Bank")+" BANK", null, bot.getEffectiveAvatarUrl()).setColor(Color.red);
						eb.setFooter("Clans ~ Season "+season+" | !clan help", null);
						User founder = me.darth.darthbot.main.Main.sm.getUserById(rs.getLong("Founder"));
						String members = "";
						String[] memberssplit = rs.getString("Members").split(",");
						for (int x = 0 ; x < memberssplit.length ; x++) {
							try {
								User m = me.darth.darthbot.main.Main.sm.getUserById(memberssplit[x]);
								members = members+"\n"+m.getAsMention();
							} catch (IllegalArgumentException e1) {}
						}
						eb.addField("Founder", founder.getAsMention(), true);
						eb.addField("Members "+Math.subtractExact(memberssplit.length, 1)+"/25", members, true);
						eb.addField("Join Status", rs.getString("Status"), true);
						if (rs.getString("Description") != null) {
							eb.setDescription(rs.getString("Description"));
						}
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
		if (args[0].equalsIgnoreCase("!clan") || args[0].equalsIgnoreCase("!clans")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				EmbedBuilder help = new EmbedBuilder().setAuthor("Clans Commands", null, "http://icons.iconarchive.com/icons/google/noto-emoji-objects/256/62963-crossed-swords-icon.png")
						.setColor(Color.blue);
				help.setDescription("**General Commands**\n"
						+ "`!clan create <Name>` Create a clan\n"
						+ "`!clan deposit <Amount>` Deposit an amount to your Clan Bank\n"
						+ "`!clan withdraw <Amount>` Withdraw an amount to your Clan Bank\n"
						+ "`!clans` View all clans\n"
						+ "\n**Management**\n"
						+ "`!clan open` Opens the clan to the public\n"
						+ "`!clan close` Closes the clan (invite only\n"
						+ "`!clan invite <User>` Invites a user to the Clan\n"
						+ "`!clan kick <User>` Kicks a user from the Clan");
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
							if (invites.contains(","+m.getUser().getId())) {
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
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					
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
				} else if (args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("commands")) {
					e.getChannel().sendMessage(help.build()).queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("open")) {
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
				} else if (args[1].equalsIgnoreCase("create")) {
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
 					e.getChannel().sendMessage(viewClan(e.getMessage().getContentRaw().replace(args[0]+" ", ""), e.getJDA().getSelfUser())).queue();
 				} else if (args[0].equalsIgnoreCase("!clans") && args.length == 1) {
 					
				} else {
 					e.getChannel().sendMessage(help.build()).queue();
 				}
				
			} catch (SQLException e1) {e1.printStackTrace();}
		}
		
	}

}
