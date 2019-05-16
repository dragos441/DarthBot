package me.darth.darthbot.main;

import me.darth.darthbot.commands.*;
import me.darth.darthbot.db.*;
import me.darth.darthbot.moderation.History;
import me.darth.darthbot.music.*;
import me.darth.darthbot.testserver.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class Main {
	
	public static JDA jda = null;
	public static Guild g = null;
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		jda = new JDABuilder(AccountType.BOT)
				.setToken("NTY5NDYxNDY5MTU0OTAyMDE2.XLxG0w.U0xyCNtGEBRXMBOBAutkh_Jzgi8").buildBlocking();
		jda.addEventListener(new Ping());
		jda.addEventListener(new Suggest());
		jda.addEventListener(new ReportBug());
		jda.addEventListener(new Info());
		jda.addEventListener(new autoReply());
		jda.addEventListener(new PublicRooms());
		jda.addEventListener(new Whois());
		jda.addEventListener(new FindID());
		jda.addEventListener(new Commands());
		jda.addEventListener(new ProfileGen());
		jda.addEventListener(new ViewProfile());
		jda.addEventListener(new Casino());
		jda.addEventListener(new logMessages());
		jda.addEventListener(new RandomEarn());
		jda.addEventListener(new SetBal());
		jda.addEventListener(new ReactRole());
		jda.addEventListener(new ChangeLog());
		jda.addEventListener(new Daily());
		jda.addEventListener(new GuildJoin());
		jda.addEventListener(new ServerLogs());
		jda.addEventListener(new MusicCommand());
		jda.addEventListener(new SearchTrello());
		jda.addEventListener(new Discord());
		jda.addEventListener(new WelcomeMessages());
		jda.addEventListener(new FAQ());
		jda.addEventListener(new Vote());
		jda.addEventListener(new Experience());
		jda.addEventListener(new Quiz());
		jda.addEventListener(new History());
		jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing("!commands"), true);
		g = jda.getGuildById("568849490425937940");
	}
	
	public static Member findUser(String target) {
		Member member = null;
		try {
			member = g.getMemberById(target);
			return member;
		} catch (ArrayIndexOutOfBoundsException e1) {} catch (NumberFormatException e2) {} catch (IndexOutOfBoundsException e3) {}
		try {
			member = (Member) g.getMembersByEffectiveName(target, true).get(0);
			return member;
		} catch (ArrayIndexOutOfBoundsException e1) {} catch (NumberFormatException e2) {} catch (IndexOutOfBoundsException e3) {}
		try {
			member = (Member) g.getMembersByName(target, true).get(0);
			return member;
		} catch (ArrayIndexOutOfBoundsException e1) {} catch (NumberFormatException e2) {} catch (IndexOutOfBoundsException e3) {}
		try {
			member = (Member) g.getMembersByNickname(target, true).get(0);
			return member;
		} catch (ArrayIndexOutOfBoundsException e1) {} catch (NumberFormatException e2) {} catch (IndexOutOfBoundsException e3) {}
		return null;	
	}
	
	public static EmbedBuilder affiliation(Member m) {
		EmbedBuilder eb = new EmbedBuilder();
		try {
			if (jda.getGuildById("568849490425937940").isMember(m.getUser())) {
				eb.setFooter(m.getUser().getName()+" is a Member of the DarthBot Discord!", "https://i.imgur.com/OhUmIFC.png");
				if (jda.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(jda.getGuildById("568849490425937940").getRoleById("575729770164256768"))) {
					eb.setFooter(m.getUser().getName()+" is a Creator of Great Ideas on the DarthBot Discord!", "https://i.imgur.com/G83T1Kh.png");
				}
				if (jda.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(jda.getGuildById("568849490425937940").getRoleById("575729381452939274"))) {
					eb.setFooter(m.getUser().getName()+" is a Master of Hunting Bugs on the DarthBot Discord!", "https://i.imgur.com/G6NedwO.png");
				}
				if (jda.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(jda.getGuildById("568849490425937940").getRoleById("569464005416976394"))) {
					eb.setFooter(m.getUser().getName()+" is a Server Moderator on the DarthBot Discord!", "https://i.imgur.com/P0Fkt4t.png");
				}
				if (jda.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(jda.getGuildById("568849490425937940").getRoleById("569463842552152094"))) {
					eb.setFooter(m.getUser().getName()+" is the Creator of DarthBot!", "https://i.imgur.com/kb2zLnn.png");
				}
			}
		} catch (NullPointerException e1) {}
		return eb;
	}
}
