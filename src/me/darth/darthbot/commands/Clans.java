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
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.Route.Emotes;

public class Clans extends ListenerAdapter {
	
	final static int season = 3;
	
	public static int stoneunlock = 0;
	public static int coalunlock = 500;
	public static int ironunlock = 2000;
	public static int goldunlock = 5000;
	public static int diamondunlock = 10000;
	
	public static int size(int level, String type) {
		if (type.equalsIgnoreCase("BANK")) {
			if (level == 0) {
				return 250000;
			} else if (level == 1) {
				return 500000;
			} else if (level == 2) {
				return 1000000;
			} else if (level == 3) {
				return 2500000;
			} else if (level == 4) {
				return 5000000;
			} else if (level == 5) {
				return 10000000;
			}
		} else if (type.equalsIgnoreCase("SIZE")) {
			if (level == 0) {
				return 15;
			} else if (level == 1) {
				return 30;
			} else if (level == 2) {
				return 45;
			} else if (level == 3) {
				return 60;
			} else if (level == 4) {
				return 75;
			} else if (level == 5) {
				return 90;
			}
		}
		
		return -1;
	}
	
	public static int upgrade(int level, String type) {
		if (type.equalsIgnoreCase("BANK")) {
			if (level == 0) {
				return 100000;
			} else if (level == 1) {
				return 250000;
			} else if (level == 2) {
				return 500000;
			} else if (level == 3) {
				return 1000000;
			} else if (level == 4) {
				return 2500000;
			}
		} else if (type.equalsIgnoreCase("SIZE")) {
			if (level == 0) {
				return 50000;
			} else if (level == 1) {
				return 100000;
			} else if (level == 2) {
				return 250000;
			} else if (level == 3) {
				return 500000;
			} else if (level == 4) {
				return 1000000;
			}
		}
		
		return -1;
	}
	
