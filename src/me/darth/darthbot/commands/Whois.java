package me.darth.darthbot.commands;

import java.awt.Color;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Whois extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		Member target = e.getMember();
		if (args[0].equalsIgnoreCase("!whois")) {
			try {
				if (!e.getMessage().getMentionedMembers().isEmpty()) {
					target = e.getMessage().getMentionedMembers().get(0);
				} else {
					target = me.darth.darthbot.main.Main.findUser(args[1], e.getGuild());
					if (target == null) {
						target = e.getMember();
					}
				}
			} catch (ArrayIndexOutOfBoundsException e1) {target=e.getMember();}
			EmbedBuilder eb = new EmbedBuilder(me.darth.darthbot.main.Main.affiliation(target));
			eb.setColor(Color.BLUE);
			eb.setAuthor("Information about "+target.getEffectiveName(),null,target.getUser().getAvatarUrl());
			eb.addField("User", ""+target.getAsMention(), true);
			if (target.getNickname() == null) {
				eb.addField("Nickname", "None", true);
			} else {
				eb.addField("Nickname", ""+target.getNickname(), true);
			}
			if (target.getGame() == null) {
				eb.addField("Currently Playing", "Nothing", true);
			} else {
				eb.addField("Currently Playing", ""+target.getGame().getName(), true);
			}
			int hour = -1;
			int minute = -1;
			if (target.getJoinDate().getHour() < 10) {
				hour = Integer.parseInt("0"+target.getJoinDate().getHour());
			} else {
				hour = target.getJoinDate().getHour();
			}
			if (target.getJoinDate().getMinute() < 10) {
				minute = Integer.parseInt("0"+target.getJoinDate().getMinute());
			} else {
				minute = target.getJoinDate().getMinute();
			}
			eb.addField("Join Date", ""+target.getJoinDate().getDayOfMonth()+"/"+target.getJoinDate().getMonthValue()
				+"/"+target.getJoinDate().getYear()+" @ "+hour+":"+minute, true);
			List<Role> rolesRaw = target.getRoles();
			List<String> roles = new ArrayList<String>();
			int n2 = rolesRaw.size();
			while (n2 > 0) {
				n2 = n2 - 1;
				Role r = rolesRaw.get(n2);
				String mention = r.getAsMention();
				roles.add(mention);
			}
			Collections.reverse(roles);
			eb.addField("Roles ["+roles.size()+"]", ""+roles, true);
			eb.setThumbnail(target.getUser().getAvatarUrl());
			if (target.getUser().getId().equals("569461469154902016")) {
				eb.addField("Developed by", "**Darth#9386**", true);
			}
			List<Permission> permsRaw = target.getPermissions();
			
			List<String> perms = new ArrayList<String>();
			if (!target.getRoles().isEmpty()) {
				eb.setColor(target.getRoles().get(0).getColorRaw());
			}
			int n = permsRaw.size();
			while (n > 0) {
				n = n - 1;
				Permission p = permsRaw.get(n);
				if (!p.equals(Permission.MESSAGE_ADD_REACTION) && 
					!p.equals(Permission.MESSAGE_ATTACH_FILES) && !p.equals(Permission.MESSAGE_EMBED_LINKS) && 
					!p.equals(Permission.MESSAGE_EXT_EMOJI) && !p.equals(Permission.MESSAGE_HISTORY) && 
					!p.equals(Permission.MESSAGE_READ) && !p.equals(Permission.MESSAGE_TTS) && !p.equals(Permission.MESSAGE_WRITE) && 
					!p.equals(Permission.VIEW_CHANNEL) && !p.equals(Permission.VOICE_CONNECT) && !p.equals(Permission.VOICE_SPEAK) &&
					!p.equals(Permission.VOICE_USE_VAD)) {
						perms.add(p.getName());
				}
			}
			Collections.reverse(perms);
			String permsstring = perms.toString();
			eb.addField("Permissions ["+perms.size()+"]", ""+permsstring, true);
			
			
			eb.setTimestamp(Instant.from(ZonedDateTime.now()));
			e.getChannel().sendMessage(eb.build()).queue();
			
		}
	}

}
