package me.darth.darthbot.main;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class WelcomeMessages extends ListenerAdapter {

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		
		TextChannel c = null;
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("WelcomeChannel") != 0) {
					c = e.getGuild().getTextChannelById(g.getLong("WelcomeChannel"));
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (c != null) {
			c.sendMessage(new EmbedBuilder().setAuthor("Member Joined", null, e.getMember().getUser().getEffectiveAvatarUrl()).setColor(Color.green)
				.setThumbnail(e.getMember().getUser().getEffectiveAvatarUrl()).setDescription(e.getMember().getAsMention()+" has joined the server! We now have  `"+e.getGuild().getMembers().size()+"` "
						+ "members!").setTimestamp(Instant.from(ZonedDateTime.now())).build()).queue();
		}
		
	}
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
		
		TextChannel c = null;
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet g = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getId());
			while (g.next()) {
				long GuildID = g.getLong("GuildID");
				if (GuildID == e.getGuild().getIdLong() && g.getLong("WelcomeChannel") != 0) {
					c = e.getGuild().getTextChannelById(g.getLong("WelcomeChannel"));
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (c != null) {
			c.sendMessage(new EmbedBuilder().setAuthor("Member Left", null, e.getMember().getUser().getEffectiveAvatarUrl()).setColor(Color.red)
				.setThumbnail(e.getMember().getUser().getEffectiveAvatarUrl()).setDescription(e.getMember().getAsMention()+" left the server! We now have `"+e.getGuild().getMembers().size()+"` "
						+ "members!").setTimestamp(Instant.from(ZonedDateTime.now())).build()).queue();
		}
		
	}
	
}