	public MessageEmbed clanMine(Member m, int clanID) {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE ID = "+clanID);
			while (rs.next()) {
				Emote stone = me.darth.darthbot.main.Main.sm.getEmoteById("607340388230889482");
				Emote coal = me.darth.darthbot.main.Main.sm.getEmoteById("607340611929899018");
				Emote iron = me.darth.darthbot.main.Main.sm.getEmoteById("607340627885031434");
				Emote gold = me.darth.darthbot.main.Main.sm.getEmoteById("607340641529233409");
				Emote diamond = me.darth.darthbot.main.Main.sm.getEmoteById("607340668871770133");
				Emote autominer = me.darth.darthbot.main.Main.sm.getEmoteById("607558852728324097");
				int max = rs.getInt("Mine");
				EmbedBuilder eb = new EmbedBuilder().setAuthor(rs.getString("Name")+"'s Mine", null, "http://www.blocksandgold.com//media/catalog/product/cache/3/image/200x/6cffa5908a86143a54dc6ad9b8a7c38e/i/r/ironpickaxe_icon32.png").setColor(Color.black)
					.addField("Mine Depth", "`"+new DecimalFormat("#,###").format(max)+"` blocks!", true)
					.addField("To Mine", "Type `!clan work` to mine some resources!", true);
				if (max == 0) {
					max++;
				}
				if (rs.getString("Picture") != null) {
					eb.setAuthor(rs.getString("Name")+"'s Mine", null, rs.getString("Picture"));
				}
				String stoneemoji = ":no_entry:";
				String coalemoji = ":no_entry:";
				String ironemoji = ":no_entry:";
				String goldemoji = ":no_entry:";
				String diamondemoji = ":no_entry:";
				if (max >= stoneunlock) {
					stoneemoji = ":white_check_mark:";
					eb.setColor(Color.DARK_GRAY);
				}
				if (max >= coalunlock) {
					coalemoji = ":white_check_mark:";
					eb.setColor(Color.black);
				
				}
				if (max >= ironunlock) {
					ironemoji = ":white_check_mark:";
					eb.setColor(Color.gray);
				}
				if (max >= goldunlock) {
					goldemoji = ":white_check_mark:";
					eb.setColor(Color.yellow);
				}
				if (max >= diamondunlock) {
					diamondemoji = ":white_check_mark:";
					eb.setColor(85242255);
				}
				eb.addField("Materials", stone.getAsMention()+"Stone *at depth `"+new DecimalFormat("#,###").format(stoneunlock)+"`* "+stoneemoji+"\n"
						+ coal.getAsMention()+"Coal *at depth `"+new DecimalFormat("#,###").format(coalunlock)+"`* "+coalemoji+"\n"
								+ iron.getAsMention()+"Iron *at depth `"+new DecimalFormat("#,###").format(ironunlock)+"`* "+ironemoji+"\n"
										+ gold.getAsMention()+"Gold *at depth `"+new DecimalFormat("#,###").format(goldunlock)+"`* "+goldemoji+"\n"
												+ diamond.getAsMention()+"Diamond *at depth `"+new DecimalFormat("#,###").format(diamondunlock)+"`* "+diamondemoji, true);
				//eb.addField("Auto Miner", autominer.getAsMention()+"`0` blocks per hour\nUpgrade: `$0`", true);
				eb.addField("Auto Miner", autominer.getAsMention()+" ***Coming Soon!***", true); //coming soon
				String str = "";
				for (int x = 0 ; x < 60 ; x++) {
					int rand = new Random().nextInt(max);
					if (x % 15 == 0) {
						str=str+"\n";
					}
					if (rand >= stoneunlock && rand < coalunlock) {
						str=str+stone.getAsMention();
					} else if (rand >= coalunlock && rand < ironunlock) {
						str=str+coal.getAsMention();
					} else if (rand >= ironunlock && rand < goldunlock) {
						str = str+iron.getAsMention();
					} else if (rand >= goldunlock && rand < diamondunlock) {
						str = str+gold.getAsMention();
					} else if (rand >= diamondunlock) {
						str = str+diamond.getAsMention();
					}
				}
				eb.setDescription(str);
				return eb.build();
			}
		} catch (SQLException e1) {}
		return null;
	}
	
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
								} catch (IllegalArgumentException | NullPointerException e1) {}
							}
						}
						//eb.addField("Founder", founder.getAsMention(), true);
						eb.addField("Members "+Math.subtractExact(memberssplit.length, 1)+"/"+size(rs.getInt("SizeLevel"), "SIZE"), members, true);
						if (officers.equals("")) {
							eb.addField("Officers", "*No Officers*", true);
						} else {
							eb.addField("Officers", officers, true);
						}
						eb.addField("Clan Stats", "Join Status: **"+rs.getString("Status")+"**\nMine Depth: `"+new DecimalFormat("#,###").format(rs.getInt("Mine"))+"`", true);
						String desc = "**Founder:** "+founder.getAsMention();
						if (rs.getString("Description") != null) {
							desc=desc+"\nDescription: "+rs.getString("Description");
						}
						eb.setDescription(desc);
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
        if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!clan") || args[0].equalsIgnoreCase("!clans") && args.length == 1 || args.length > 1 && args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("list")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				EmbedBuilder help = new EmbedBuilder().setAuthor("Clans Commands", null, "http://icons.iconarchive.com/icons/google/noto-emoji-objects/256/62963-crossed-swords-icon.png")
						.setColor(Color.blue);
				help.setDescription("**General Commands**\n"
						+ "`!clan` View the Clan that you're in\n"
						+ "`!clans` View all clans\n"
						+ "`!clan create <Name>` Create a clan `$100k`\n"
						+ "`!clan join <Name/ID>` Joins a clan\n"
						+ "`!clan leave` Leaves a clan\n"
						+ "`!clan deposit <Amount>` Deposit an amount to your Clan Bank\n"
						+ "`!clan withdraw <Amount>` Withdraw an amount to your Clan Bank `[OFFICER]`\n"
						+ ":new:`!clan mine (Clan)` View your own or other Clans mines\n"
						+ ":new:`!clan work` Work in your Clans mine\n"
						+ "\n**Management**\n"
						+ "`!clan open` Opens the clan to the public\n"
						+ "`!clan close` Closes the clan (invite only)\n"
						+ "`!clan invite <User>` Invites a user to the Clan\n"
						+ "`!clan promote <User>` Promotes a member to Officer\n"
						+ "`!clan demote <User>` Demotes a member from Officer\n"
						+ "`!clan kick <User>` Kicks a user from the Clan\n"
						+ "`!clan perks` View a list of your Clans perks\n"
						+ "`!clan rename <Name>` Change your Clans name\n"
						+ "`!clan disband` Delete the Clan you own\n"
						+ "`!clan upgrade <BANK/SIZE>` Upgrades an aspect of your Clan\n"
						+ "`!clan description <Description>` Set a description for the Clan `2+ MEMBERS`\n"
						+ "`!clan colour <Colour>` Set a colour for your Clan `5+ MEMBERS`\n"
						+ "`!clan avatar <URL>` Set an avatar for your Clan `10+ MEMBERS`");
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
					e.getChannel().sendMessage(":no_entry: You're not currently in a clan. To join one, use `!clan join <Clan>`!").queue();
					return;
				} else if (args[0].equalsIgnoreCase("!clans") && args.length > 1) {
	 				e.getChannel().sendMessage(":no_entry: The `!clans` command is just for viewing the Clan List! Did you mean `!clan`?").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("work")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs.next()) {
						if (rs.getString("Members").contains(","+e.getAuthor().getIdLong())) {
							ResultSet profile = con.prepareStatement("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getId()).executeQuery();
							while (profile.next()) {
								if (profile.getLong("Mine") != 0L) {
									Calendar cal = Calendar.getInstance();
									cal.setTimeInMillis(profile.getLong("Mine"));
									cal.add(Calendar.MINUTE, 1);
									if (cal.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
										long secs = ChronoUnit.SECONDS.between(Calendar.getInstance().toInstant(), cal.toInstant());
										e.getChannel().sendMessage("You're tired after that hard work and must rest! You're able to mine again in **"+secs+"** seconds!").queue();
										return;
									}
								}	
							}
							Emote stone = me.darth.darthbot.main.Main.sm.getEmoteById("607340388230889482");
							Emote coal = me.darth.darthbot.main.Main.sm.getEmoteById("607340611929899018");
							Emote iron = me.darth.darthbot.main.Main.sm.getEmoteById("607340627885031434");
							Emote gold = me.darth.darthbot.main.Main.sm.getEmoteById("607340641529233409");
							Emote diamond = me.darth.darthbot.main.Main.sm.getEmoteById("607340668871770133");
							Emote autominer = me.darth.darthbot.main.Main.sm.getEmoteById("607558852728324097");
							Emote pickaxe = me.darth.darthbot.main.Main.sm.getEmoteById("607670699250942001");
							EmbedBuilder eb = new EmbedBuilder().setAuthor("Working in the Mine", null, "http://www.blocksandgold.com//media/catalog/product/cache/3/image/200x/6cffa5908a86143a54dc6ad9b8a7c38e/i/r/ironpickaxe_icon32.png");
							eb.setColor(Color.yellow).setFooter("Type | !clan mine | to view an overview of your Clan mine", null);
							eb.setDescription(pickaxe.getAsMention()+"You enter the mine and begin digging...");
							if (rs.getString("Picture") != null) {
								eb.setAuthor(rs.getString("Name")+"'s Mine", null, rs.getString("Picture"));
							}
							Message msg = e.getChannel().sendMessage(eb.build()).complete();
							int max = rs.getInt("Mine");
							String str = "";
							int stonec = 0;
							int coalc = 0;
							int ironc = 0;
							int goldc = 0;
							int diamondc = 0;
							int earnings = 0;
							for (int x = 0 ; x < 10 ; x++) {
								if (max == 0) {
									max++;
								}
								int rand = new Random().nextInt(max);
								if (rand >= stoneunlock && rand < coalunlock) {
									stonec++;
									earnings=earnings+1;
								} else if (rand >= coalunlock && rand < ironunlock) {
									coalc++;
									earnings=earnings+2;
								} else if (rand >= ironunlock && rand < goldunlock) {
									ironc++;
									earnings=earnings+5;
								} else if (rand >= goldunlock && rand < diamondunlock) {
									goldc++;
									earnings=earnings+10;
								} else if (rand >= diamondunlock) {
									diamondc++;
									earnings=earnings+20;
								}
							}
							long clanbal = rs.getLong("Bank") + earnings;
							eb.addField("You Return With:", stone.getAsMention()+"Stone: **x"+stonec+"**\n"
									+ coal.getAsMention()+"Coal: **x"+coalc+"**\n"
											+ iron.getAsMention()+"Iron: **x"+ironc+"**\n"
													+ gold.getAsMention()+"Gold: **x"+goldc+"**\n"
														+ diamond.getAsMention()+"Diamond: **x"+diamondc+"**\n"
																+ "**Total Earnings: `$"+earnings+"`**\n**New Clan Balance: `$"
															+new DecimalFormat("#,###").format(clanbal)+"`**", true);
							if (clanbal > size(rs.getInt("BankLevel"), "BANK")) {
								eb.addField("❗Bank Full❗", "Money couldn't be deposited into your clan bank as it's full! Consider upgrading it through `!clan perks`", true);
							} else {
								con.prepareStatement("UPDATE Clans SET Bank = "+clanbal+" WHERE ID = "+rs.getInt("ID")).execute();
							}
							con.prepareStatement("UPDATE profiles SET Mine = "+Calendar.getInstance().getTimeInMillis()+" WHERE UserID = "+e.getAuthor().getId()).execute();
							con.prepareStatement("UPDATE Clans SET Mine = Mine + 1 WHERE ID = "+rs.getInt("ID")).execute();
							ScheduledExecutorService executorService
						      = Executors.newSingleThreadScheduledExecutor();
							ScheduledFuture<?> scheduledFuture = executorService.schedule(() -> {
								msg.editMessage(eb.build()).queue();
						    }, 5, TimeUnit.SECONDS);
							return;
						}
					}
					e.getChannel().sendMessage(":no_entry: You are not in a Clan!").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("mine")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					if (args.length < 3) {
						while (rs.next()) {
							if (rs.getString("Members").contains(","+e.getAuthor().getId())) {
								e.getChannel().sendMessage(clanMine(e.getMember(), rs.getInt("ID"))).queue();
								return;
							}
						}
					} else {
						while (rs.next()) {
							String clanID = e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", "");
							try {
								if (rs.getString("Name").equalsIgnoreCase(clanID) || rs.getInt("ID") == Integer.parseInt(clanID)) {
									e.getChannel().sendMessage(clanMine(e.getMember(), rs.getInt("ID"))).queue();
									return;
								}
							} catch (NumberFormatException e1) {}
						}
						e.getChannel().sendMessage(":no_entry: That Clan couldn't be found! Please ensure the name/ID is exact!").queue();
						return;
					}
					e.getChannel().sendMessage(":no_entry: You are not in a Clan!").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].toLowerCase().contains("perk")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs.next()) {
						if (rs.getString("Members").contains(","+e.getAuthor().getId())) {
							String bankstring = "";
							String sizestring = "";
							int banksize = rs.getInt("BankLevel");
							int clansize = rs.getInt("SizeLevel");
							String bankupgrade = null;
							if (upgrade(rs.getInt("BankLevel"), "BANK") == -1) {
								bankupgrade = "MAX";
							} else {
								bankupgrade = "$"+new DecimalFormat("#,###").format(upgrade(rs.getInt("BankLevel"), "BANK"));
							}
							String sizeupgrade = null;
							if (upgrade(rs.getInt("SizeLevel"), "SIZE") == -1) {
								sizeupgrade = "MAX";
							} else {
								sizeupgrade = "$"+new DecimalFormat("#,###").format(upgrade(rs.getInt("SizeLevel"), "SIZE"));
							}
							for (int x = 0 ; x < 5 ; x++) { 
								if (banksize > 0) {
									bankstring=bankstring+":white_small_square:";
									banksize--;
								} else {
									bankstring=bankstring+":black_small_square:";
								}
							}
							for (int x = 0 ; x < 5 ; x++) { 
								if (clansize > 0) {
									sizestring=sizestring+":white_small_square:";
									clansize--;
								} else {
									sizestring=sizestring+":black_small_square:";
								}
							}
							
							EmbedBuilder eb = new EmbedBuilder().setAuthor("Clan Perks", null, "http://icons.iconarchive.com/icons/google/noto-emoji-objects/256/62963-crossed-swords-icon.png");
							eb.setDescription("Bank Capacity: `$"+new DecimalFormat("#,###").format(size(rs.getInt("BankLevel"), "BANK"))+"` "+bankstring+" `UPGRADE: "+bankupgrade+"`\n"
									+ "Clan Size: `"+new DecimalFormat("#,###").format(size(rs.getInt("SizeLevel"), "SIZE"))+"` "+sizestring+" ` UPGRADE: "+sizeupgrade+"`").setColor(Color.blue);
							if (rs.getString("Picture") != null) {
								eb.setAuthor("Clan Perks", null, rs.getString("Picture"));
							}
							int members = rs.getString("Members").split(",").length - 1;
							String desc = "🔒";
							String colour = "🔒";
							String picture = "🔒";
							if (members >= 2) {
								desc = "🔓`!clan desc`";
							} if (members >= 5) {
								colour = "🔓`!clan colour`";
							} if (members >= 10) {
								picture = "🔓`!clan avatar`";
							}
							eb.addField("Cosmetics", "**Clan Description** `2 Members` "+desc
									+ "\n**Clan Colour** `5 Members` "+colour
									+ "\n**Clan Avatar** `10 Members` "+picture, true);
							eb.setFooter("Clans Season "+season+" | !clan upgrade <BANK / SIZE>", null);
							e.getChannel().sendMessage(eb.build()).queue();
						}
					}
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("rename")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					while (rs.next()) {
						if (args.length < 3) {
							e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!clan rename <Name>`").queue();
							return;
						}
						String name = e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", "");
						ResultSet clans = con.prepareStatement("SELECT * FROM Clans").executeQuery();
						while (clans.next()) {
							if (clans.getString("Name").replace(" ", "").toLowerCase().equalsIgnoreCase(name.toLowerCase().replace(" ", ""))) {
								e.getChannel().sendMessage(":no_entry: A Clan already exists with that name!").queue();
								return;
							}
						}
						PreparedStatement ps = con.prepareStatement("UPDATE Clans SET Name = ? WHERE ID = "+rs.getInt("ID"));
						ps.setString(1, name);
						ps.execute();
						e.getChannel().sendMessage(":white_check_mark: Successfully renamed Clan!").queue();
					}
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("disband")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getAuthor().getId());
					while (rs.next()) {
						if (args.length < 2) {
							e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!clan disband``").queue();
							return;
						}
						e.getChannel().sendMessage(":exclamation: Disbanding a clan is **irreversible**, and once done, **cannot be recovered**. React to your message with a :white_check_mark: to confirm the action.\n"+e.getMember().getAsMention()).queue();
						e.getMessage().addReaction("✅").queue();
					}
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("upgrade")) {
					ResultSet user = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getId());
					long bal = -1L;
					while (user.next()) {
						bal = user.getLong("DBux");
					}
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs.next()) {
						if (rs.getString("Members").contains(","+e.getAuthor().getId())) { 
							if (args.length < 3) {
								e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!clan upgrade <BANK / SIZE>").queue();
							}
							if (args[2].equalsIgnoreCase("BANK")) {
								int banklevel = rs.getInt("BankLevel");
								if (banklevel >= 5) {
									e.getChannel().sendMessage(":no_entry: This perk is at the maximum level!").queue();
									return;
								}
								int price = upgrade(banklevel, "BANK");
								if (price > bal) {
									e.getChannel().sendMessage(":no_entry: You do not have enough in your personal balance to afford this upgrade!").queue();
								} else {
									con.prepareStatement("UPDATE profiles SET DBux = DBux - "+price+" WHERE UserID = "+e.getAuthor().getId()).execute();
									con.prepareStatement("UPDATE Clans SET BankLevel = BankLevel + 1 WHERE ID = "+rs.getInt("ID")).execute();
									banklevel++;
									e.getChannel().sendMessage(":white_check_mark: Successfully upgraded Clan bank to **Level "+banklevel+"**!").queue();
								}
							} else if (args[2].equalsIgnoreCase("SIZE")) {
								int sizelevel = rs.getInt("SizeLevel");
								if (sizelevel >= 5) {
									e.getChannel().sendMessage(":no_entry: This perk is at the maximum level!").queue();
									return;
								}
								int price = upgrade(sizelevel, "SIZE");
								if (price > bal) {
									e.getChannel().sendMessage(":no_entry: You do not have enough in your personal balance to afford this upgrade!").queue();
								} else {
									con.prepareStatement("UPDATE profiles SET DBux = DBux - "+price+" WHERE UserID = "+e.getAuthor().getId()).execute();
									con.prepareStatement("UPDATE Clans SET SizeLevel = SizeLevel + 1 WHERE ID = "+rs.getInt("ID")).execute();
									sizelevel++;
									e.getChannel().sendMessage(":white_check_mark: Successfully upgraded Clan size to **Level "+sizelevel+"**!").queue();
								}
							} else {
								e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!clan upgrade <BANK / SIZE>`").queue();
							}
							
						}
						return;
					}
					e.getChannel().sendMessage(":no_entry: You are not in a Clan!").queue();
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
								Field f = Color.class.getField(args[2].toUpperCase());
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
							e.getChannel().sendMessage(":no_entry: Invalid Syntax `!clan avatar <Image URL>`").queue();
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
								
								String officers = rs.getString("Officers");
								officers = officers.replace(","+m.getUser().getIdLong(), "");
								con.prepareStatement("UPDATE Clans SET Officers = '"+officers+"' WHERE ID = "+rs.getInt("ID")).execute();
								e.getChannel().sendMessage(":white_check_mark: Successfully kicked user!").queue();
								return;
							}
						}
					}
					e.getChannel().sendMessage(":no_entry: You do not own a clan!").queue();
				} else if (args[0].equalsIgnoreCase("!clan") && args[1].equalsIgnoreCase("join")) {
					long aicf = 0L;
					ResultSet rs2 = con.createStatement().executeQuery("SELECT * FROM Clans");
					while (rs2.next()) {
						if (rs2.getString("Members").contains(","+e.getAuthor().getId())) {
							e.getChannel().sendMessage(":no_entry: You are already in a Clan!").queue();
							return;
						}
						if (rs2.getLong("Founder") == e.getAuthor().getIdLong()) {
							e.getChannel().sendMessage(":no_entry: You cannot join a Clan while owning one!").queue();
							aicf=rs2.getLong("Founder");
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
								if (aicf != 0L && aicf != e.getAuthor().getIdLong()) {
									e.getChannel().sendMessage(":no_entry: You cannot join a Clan while owning one!").queue();
									return;
								} else if (rs.getString("Status").equals("CLOSED") && !invites.contains(","+e.getAuthor().getId()) && rs.getLong("Founder") != e.getAuthor().getIdLong()) {
									e.getChannel().sendMessage(":no_entry: That clan is invite only!").queue();
									return;
								} else {
									String members = rs.getString("Members");
									if (Math.subtractExact(members.split(",").length, 1) >= size(rs.getInt("SizeLevel"), "SIZE")) {
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
							con.prepareStatement("UPDATE Clans SET Members = '"+members+"' WHERE ID = "+rs.getInt("ID")).execute();
							
							String officers = rs.getString("Officers");
							officers = officers.replace(","+e.getAuthor().getIdLong(), "");
							con.prepareStatement("UPDATE Clans SET Officers = '"+officers+"' WHERE ID = "+rs.getInt("ID")).execute();
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
							if (args.length < 3) {
								e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!clan deposit <Amount>`").queue();
								return;
							}
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
								} else if (rs.getLong("Bank") + amount > size(rs.getInt("BankLevel"), "BANK")) {
									e.getChannel().sendMessage(":no_entry: Your Clan bank isn't big enough to hold that much money!\n*Upgrade it using `!clan upgrade BANK`*").queue();
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
			if (e.getReaction().getReactionEmote().getName().equals("✅") && msg.getContentRaw().split(" ")[0].equalsIgnoreCase("!clan") && msg.getContentRaw().split(" ")[1].equalsIgnoreCase("disband")) {
				if (msg.getAuthor().getIdLong() == e.getUser().getIdLong()) {
					try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
						ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Clans WHERE Founder = "+e.getUser().getId());
						while (rs.next()) {
							con.prepareStatement("DELETE FROM Clans WHERE Founder = "+e.getUser().getId()+" AND ID = "+rs.getInt("ID")).execute();
							e.getChannel().sendMessage(":white_check_mark: Successfully disbanded Clan!").queue();
						}
					} catch (SQLException e1) {e1.printStackTrace();}
				} else {
					e.getTextChannel().removeReactionById(e.getMessageId(), e.getReaction().getReactionEmote().getName(), e.getMember().getUser()).queue();
				}
			}
		} catch (Exception e1) {}
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
 						if (counter < max && total < max && total >= counter) {
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
	 						eb.addField("#"+pos+" "+rs.getString("Name"), "**Members:** `"+new DecimalFormat("#,###").format(Math.subtractExact(rs.getString("Members").split(",").length , 1))+"`"
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
 						if (counter < max && total < max && total >= counter) {
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
	 						eb.addField("#"+pos+" "+rs.getString("Name"), "**Members:** `"+new DecimalFormat("#,###").format(Math.subtractExact(rs.getString("Members").split(",").length , 1))+"`"
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
