package me.darth.darthbot.commands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Rob extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!rob") || args[0].equalsIgnoreCase("!steal")) {
			if (!me.darth.darthbot.main.Main.economyEnabled) {
				e.getChannel().sendMessage(":no_entry: This section of the economy system is temporarily disabled due to an ongoing issue! Check the Official `!darthbot` Server for Updates!").queue();
				return;
			}
			if (args.length < 2) {
				e.getChannel().sendMessage(":no_entry: Incorrect Syntax `!rob <User>`").queue();
				return;
			}
			Member target = null;
			if (!e.getMessage().getMentionedMembers().isEmpty()) {
				target = e.getMessage().getMentionedMembers().get(0);
			} else {
				target = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" ", ""), e.getGuild());
			}
			if (target == null) {
				e.getChannel().sendMessage(":no_entry: User not found!").queue();
				return;
			}
			if (target.equals(e.getMember())) {
				e.getChannel().sendMessage(":no_entry: You can't rob yourself!").queue();
				return;
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getId()+" OR UserID = "+target.getUser().getId());
				int robchance = -1;
				int foundWeapon = -1;
				int foundDefense = -1;
				String robstring = null;
				String victimstring = null;
			    Calendar cal = Calendar.getInstance();
			    cal.add(Calendar.HOUR, -1);
				while (rs.next()) {
					
					if (e.getAuthor().getIdLong() == rs.getLong("UserID")) {
						robstring = rs.getString("Inventory");
						Calendar claimed = Calendar.getInstance();
			        	claimed.setTimeInMillis(rs.getLong("Robbed"));
			        	long mins = ChronoUnit.MINUTES.between(cal.toInstant(), claimed.toInstant());
			        	int hours = 0;
			        	while (mins >= 60) {
			        		hours++;
			        		mins = mins - 60;
			        	}
						if (rs.getLong("Robbed") >= cal.getTimeInMillis()) {
							e.getChannel().sendMessage(":no_entry: You may only rob people once an hour! You can rob another user in **"+hours+"** hours "
									+ "and **"+mins+"** minutes!").queue();
							return;
						}
						if (robstring == null || robstring.isEmpty()) {
							e.getChannel().sendMessage(":no_entry: You do not have a weapon to rob the user with! Purchase one at the `!shop`!").queue();
							return;
						}
						
						if (robstring.contains(",5")) {
							robchance = 50;
							foundWeapon = 5;
						} else if (robstring.contains(",4") && foundWeapon == -1) {
							robchance = 40;
							foundWeapon = 4;
						} else if (robstring.contains(",3") && foundWeapon == -1) {
							robchance = 30;
							foundWeapon = 3;
						} else if (robstring.contains(",2") && foundWeapon == -1) {
							robchance = 20;
							foundWeapon = 2;
						} else if (robstring.contains(",1") && foundWeapon == -1) {
							robchance = 10;
							foundWeapon = 1;
						} else {
							e.getChannel().sendMessage(":no_entry: You do not have a weapon to rob the user with! Purchase one at the `!shop`!").queue();
							return;
						}
					}
					
					if (target.getUser().getIdLong() == rs.getLong("UserID")) {
						victimstring = rs.getString("Inventory");
						if (rs.getLong("DBux") == -1337) {
							e.getChannel().sendMessage(":no_entry: You can't rob that user!").queue();
							return;
						}
						if (rs.getLong("DBux") < 100) {
							e.getChannel().sendMessage(":no_entry: A user must have at least **$100** to be robbed!").queue();
							return;
						}
						if (victimstring != null && !victimstring.replace(" ", "").isEmpty()) {
							if (victimstring.contains(",8")) {
								robchance = 50;
								foundDefense = 8;
							} else if (victimstring.contains(",7")) {
								robchance = 40;
								foundDefense = 7;
							} else if (victimstring.contains(",6")) {
								robchance = 30;
								foundDefense = 6;
							}
						}
					}
					
				}
				int randint = new Random().nextInt(100) + 1;
				ResultSet weapon = con.createStatement().executeQuery("SELECT * FROM StoreItems");
				String wname = "[WEAPON NOT FOUND]";
				String dname = "[DEFENSIVE WEAPON NOT FOUND]";
				EmbedBuilder robbery = new EmbedBuilder().setAuthor("Robbing "+target.getEffectiveName(), null, target.getUser().getEffectiveAvatarUrl()).setColor(Color.orange);
				while (weapon.next()) {
					if (foundWeapon == weapon.getInt("ID")) {
						wname = weapon.getString("Name");
					} else if (foundDefense == weapon.getInt("ID")) {
						dname = weapon.getString("Name");
					}
				}
				weapon.close();
				robbery.addField("You approach "+target.getEffectiveName(), "And draw your **"+wname+"**...", false);
				Message msg = e.getChannel().sendMessage(robbery.build()).complete();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ScheduledExecutorService executorService
			      = Executors.newSingleThreadScheduledExecutor();
				//e.getChannel().sendMessage("`DEBUG` "+robchance+" - "+randint+" odds").queue();
				int policechance = new Random().nextInt(10) + 1;
				if (policechance == 1) {
					ResultSet caught = con.createStatement().executeQuery("SELECT * FROM profiles where UserID = "+e.getAuthor().getId());
					while (caught.next()) {
						long bux = caught.getLong("DBux");
						long min = 0L;
						if (bux < 100) {
							 min = 1;
						} else {
							 min = bux / 100;
						}
						long max = min * 5;
						long rand = ThreadLocalRandom.current().nextLong(max);
						robbery.addField("🚓 You were caught!", "The Police caught you robbing "+target.getEffectiveName()+", and you bribe the officers **$"+new DecimalFormat("#,###").format(rand)+"** to stay out of jail!", false);
						robbery.setColor(Color.blue);
						long newbux = bux - rand;
						ScheduledFuture<?> scheduledFuture = executorService.schedule(() -> {
							//
							msg.editMessage(robbery.build()).queue();
						}, 1, TimeUnit.SECONDS);
						con.prepareStatement("UPDATE profiles SET DBux = "+newbux+" WHERE UserID = "+e.getAuthor().getId()).execute();
						con.close();
						return;
					}
				}
				if (robchance > randint) {
					ResultSet victim = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+target.getUser().getId());
					long rand = -1;
					while (victim.next()) {
						long bux = victim.getLong("DBux");
						long min = bux / 100;
						long max = min * 5;
						rand = ThreadLocalRandom.current().nextLong(max);
						long newbux = victim.getLong("DBux") - rand;
						robbery.addField(target.getEffectiveName()+" surrenders", "And hands over **$"+new DecimalFormat("#,###").format(rand)+"**. You drop your weapon and run!", false);
						robbery.setColor(Color.green);
						ScheduledFuture<?> scheduledFuture = executorService.schedule(() -> {
							msg.editMessage(robbery.build()).queue();
					    }, 1, TimeUnit.SECONDS);
						con.prepareStatement("UPDATE profiles SET DBux = "+newbux+" WHERE UserID = "+target.getUser().getId()).execute();
						EmbedBuilder log = new EmbedBuilder().setAuthor(e.getMember().getEffectiveName()+" robbed "+target.getEffectiveName(), null, e.getAuthor().getEffectiveAvatarUrl()).setTimestamp(Instant.from(ZonedDateTime.now()));
	    				log.setDescription(e.getMember().getAsMention()+" robbed "+target.getEffectiveName()+" for a total of **$"+rand+"**").setColor(Color.green)
	    				.addField(e.getMember().getEffectiveName()+" New Balance", "$**"+newbux+"**", true)
	    				.setFooter(e.getGuild().toString(), e.getGuild().getIconUrl()).setTimestamp(Instant.from(ZonedDateTime.now()));
	    				me.darth.darthbot.main.Main.sm.getTextChannelById("590158748736159744").sendMessage(log.build()).queue();
					}
					ResultSet robber = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getId());
					while (robber.next()) {
						long newbux = robber.getLong("DBux") + rand;
						if (robber.getInt("DBux") != -1337) {
							con.prepareStatement("UPDATE profiles SET DBux = "+newbux+" WHERE UserID = "+e.getAuthor().getId()).execute();
						}
					}
					
				} else {
					robbery.addField(target.getEffectiveName()+" escapes", "You drop your weapon, and walk away a failure!", false);
					robbery.setColor(Color.red);
					ScheduledFuture<?> scheduledFuture = executorService.schedule(() -> {
						msg.editMessage(robbery.build()).queue();
				    }, 1, TimeUnit.SECONDS);
					
					
				}
				robstring = robstring.replaceFirst(","+foundWeapon, "");
				con.prepareStatement("UPDATE profiles SET inventory = '"+robstring+"' WHERE UserID = "+e.getAuthor().getId()).execute();
				if (foundDefense != -1) {
					int rand = new Random().nextInt(10) + 1;
					if (rand == 1) {
						victimstring = victimstring.replaceFirst(","+foundDefense, "");
						robbery.setTitle(target.getEffectiveName()+" dropped their **"+dname+"**!", null);
						con.prepareStatement("UPDATE profiles SET inventory = '"+victimstring+"' WHERE UserID = "+target.getUser().getId()).execute();
						ScheduledFuture<?> scheduledFuture = executorService.schedule(() -> {
							msg.editMessage(robbery.build()).queue();
					    }, 1, TimeUnit.SECONDS);
						
					}
				}
				con.prepareStatement("UPDATE profiles SET Robbed = "+Calendar.getInstance().getTimeInMillis()+" WHERE UserID = "+e.getAuthor().getId()).execute();
				rs.close();
			    con.close();
			
			} catch (SQLException e1) {
			    e1.printStackTrace();
			}
		}
	}
}
