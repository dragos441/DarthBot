package me.darth.darthbot.main;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import me.darth.darthbot.commands.*;
import me.darth.darthbot.db.*;
import me.darth.darthbot.moderation.*;
import me.darth.darthbot.music.*;
import me.darth.darthbot.testserver.*;
import me.darth.darthbot.retalibot.*;
import me.darth.darthbot.retalibot.submit;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class Main {
	
	//public static JDA jda = null;
	public static ShardManager sm = null;
	public static int updatedmin = -1;
	//private static final String key = "NTY5NDYxNDY5MTU0OTAyMDE2.XLxG0w.U0xyCNtGEBRXMBOBAutkh_Jzgi8"; //Public Bot
	private static final String key = "NTc5NjQ3OTM5MTUyOTY5NzQ5.XOkv7g.Ln__EfJmO3jb-3VlpnWhI__MMlk"; //Dev Bot
	
	
	
	public static void main(String[] args) throws Exception {
		DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder(key);
		builder.addEventListeners(new Ping());
		builder.addEventListeners(new Suggest());
		builder.addEventListeners(new ReportBug());
		builder.addEventListeners(new Info());
		builder.addEventListeners(new autoReply());
		builder.addEventListeners(new PublicRooms());
		builder.addEventListeners(new Whois());
		builder.addEventListeners(new FindID());
		builder.addEventListeners(new Commands());
		builder.addEventListeners(new ProfileGen());
		builder.addEventListeners(new ViewProfile());
		builder.addEventListeners(new Casino());
		builder.addEventListeners(new logMessages());
		builder.addEventListeners(new RandomEarn());
		builder.addEventListeners(new SetBal());
		builder.addEventListeners(new ReactRole());
		builder.addEventListeners(new ChangeLog());
		builder.addEventListeners(new Daily());
		builder.addEventListeners(new GuildJoin());
		builder.addEventListeners(new ServerLogs());
		builder.addEventListeners(new MusicCommand());
		builder.addEventListeners(new SearchTrello());
		builder.addEventListeners(new DarthBot());
		builder.addEventListeners(new WelcomeMessages());
		builder.addEventListeners(new FAQ());
		builder.addEventListeners(new Vote());
		builder.addEventListeners(new Experience());
		builder.addEventListeners(new Quiz());
		builder.addEventListeners(new History());
		builder.addEventListeners(new Kick());
		builder.addEventListeners(new Ban());
		builder.addEventListeners(new Warn());
		builder.addEventListeners(new Purge());
		builder.addEventListeners(new Mute());
		builder.addEventListeners(new GetRoles());
		builder.addEventListeners(new newApplicant());
		builder.addEventListeners(new SAC());
		builder.addEventListeners(new submit());
		builder.addEventListeners(new apply());
		builder.addEventListeners(new ServerSetup());
		builder.addEventListeners(new Lock());
		builder.addEventListeners(new LevelRoles());
		builder.addEventListeners(new Leaderboards());
		builder.addEventListeners(new Shard());
		builder.addEventListeners(new Give());
		builder.addEventListeners(new EditMsg());
		builder.addEventListeners(new Avatar());
		builder.addEventListeners(new Inventories());
		builder.addEventListeners(new Rob());
		sm = builder.build();
		sm.setPresence(OnlineStatus.ONLINE, Game.playing("[BETA] !commands"));
		Thread.sleep(5000);
		while (true) {
			try {
				if (key.contains("NTY")) {
					int min = Calendar.getInstance().get(Calendar.MINUTE);
					if (min != updatedmin) {
						me.darth.darthbot.main.AutoProcesses.removePunishments();
						sm.getVoiceChannelById("585917931586584586").getManager().setName("Supporting "+new DecimalFormat("#,###").format(sm.getGuilds().size())+" Servers").queue();
						sm.getVoiceChannelById("585918083168731146").getManager().setName("Serving "+new DecimalFormat("#,###").format(sm.getUsers().size())+" Users").queue();
						if (min == 0) {
							me.darth.darthbot.commands.Vote.listSort();
							me.darth.darthbot.main.AutoProcesses.purgeMessages();
							me.darth.darthbot.main.AutoProcesses.leaveEmptyChannels();
							sm.setPresence(OnlineStatus.ONLINE, Game.playing("[BETA] !commands"));
						}
						if (min % 5 == 0 || updatedmin == -1) {
							me.darth.darthbot.main.Leaderboards.GlobalLeaderboard();
							me.darth.darthbot.main.Leaderboards.Retali8Leaderboard();
						}
						updatedmin = min;
					}
					Thread.sleep(5000);
				}
			} catch (NullPointerException | IllegalStateException e1) {
				e1.printStackTrace();
				updatedmin = new Date().getMinutes();
			}
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
			if (sm.getGuildById("568849490425937940").isMember(m.getUser())) {
				eb.setFooter(m.getUser().getName()+" is a Member of the DarthBot Discord!", "https://i.imgur.com/OhUmIFC.png");
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("575729770164256768"))) {
					eb.setFooter(m.getUser().getName()+" is a Creator of Great Ideas on the DarthBot Discord!", "https://i.imgur.com/G83T1Kh.png");
				}
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("575729381452939274"))) {
					eb.setFooter(m.getUser().getName()+" is a Master of Hunting Bugs on the DarthBot Discord!", "https://i.imgur.com/G6NedwO.png");
				}
				
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("575729381452939274"))
				&& sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("575729381452939274"))) {
					eb.setFooter(m.getUser().getName()+" is a Master Bug Hunter and an Ideas Connoisseur on the DarthBot Discord!", "https://i.imgur.com/lesRIbp.png");
				}
				
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("569464005416976394"))) {
					eb.setFooter(m.getUser().getName()+" is a Server Moderator on the DarthBot Discord!", "https://i.imgur.com/P0Fkt4t.png");
				}
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("569463842552152094"))) {
					eb.setFooter(m.getUser().getName()+" is the Creator of DarthBot!", "https://i.imgur.com/kb2zLnn.png");
				}
			}
		} catch (NullPointerException e1) {}
		return eb;
	}
}
