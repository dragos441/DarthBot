package me.darth.bot.db;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class RandomEarn extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		try {
			if (e.getAuthor().isBot() || e.getMessage().getContentRaw().toCharArray()[0] == '!' && !args[0].equalsIgnoreCase("!forcewin")) {
				return;
			}
		} catch (ArrayIndexOutOfBoundsException e1) {return;}
		try {
			if (!args[0].equalsIgnoreCase("!forcewin")) {
				Message prevmsg = e.getChannel().getHistory().retrievePast(2).complete().get(1);
				if (prevmsg.getAuthor() == e.getMember().getUser()) {
					return;
				}
			}
		} catch (IndexOutOfBoundsException e2) {return;}
		int multi = 1;
		int rN = new Random().nextInt(10);
		/*if (e.getMember().getRoles().contains(e.getGuild().getRoleById("569277445354815499"))) { // Bronze
			multi=2;
		} if (e.getMember().getRoles().contains(e.getGuild().getRoleById("569277448387297291"))) { //Silver
			multi=3;
		} if (e.getMember().getRoles().contains(e.getGuild().getRoleById("569277451037966352"))) { //Gold
			multi=4;
		}*/
		int money = ThreadLocalRandom.current().nextInt(8, 12 + 1);
		String multimsg = "";
		/*if (multi == 1) {
			multimsg = "Multiplier: **x1** (Normal)";
		} else if (multi == 2) {
			money = money * 2;
			multimsg = "Multiplier: **x2** (Bronze)";
		} if (multi == 3) {
			money = money * 3;
			multimsg = "Multiplier: **x3** (Silver)";
		} if (multi == 4) {
			money = money * 4;
			multimsg = "Multiplier: **x4** (Gold)";
		}*/
		
		if (rN == 1 || args[0].equalsIgnoreCase("!forcewin") && e.getMember().getRoles().contains(e.getGuild().getRoleById("557702978455339009"))) {
			EmbedBuilder eb = new EmbedBuilder();
			Member target = e.getMember();
			if (!e.getMessage().getMentionedMembers().isEmpty() && args[0].equalsIgnoreCase("!forcewin")) {
				target = e.getMessage().getMentionedMembers().get(0);
			}
			eb.setColor(Color.green);
			eb.setDescription(":money_with_wings:"+target.getAsMention()+" just found **$"+money+"** from chatting!\n"+multimsg);
			eb.setFooter("Use !profile to view your balance!", null);
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles WHERE UserID = "+e.getAuthor().getIdLong());
			      while (rs.next())
			      {
			        long UserID = rs.getLong("UserID");
			        long DBux = rs.getLong("DBux");
			        if (UserID == target.getUser().getIdLong()) {
			        	long newTotal = DBux + money;
			        	if (DBux != -1337) {
			        		con.prepareStatement("UPDATE profiles SET DBux = "+newTotal+" WHERE UserID = "+target.getUser().getIdLong()).execute();
			        	}
			        }
			      }
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			    e.getChannel().sendMessage("<@393796810918985728> Error! ```"+e1+"```").queue();
			}
			
			e.getChannel().sendMessage(eb.build()).complete().delete().queueAfter(20, TimeUnit.SECONDS);
		}
		
	}

}
