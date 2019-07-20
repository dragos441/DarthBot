package me.darth.darthbot.commands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CustomStores extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!cs") || args[0].equalsIgnoreCase("!customstore") || args[0].equalsIgnoreCase("!customstores")) {
			EmbedBuilder eb = new EmbedBuilder().setAuthor("Custom Stores", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl())
					.setDescription("`!customstores` Views all custom store commands\n"
							+ "`!store <User>` View a users store\n"
							+ "`!customstore add <Item> | Description | Price` Adds an item to your custom store\n"
							+ "`!customstore remove <Item ID>` Removes an item from your custom store\n"
							+ "`!shop` View the DarthBot store where you can buy virtual items!")
					.setFooter("The bot will try to DM if one of your items is purchased. If your DMs are disabled, it will tag you in the channel", "https://www.freepngimg.com/download/light/78156-compact-lightbulb-electric-light-lamp-lighting-incandescent.png")
					.setColor(Color.blue);
			if (args.length == 1 && !args[0].equalsIgnoreCase("!store")) {
				e.getChannel().sendMessage(eb.build()).queue();
				return;
			} else if (!args[0].equalsIgnoreCase("!store") && args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")) {
				try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
					int counter = 0;
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM StoreItems WHERE SellerID = "+e.getAuthor().getId());
					while (rs.next()) {
						counter++;
					}
					if (counter >= 10) {
						e.getChannel().sendMessage(":no_entry: Your store is full! Remove some items from your store using `!customstore remove <Item ID>`!").queue();
						return;
					}
					String name = null;
					String desc = null;
					long price = -1L;
					try {
						if (e.getMessage().getContentRaw().contains("|")) {
								String[] split = e.getMessage().getContentRaw().split("\\|");
								name = split[0].replace(args[0]+" "+args[1]+" ", "");
								desc = split[1];
								price = Long.parseLong(split[2].replace("$", "").replace(" ", ""));
						} else {
							price = -1L;
						}
					} catch (ArrayIndexOutOfBoundsException | NumberFormatException e1) {
						e.getChannel().sendMessage(":no_entry: Invalid Syntax `!customstore add Item | Description | Price`\nEg: *!customstore add Drawing* **|** *I will make you a drawing of your avatar* **|** *10000*").queue();
						return;
					}
					if (price == -1L) {
						e.getChannel().sendMessage(":no_entry: Invalid Syntax `!customstore add Item | Description | Price`\nEg: *!customstore add Drawing* **|** *I will make you a drawing of your avatar* **|** *10000*").queue();
						return;
					}
					if (price <= 0) {
						e.getChannel().sendMessage(":no_entry: Your item must cost something!").queue();
						return;
					}
					con.prepareStatement("INSERT INTO StoreItems (SellerID, Name, Description, Price) values ("+e.getAuthor().getId()+", '"+name+"', '"+desc+"', "+price+")").execute();
					e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.green).setDescription("Successfully added **"+name+"** to your store!").build()).queue();
				} catch (SQLException e1) {e1.printStackTrace();}
				
			} else if (!args[0].equalsIgnoreCase("!store") && args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")) {
				if (args.length < 3) {
					e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!customstore remove <Item ID>`").queue();
					return;
				}
				try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
					ResultSet rs = con.createStatement().executeQuery("SELECT * FROM StoreItems WHERE SellerID = "+e.getAuthor().getId());
					while (rs.next()) {
						try {
							if (rs.getInt("ID") == Integer.parseInt(args[2])) {
								con.prepareStatement("DELETE FROM StoreItems WHERE ID = "+args[2]).execute();
								e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.red).setDescription("Successfully removed **"+rs.getString("Name")+"** from your store!").build()).queue();
								return;
							}
						} catch (NumberFormatException e1) {
							e.getChannel().sendMessage(":no_entry: Invalid Syntax: `!customstore remove <Item ID>").queue();
							return;
						}
					}
					e.getChannel().sendMessage(":no_entry: Item not found! Make sure the ID is exact, and the item is listed by you!").queue();
				} catch (SQLException e1) {e1.printStackTrace();}
				
			} else {
				e.getChannel().sendMessage(eb.build()).queue();
			}
		}
		if (args[0].equalsIgnoreCase("!store")) {
			Member target = null;
			if (args.length == 1) {
				target = e.getMember();
			} else if (!e.getMessage().getMentionedMembers().isEmpty()) {
				target = e.getMessage().getMentionedMembers().get(0);
			} else {
				target = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" ", ""), e.getGuild());
			}
			
			if (target == null) {
				e.getChannel().sendMessage(":no_entry: Could not find that users store!").queue();
				return;
			}
			
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet rs = con.createStatement().executeQuery("SELECT * FROM StoreItems WHERE SellerID = "+target.getUser().getId());
				EmbedBuilder eb = new EmbedBuilder().setAuthor(target.getEffectiveName()+"'s Store", null, target.getUser().getEffectiveAvatarUrl()).setColor(Color.green);
				while (rs.next()) {
					eb.addField("`"+rs.getInt("ID")+"` "+rs.getString("Name")+" [$"+new DecimalFormat("#,###").format(rs.getLong("Price"))+"]", rs.getString("Description"), false);
				}
				if (target.equals(e.getMember())) {
					eb.setFooter("You can add items to your store through the !customstore command!", "https://www.freepngimg.com/download/light/78156-compact-lightbulb-electric-light-lamp-lighting-incandescent.png");
				} else if (!eb.getFields().isEmpty()){
					eb.setFooter("DarthBot Staff won't compensate for scamming/accidental purchase! Verify the Item ID and only purchase items from users you trust! Only DarthBot sells items that you can use on the bot!", "https://static.thenounproject.com/png/18974-200.png");
				} else {
					eb.setDescription(":shrug: "+target.getAsMention()+"'s store is empty! They can add some items by using `!cs`");
				}
				e.getChannel().sendMessage(eb.build()).queue();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}

}
