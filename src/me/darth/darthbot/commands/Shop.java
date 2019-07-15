package me.darth.darthbot.commands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.css.Counter;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


public class Shop extends ListenerAdapter {
	
	public static int publicIds = 8;
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!buy") || args[0].equalsIgnoreCase("!purchase")) {
			if (!me.darth.darthbot.main.Main.economyEnabled) {
				e.getChannel().sendMessage(":no_entry: This section of the economy system is temporarily disabled due to an ongoing issue! Check the Official `!darthbot` Server for Updates!").queue();
				return;
			}
			if (args.length < 2) {
				e.getChannel().sendMessage(":no_entry: Incorrect Syntax `!buy <Item ID>`\n*Use !shop to view a list of items!*").queue();
				return;
			}
			
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet rs = con.createStatement().executeQuery("SELECT * FROM StoreItems WHERE ID = "+args[1]); 
				boolean found = false;
				while (rs.next()) {
					found=true;
					Member seller = null;
					ResultSet profile = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getId());
					while (profile.next()) {
						if (rs.getString("SellerID").equals("569461469154902016") && profile.getString("Inventory") != null && profile.getString("Inventory").split(",").length > 10) {
							e.getChannel().sendMessage(":no_entry: Your Inventory is Full!").queue();
							return;
						}
						long newbux = profile.getLong("DBux") - rs.getLong("Price");
						if (newbux < 0 && profile.getLong("DBux") != -1337) {
							e.getChannel().sendMessage(":no_entry: You don't have enough to purchase this item!").queue();
							return;
						}
						String newinv = profile.getString("Inventory");
						if (newinv == null) {
							newinv = ","+rs.getInt("ID");
						} else {
							newinv = profile.getString("Inventory")+","+rs.getInt("ID");
						}
						if (profile.getLong("DBux") != -1337) {
							if (rs.getString("SellerID").equals("569461469154902016")) {
								con.prepareStatement("UPDATE profiles SET DBux = "+newbux+", Inventory = '"+newinv+"' WHERE UserID = "+e.getAuthor().getId()).execute();
							} else if (!rs.getString("SellerID").equals("569461469154902016")) {
								con.prepareStatement("UPDATE profiles SET DBux = "+newbux+" WHERE UserID = "+e.getAuthor().getId()).execute();
								long sellerID = rs.getLong("SellerID");
								ResultSet sid = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+sellerID);
								while (sid.next()) {
									long sbux = sid.getLong("DBux") + rs.getLong("Price");
									con.prepareStatement("UPDATE profiles SET DBux = "+sbux+" WHERE UserID = "+sellerID).execute();
								}
								seller = e.getGuild().getMemberById(sellerID);
								EmbedBuilder eb = new EmbedBuilder().setAuthor("Item Purchased", null, e.getAuthor().getEffectiveAvatarUrl()).setColor(Color.green);
								eb.setDescription(e.getMember().getAsMention()+" purchased your item **"+rs.getString("Name")+"**! You have earned **$"+rs.getLong("Price")+"**!");
								seller.getUser().openPrivateChannel().queue((channel) ->
						        {
						        	Message message = null;
						        	
									try {
										message = channel.sendMessage(eb.build()).submit().get();
									} catch (InterruptedException e1) {} catch (ExecutionException e2) {}
									if (message == null) {
										eb.setFooter("Allow messages from Server Members to get these kind of messages DMed to you!", null);
										e.getChannel().sendMessage(eb.build()).queue();
									}
						        });
							}
						}
						e.getChannel().sendMessage(rs.getInt("ID")+" - "+rs.getString("Name")+" - "+rs.getLong("Price")).queue();
						e.getChannel().sendMessage("Successfully purchased **"+rs.getString("Name")+"**!").queue();
						EmbedBuilder eb = new EmbedBuilder().setAuthor("Item Purchased", null, e.getAuthor().getEffectiveAvatarUrl())
							.setDescription(e.getMember().getAsMention()+" purchased **"+rs.getString("Name")+"** from "+seller.getAsMention()+" for `$"+rs.getLong("Price")+"`!")
							.setColor(Color.green).setFooter(e.getGuild().toString(), e.getGuild().getIconUrl())
							.setTimestamp(Instant.from(ZonedDateTime.now()));
						me.darth.darthbot.main.Main.sm.getGuildById("545700502747349022").getTextChannelById("599265246049206284").sendMessage(eb.build()).queue();
					}
					
				}
				if (!found) {
					e.getChannel().sendMessage(":no_entry: Item not found! Make sure to use `!buy <ID>` or `!buy <Item Name>`").queue();
				}
			    con.close();
			
			} catch (SQLException e1) {e1.printStackTrace();}
			
		}
		if (args[0].equalsIgnoreCase("!shop") || args[0].equalsIgnoreCase("!shop")) {
			EmbedBuilder eb = new EmbedBuilder().setAuthor("DarthBot Shop", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl()).setColor(Color.yellow)
					.setDescription("> **Buy items using `!buy <ID>` or `!buy <Name>`**\n"
							+ "> **Max Inventory Size** is **10 items**!\n"
							+ "> **Robbery Defense Items** have a **1/10** chance of being dropped after each time you're robbed!\n"
							+ "> **Your most powerful item** is **always used**!")
			 		.setFooter("Looking for Custom user Stores? Try !store", "https://www.freepngimg.com/download/light/78156-compact-lightbulb-electric-light-lamp-lighting-incandescent.png");
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet rs = con.createStatement().executeQuery("SELECT * FROM StoreItems");
				int c = 1;
				while (rs.next() && c <= publicIds) {
					eb.addField("`"+rs.getInt("ID")+"` "+rs.getString("Name")+" [$"+new DecimalFormat("#,###").format(rs.getLong("Price"))+"]", rs.getString("Description"), false);
					c++;
					
				}
				e.getChannel().sendMessage(eb.build()).queue();
			    con.close();
			
			} catch (SQLException e1) {
			    e.getChannel().sendMessage("<@159770472567799808> Error! ```"+e1+"```").queue();
			}
		}
		if (args[0].equalsIgnoreCase("!inventory") || args[0].equalsIgnoreCase("!inv")) {
			Member target = null;
			try {
				if (!e.getMessage().getMentionedMembers().isEmpty()) {
					target = e.getMessage().getMentionedMembers().get(0);
				} else {
					target = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" ", ""), e.getGuild());
				}
			} catch (ArrayIndexOutOfBoundsException e1) {
				target = e.getMember();
			}
			if (args.length < 2) {
				target = e.getMember();
			}
			if (target == null) {
				e.getChannel().sendMessage(":no_entry: User not found!").queue();
				return;
			}
			EmbedBuilder eb = new EmbedBuilder().setAuthor(target.getEffectiveName()+"'s Inventory", null, target.getUser().getEffectiveAvatarUrl()).setColor(Color.yellow);
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+target.getUser().getId());
				while (rs.next()) {
					String invstring = "";
					String invraw = rs.getString("Inventory");
					if (invraw == null || invraw.replace(" ", "").isEmpty()) {
						eb.addField("Inventory", target.getEffectiveName()+"'s Inventory is Empty!", false);
					} else {
						String[] invsplit = invraw.split(",");
						for (int x = 0 ; x < invsplit.length ; x++) {
							if (!invsplit[x].isEmpty()) {
								ResultSet item = con.createStatement().executeQuery("SELECT * FROM StoreItems WHERE ID = "+invsplit[x]);
								while (item.next()) {
									invstring=invstring+"\n**"+item.getString("Name")+"**";
								}
							}
						}
					}
					eb.setDescription(invstring);
					e.getChannel().sendMessage(eb.build()).queue();
				}
			    con.close();
			
			} catch (SQLException e1) {
			    e1.printStackTrace();
			}
		}
	}

}
