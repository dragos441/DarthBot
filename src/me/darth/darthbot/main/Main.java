package me.darth.darthbot.main;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import me.darth.darthbot.commands.*;
import me.darth.darthbot.db.*;
import me.darth.darthbot.moderation.*;
import me.darth.darthbot.music.*;
import me.darth.darthbot.natter.Project_Natter;
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
	
	public static final boolean economyEnabled = true;
	
	
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
		builder.addEventListeners(new HigherLower());
		builder.addEventListeners(new logMessages());
		builder.addEventListeners(new RandomEarn());
		builder.addEventListeners(new SetBal());
		builder.addEventListeners(new ReactRole());
		builder.addEventListeners(new DBCommands());
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
		builder.addEventListeners(new Setup());
		builder.addEventListeners(new Lock());
		builder.addEventListeners(new LevelRoles());
		builder.addEventListeners(new Leaderboards());
		builder.addEventListeners(new Shard());
		builder.addEventListeners(new Give());
		builder.addEventListeners(new EditMsg());
		builder.addEventListeners(new Avatar());
		builder.addEventListeners(new Shop());
		builder.addEventListeners(new Rob());
		builder.addEventListeners(new Slots());
		builder.addEventListeners(new PingTEST());
		builder.addEventListeners(new Ball8());
		builder.addEventListeners(new AutoMod());
		builder.addEventListeners(new CustomStores());
		builder.addEventListeners(new Clans());
		builder.addEventListeners(new Debug());
		//builder.addEventListeners(new Project_Natter());
		sm = builder.build();
		me.darth.darthbot.main.AutoProcesses.chatLeaderboards();
		if (key.contains("NTc")) {
			sm.setPresence(OnlineStatus.DO_NOT_DISTURB, Game.playing("[BETA] !commands"));
		} else {
			sm.setPresence(OnlineStatus.ONLINE, Game.playing("[BETA] !commands"));
		}
		Thread.sleep(5000);
		while (true) {
			try {
				if (key.contains("NTY")) {
					int min = Calendar.getInstance().get(Calendar.MINUTE);
					if (min != updatedmin) {
						me.darth.darthbot.main.AutoProcesses.removePunishments();
						sm.getVoiceChannelById("585917931586584586").getManager().setName("Supporting "+new DecimalFormat("#,###").format(sm.getGuilds().size())+" Servers").queue();
						sm.getVoiceChannelById("585918083168731146").getManager().setName("Serving "+new DecimalFormat("#,###").format(sm.getUsers().size())+" Users").queue();
						if (min % 5 == 0 || updatedmin == -1) {
							me.darth.darthbot.main.Leaderboards.GlobalLeaderboard();
							me.darth.darthbot.main.AutoProcesses.chatLeaderboards();
							me.darth.darthbot.main.Leaderboards.Retali8Leaderboard();
						}
						if (min == 0 || updatedmin == -1) {
							//me.darth.darthbot.commands.Vote.listSort();
							me.darth.darthbot.main.AutoProcesses.purgeMessages();
							me.darth.darthbot.main.AutoProcesses.leaveChannels(sm.getGuilds());
							sm.setPresence(OnlineStatus.ONLINE, Game.playing("[BETA] !commands"));
						}
						updatedmin = min;
					}
					Thread.sleep(5000);
				} else {
					break;
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
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("590261682430017566"))) {
					eb.setFooter(m.getEffectiveName()+" is a Bot Innovator on the DarthBot Discord!", "https://i.imgur.com/c8NuUYh.png");
				}	
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("587318643633684491"))) {
					eb.setFooter(m.getEffectiveName()+" Nitro Boosts the DarthBot Discord!", "http://pimg.p30download.com/APK_IMG/n/com.nitro.boost.and.cleaner/icon/icon_4_small.png");
				}
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("582164371455606784"))) {
					eb.setFooter(m.getEffectiveName()+" Supports the Development of DarthBot!", "https://is5-ssl.mzstatic.com/image/thumb/Purple123/v4/56/00/8a/56008a35-b11c-d31d-da7d-05ecba9bb69b/AppIcon-0-1x_U007emarketing-0-0-GLES2_U002c0-512MB-sRGB-0-0-0-85-220-0-0-0-7.png/246x0w.jpg");
				}
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("589800268795871243"))) {
					eb.setFooter(m.getEffectiveName()+" is a Community Supporter on the DarthBot Discord!", "https://i.imgur.com/nc5vmM1.png");
				}
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("592813831164657684"))) {
					eb.setFooter(m.getEffectiveName()+" is the Event Coordinator on the DarthBot Discord!", "https://i.imgur.com/wN5JF1r.png");
				}
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("589550817649098773"))) {
					eb.setFooter(m.getEffectiveName()+" is the Feedback and Clans Manager of DarthBot!", "https://i.imgur.com/P0Fkt4t.png");
				}
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("589550729593880590"))) {
					eb.setFooter(m.getEffectiveName()+" is the Community Support Team Manager on the DarthBot Discord!", "https://i.imgur.com/P0Fkt4t.png");
				}
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("589550625537392643"))) {
					eb.setFooter(m.getEffectiveName()+" is the Economy Manager of DarthBot!", "https://i.imgur.com/P0Fkt4t.png");
				}
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getRoles().contains(sm.getGuildById("568849490425937940").getRoleById("569463842552152094"))) {
					eb.setFooter(m.getEffectiveName()+" is a Developer of DarthBot!", "https://i.imgur.com/kb2zLnn.png");
				}
				if (sm.getGuildById("568849490425937940").getMember(m.getUser()).getUser().getId().equals("159770472567799808")) {
					eb.setFooter(m.getEffectiveName()+" is the Creator of DarthBot!", "https://i.imgur.com/kb2zLnn.png");
				}
			}
		} catch (NullPointerException e1) {}
		return eb;
	}
	
	
	
}
