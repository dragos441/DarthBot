package me.darth.darthbot.main;

import java.util.Date;

import me.darth.darthbot.commands.*;
import me.darth.darthbot.db.*;
import me.darth.darthbot.moderation.*;
import me.darth.darthbot.music.*;
import me.darth.darthbot.testserver.*;
import me.darth.darthbot.retalibot.*;
import me.darth.darthbot.retalibot.submit;
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
	public static int updatedmin = -1;
	//private static final String key = "NTY5NDYxNDY5MTU0OTAyMDE2.XLxG0w.U0xyCNtGEBRXMBOBAutkh_Jzgi8"; //Public Bot
	private static final String key = "NTc5NjQ3OTM5MTUyOTY5NzQ5.XOkv7g.Ln__EfJmO3jb-3VlpnWhI__MMlk"; //Dev Bot
	
	
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		jda = new JDABuilder(AccountType.BOT)
				.setToken(key).buildBlocking();
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
		jda.addEventListener(new Kick());
		jda.addEventListener(new Ban());
		jda.addEventListener(new Warn());
		jda.addEventListener(new Purge());
		jda.addEventListener(new Mute());
		jda.addEventListener(new GetRoles());
		jda.addEventListener(new newApplicant());
		jda.addEventListener(new SAC());
		jda.addEventListener(new submit());
		jda.addEventListener(new apply());
		jda.addEventListener(new ServerSetup());
		jda.addEventListener(new Lock());
		jda.addEventListener(new LevelRoles());
		jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing("!commands"), true);
		g = jda.getGuildById("568849490425937940");
		while (true) {
			int min = new Date().getMinutes();
			if (min != updatedmin) {
				me.darth.darthbot.main.AutoProcesses.removePunishments();
				if (min == 1) {
					me.darth.darthbot.main.AutoProcesses.purgeMessages();
					me.darth.darthbot.commands.Vote.listSort();
					jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing("!commands"), true);
				}
				if (min % 5 == 0) {
					me.darth.darthbot.main.Leaderboards.GlobalLeaderboard();
				}
				updatedmin = min;
			}
			Thread.sleep(5000);
		}
	}
	
	public static Member findUser(String target, Guild guild) {
		Member member = null;
		try {
			member = guild.getMemberById(target);
			return member;
		} catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException e1) {}
		try {
			member = (Member) guild.getMembersByEffectiveName(target, true).get(0);
			return member;
		} catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException e1) {}
		try {
			member = (Member) guild.getMembersByName(target, true).get(0);
			return member;
		} catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException e1) {}
		try {
			member = (Member) guild.getMembersByNickname(target, true).get(0);
			return member;
		} catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException e1) {}
		try {
			member = (Member) guild.getMemberById(target);
		} catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException e1) {}
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
				
				if (jda.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(jda.getGuildById("568849490425937940").getRoleById("575729381452939274"))
				&& jda.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(jda.getGuildById("568849490425937940").getRoleById("575729381452939274"))) {
					eb.setFooter(m.getUser().getName()+" is a Master Bug Hunter and an Ideas Connoisseur on the DarthBot Discord!", "https://i.imgur.com/lesRIbp.png");
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
