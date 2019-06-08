package me.darth.darthbot.db;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildJoin extends ListenerAdapter {

	public void onGuildJoin(GuildJoinEvent e) {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo");
		    boolean found = false;
		    while (rs.next())
		    {
		      long GuildID = rs.getLong("GuildID");
		      if (GuildID == e.getGuild().getIdLong()) {
		    	  found = true;
		      }
		    }
		    if (!found) {
				con.prepareStatement("INSERT INTO GuildInfo (GuildID, WelcomeChannel, LogChannel)"
						+ " values ("+e.getGuild().getIdLong()+", NULL, NULL);").execute();
				EmbedBuilder eb = new EmbedBuilder().setAuthor("Database - Guild Info Generated", null, e.getGuild().getIconUrl())
				.setDescription(e.getGuild().getName()+"'s *("+e.getGuild().getIdLong()+")* profile was generated (Method 1)").setColor(Color.green);
				me.darth.darthbot.main.Main.sm.getTextChannelById("569883444126023680").sendMessage(eb.build()).queue();	
		    }
		    con.close();
			
		} catch (SQLException e1) {
		   e1.printStackTrace();
		}
	}
	
}
