package me.darth.darthbot.commands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class HigherLower extends ListenerAdapter {
	
	public static Map<Member, Long> betcooldown = new HashMap<>();	
	public static Map<Member, Long> highlowcooldown = new HashMap<>();	
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");

		if (args[0].equalsIgnoreCase("!hl") || args[0].equalsIgnoreCase("!higherlower") || args[0].equalsIgnoreCase("!highlow") || args[0].equalsIgnoreCase("!higherorlower")) {
			long tobet = -1;
			try {
				tobet = Long.parseLong(args[1]);
			} catch (ArrayIndexOutOfBoundsException e1) {
				e.getChannel().sendMessage("Invalid Syntax: `!hl <amount>`").queue();
				return;
			} catch (NumberFormatException e2) {
				e.getChannel().sendMessage("Invalid Syntax: `!hl <amount>`").queue();
				return;
			}
			try {
				long time = highlowcooldown.get(e.getMember());
				if (time > System.currentTimeMillis() && !e.getMember().getRoles().contains(e.getGuild().getRoleById("557702978455339009"))) {
					Date date = new Date(time);
					long s = (date.getTime() - System.currentTimeMillis()) / 1000;
					e.getChannel().sendMessage("Please wait **"+s+"** seconds before using `!higherlower` again!").queue();
					return;
				} else {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.SECOND, 10);
					highlowcooldown.put(e.getMember(), c.getTimeInMillis());
				}
			} catch (NullPointerException e1) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.SECOND, 10);
				highlowcooldown.put(e.getMember(), c.getTimeInMillis());
			} catch (IllegalStateException e2) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.SECOND, 10);
				betcooldown.put(e.getMember(), c.getTimeInMillis());
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet canBet = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "
    					+e.getAuthor().getIdLong());
				long bux = -1;
    			while (canBet.next()) {
    				bux = canBet.getLong("DBux");
    				if (tobet > bux && bux != -1337) {
    					if (bux > 0) {
    						e.getChannel().sendMessage("You don't have $"+tobet+" to bet! You can only bet a maximum of $"+bux).queue();
    					} else {
    						e.getChannel().sendMessage("You don't have any money to bet!").queue();
    					}
    					betcooldown.put(e.getMember(), System.currentTimeMillis());
    					return;
    				}
    				if (tobet < 10) {
    					e.getChannel().sendMessage("You must bet $10 or more!").queue();
    					betcooldown.put(e.getMember(), System.currentTimeMillis());
    					return;
    				}
    				if (bux != -1337) {
    					long newbux = bux - tobet;
    					con.prepareStatement("UPDATE profiles SET DBux = "+newbux+" WHERE UserID = "+e.getMember().getUser().getIdLong()).execute();
    				}
    				EmbedBuilder eb = new EmbedBuilder();
    				eb.setColor(Color.yellow);
    				eb.setAuthor("Casino ~ Higher or Lower", null, e.getAuthor().getEffectiveAvatarUrl());
    				eb.setDescription("__**Instructions**__\nReact with :arrow_up: for Upper\nReact with :arrow_down: for Lower"
    						+ "\nReact with :moneybag: to Cashout\n:newspaper: **Info** The minimum is 1, and the maximum is 100"
    						+ "\n:bulb: **Tip** You won't get any money unless you cashout, even if you lose!");
    				e.getChannel().sendMessage(eb.build()).queue();
    				eb.setFooter(e.getAuthor().getId(), null);
    				eb.setDescription("*Starting Bet: $*"+args[1]+"\n**Cashout Prize: $**"+args[1]+"");
    				int rand = new Random().nextInt(100);
    				rand++;
    				eb.addField("Higher or Lower: ", ""+rand, false);
    				Message msg = e.getChannel().sendMessage(eb.build()).complete();
    				msg.addReaction("⬆").queue();
    				msg.addReaction("⬇").queue();
    				msg.addReaction("💰").queue();
     			}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		}
		
		
		
	}
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			if (e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getAuthor().getName().equals("Casino ~ Higher or Lower")) {
				if (!e.getMember().equals(e.getGuild().getMemberById(e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getFooter().getText()))
					&& !e.getMember().getUser().isBot()) {
					e.getTextChannel().removeReactionById(e.getMessageId(), e.getReaction().getReactionEmote().getName(), e.getMember().getUser()).queue();
					con.close();
					return;
				}
				ResultSet canBet = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "
    					+e.getMember().getUser().getIdLong());
				long bux = -1;
    			while (canBet.next()) {
    				bux = canBet.getLong("DBux");
    			}
				if (e.getMember().getUser().isBot()) {
					con.close();
					return;
				}
				MessageEmbed oldeb = e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0);
				int num = oldeb.getFields().size() - 1;
				try {
					num = Integer.parseInt(e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getFields().get(num).getValue());
				} catch (IndexOutOfBoundsException e1) {con.close(); return;} catch (NumberFormatException e2) { con.close();return;}
				int rand = new Random().nextInt(101);
				/*if (bux > 10000) {
					if (rand < 4 && new Random().nextInt(10) <= 3) {
						rand++;
					} else if (rand > 6 && new Random().nextInt(10) >= 7) {
						rand--;
					}
				}*/
				EmbedBuilder eb = new EmbedBuilder(oldeb);
				eb.addField("Higher or Lower:", ""+rand, false);
				if (!e.getMember().getUser().isBot()) {
					//e.getChannel().sendMessage(newnum+" - newnum\n`"+e.getReactionEmote().getName()+"`").queue();
				}
				if (e.getReactionEmote().getName().equals("💰") || eb.getFields().size() >= 6) {
					if (eb.getFields().size() <= 2) {
						e.getTextChannel().removeReactionById(e.getMessageId(), e.getReaction().getReactionEmote().getName(), e.getMember().getUser()).queue();
						e.getChannel().sendMessage(e.getMember().getAsMention()+", You must have at least one guess before cashing out!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
						return;
					}
					int cashout = Integer.parseInt(oldeb.getDescription().split("\n")[1].replace("**Cashout Prize: $**", ""));
					if (cashout > 300 && eb.getFields().size() <= 4) {
						e.getChannel().sendMessage("You must have 3+ guesses to cash out prizes over $300!").queue();
						return;
					}
					long newbux = bux + cashout;
					if (bux != -1337) {
						con.prepareStatement("UPDATE profiles SET DBux = "+newbux+" WHERE UserID = "+e.getMember().getUser().getIdLong()).execute();
					}
					long winnings = cashout - Integer.parseInt(oldeb.getDescription().split("\n")[0].replace("*Starting Bet: $*", ""));
					eb.addField("Successfully Cashed Out $"+winnings, "New Balance: $"+newbux, false);
					eb.setColor(Color.green);
					e.getChannel().getMessageById(e.getMessageId()).complete().editMessage(eb.build()).queue();
					EmbedBuilder log = new EmbedBuilder().setAuthor(e.getMember().getEffectiveName()+" used !higherlower", null, e.getUser().getEffectiveAvatarUrl()).setTimestamp(Instant.from(ZonedDateTime.now()));
    				log.setDescription(e.getMember().getAsMention()+" cashed out **$"+cashout+"**").setColor(Color.green)
    				.addField(e.getMember().getEffectiveName()+" New Balance", "$**"+newbux+"**", true)
    				.setFooter(e.getGuild().toString(), e.getGuild().getIconUrl()).setTimestamp(Instant.from(ZonedDateTime.now()));
    				me.darth.darthbot.main.Main.sm.getTextChannelById("590158585602768896").sendMessage(log.build()).queue();
					con.close();
					return;
					
				}
				if (rand < num && e.getReactionEmote().getName().equals("⬇") || rand > num && e.getReactionEmote().getName().equals("⬆") 
						|| rand == num && e.getReactionEmote().getName().equals("⬇") || rand == num && e.getReactionEmote().getName().equals("⬆")) {
					int start = Integer.parseInt(oldeb.getDescription().split("\n")[0].replace("*Starting Bet: $*", ""));
					int cashout = Integer.parseInt(oldeb.getDescription().split("\n")[1].replace("**Cashout Prize: $**", ""));
					cashout = cashout + Math.abs(start / 10);
					eb.setDescription("*Starting Bet: $*"+start+"\n**Cashout Prize: $**"+cashout+"");
					
				} else {
					eb.addField("You Lose!", "You guessed wrong! Game Over!", false);
					eb.setColor(Color.red);
					e.getChannel().getMessageById(e.getMessageId()).complete().editMessage(eb.build()).queue();
					con.close();
					return;
				}
				e.getChannel().getMessageById(e.getMessageId()).complete().editMessage(eb.build()).queue();
			}
		} catch (IndexOutOfBoundsException e1) {return;} catch (NullPointerException e2) {return;} catch (SQLException e3) {
			e3.printStackTrace();
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			if (e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getAuthor().getName().equals("Casino ~ Higher or Lower")) {
				if (!e.getMember().equals(e.getGuild().getMemberById(e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getFooter().getText()))
					&& !e.getMember().getUser().isBot()) {
					e.getTextChannel().removeReactionById(e.getMessageId(), e.getReaction().getReactionEmote().getName(), e.getMember().getUser()).queue();
					con.close();
					return;
				}
				ResultSet canBet = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "
    					+e.getMember().getUser().getIdLong());
				long bux = -1;
    			while (canBet.next()) {
    				bux = canBet.getLong("DBux");
    			}
				if (e.getMember().getUser().isBot()) {
					con.close();
					return;
				}
				MessageEmbed oldeb = e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0);
				int num = oldeb.getFields().size() - 1;
				try {
					num = Integer.parseInt(e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getFields().get(num).getValue());
				} catch (IndexOutOfBoundsException e1) {con.close(); return;} catch (NumberFormatException e2) { con.close();return;}
				int rand = new Random().nextInt(101);
				EmbedBuilder eb = new EmbedBuilder(oldeb);
				eb.addField("Higher or Lower:", ""+rand, false);
				if (!e.getMember().getUser().isBot()) {
					//e.getChannel().sendMessage(newnum+" - newnum\n`"+e.getReactionEmote().getName()+"`").queue();
				}
				if (e.getReactionEmote().getName().equals("💰") || eb.getFields().size() >= 6) {
					if (eb.getFields().size() <= 2) {
						return;
					}
					int start = Integer.parseInt(oldeb.getDescription().split("\n")[0].replace("*Starting Bet: $*", ""));
					int cashout = Integer.parseInt(oldeb.getDescription().split("\n")[1].replace("**Cashout Prize: $**", ""));
					if (eb.getFields().size() >= 6) {
						cashout = cashout + Math.abs(start / 10);
					}
					if (cashout > 300 && eb.getFields().size() <= 4) {
						e.getChannel().sendMessage("You must have 3+ guesses to cash out prizes over $300!").queue();
						return;
					}
					long newbux = bux + cashout;
					if (bux != -1337) {
						con.prepareStatement("UPDATE profiles SET DBux = "+newbux+" WHERE UserID = "+e.getMember().getUser().getIdLong()).execute();
					}
					long winnings = cashout - Integer.parseInt(oldeb.getDescription().split("\n")[0].replace("*Starting Bet: $*", ""));
					eb.addField("Successfully Cashed Out $"+winnings, "New Balance: $"+newbux, false);
					eb.setColor(Color.green);
					e.getChannel().getMessageById(e.getMessageId()).complete().editMessage(eb.build()).queue();
					EmbedBuilder log = new EmbedBuilder().setAuthor(e.getMember().getEffectiveName()+" used !higherlower", null, e.getUser().getEffectiveAvatarUrl()).setTimestamp(Instant.from(ZonedDateTime.now()));
    				log.setDescription(e.getMember().getAsMention()+" cashed out **$"+cashout+"**").setColor(Color.green)
    				.addField(e.getMember().getEffectiveName()+" New Balance", "$**"+newbux+"**", true)
    				.setFooter(e.getGuild().toString(), e.getGuild().getIconUrl()).setTimestamp(Instant.from(ZonedDateTime.now()));
    				me.darth.darthbot.main.Main.sm.getTextChannelById("590158585602768896").sendMessage(log.build()).queue();
					con.close();
					return;
					
				}
				if (rand < num && e.getReactionEmote().getName().equals("⬇") || rand > num && e.getReactionEmote().getName().equals("⬆") 
						|| rand == num && e.getReactionEmote().getName().equals("⬇") || rand == num && e.getReactionEmote().getName().equals("⬆")) {
					int start = Integer.parseInt(oldeb.getDescription().split("\n")[0].replace("*Starting Bet: $*", ""));
					int cashout = Integer.parseInt(oldeb.getDescription().split("\n")[1].replace("**Cashout Prize: $**", ""));
					cashout = cashout + Math.abs(start / 10);
					eb.setDescription("*Starting Bet: $*"+start+"\n**Cashout Prize: $**"+cashout+"");
					
				} else {
					eb.addField("You Lose!", "You guessed wrong! Game Over!\nNew Balance: $"+bux, false);
					eb.setColor(Color.red);
					e.getChannel().getMessageById(e.getMessageId()).complete().editMessage(eb.build()).queue();
					con.close();
					return;
				}
				e.getChannel().getMessageById(e.getMessageId()).complete().editMessage(eb.build()).queue();
			}
		} catch (IndexOutOfBoundsException e1) {return;} catch (NullPointerException e2) {return;}  catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

}
