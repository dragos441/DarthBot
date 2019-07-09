package me.darth.darthbot.commands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.css.Counter;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


public class Store extends ListenerAdapter {
	
	public static int publicIds = 8;
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!buy") || args[0].equalsIgnoreCase("!purchase")) {
			if (args.length < 2) {
				e.getChannel().sendMessage(":no_entry: Incorrect Syntax `!buy <Item ID / Item Name>`\n*Use !store to view a list of items!*").queue();
				return;
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet rs = con.createStatement().executeQuery("SELECT * FROM StoreItems");
				boolean found = false;
				while (rs.next()) {
					
					try {
						if (e.getMessage().getContentRaw().replace(args[0]+" ", "").equalsIgnoreCase(rs.getString("Name").replace(rs.getString("Name").split(" ")[0]+" ", ""))) {
							found=true;
							ResultSet profile = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getId());
							while (profile.next()) {
								if (profile.getString("Inventory") != null && profile.getString("Inventory").split(",").length > 10) {
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
									con.prepareStatement("UPDATE profiles SET DBux = "+newbux+", Inventory = '"+newinv+"' WHERE UserID = "+e.getAuthor().getId()).execute();
								}
								e.getChannel().sendMessage("Successfully purchased **"+rs.getString("Name")+"**").queue();
							}
						}
						if (Integer.parseInt(args[1]) == rs.getInt("ID")) {
							found=true;
							ResultSet profile = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getId());
							while (profile.next()) {
								if (profile.getString("Inventory") != null && profile.getString("Inventory").split(",").length > 10) {
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
									con.prepareStatement("UPDATE profiles SET DBux = "+newbux+", Inventory = '"+newinv+"' WHERE UserID = "+e.getAuthor().getId()).execute();
								} else {
									con.prepareStatement("UPDATE profiles SET Inventory = '"+newinv+"' WHERE UserID = "+e.getAuthor().getId()).execute();
								}
								e.getChannel().sendMessage("Successfully purchased **"+rs.getString("Name")+"**").queue();
							}
						}
					} catch (NumberFormatException e1) {}
					
				}
				if (!found) {
					e.getChannel().sendMessage(":no_entry: Item not found! Make sure to use `!buy <ID>` or `!buy <Item Name>`").queue();
				}
			    con.close();
			
			} catch (SQLException e1) {
			    e.getChannel().sendMessage("<@159770472567799808> Error! ```"+e1+"```").queue();
			}
		}
		if (args[0].equalsIgnoreCase("!store") || args[0].equalsIgnoreCase("!store")) {
			EmbedBuilder eb = new EmbedBuilder().setAuthor("Store", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl()).setColor(Color.yellow)
					.setDescription("> **Buy items using `!buy <ID>` or `!buy <Name>`**\n"
							+ "> **Max Inventory Size** is **10 items**!\n"
							+ "> **Robbery Defense Items** have a **1/10** chance of being dropped after each time you're robbed!\n"
							+ "> **Your most powerful item** is **always used**!");
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
