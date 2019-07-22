package me.darth.darthbot.commands;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!commands") || args[0].equalsIgnoreCase("!help") || args[0].equalsIgnoreCase("!command") || args[0].equalsIgnoreCase("!cmd") || args[0].equalsIgnoreCase("!cmds")) {
			String def = "`!setup` - View commands to configure DarthBot\n`!clans help` View Clans commands\n`!commands` - View command categories\n`!commands Economy` - View economy commands\n"
					+ "`!commands Moderation` - View moderation commands\n`!commands Music` - View music commands\n`!commands Misc` - View miscellaneous commands";
			EmbedBuilder eb = new EmbedBuilder().setAuthor("Commands", null, e.getGuild().getIconUrl()).setColor(Color.blue);
			if (!e.getGuild().getId().equals("568849490425937940")) {
				eb.setFooter("Why not join the DarthBot Discord? Type !darthbot to view more info!", e.getJDA().getSelfUser().getEffectiveAvatarUrl());
			}
			if (args.length < 2) {
				eb.setDescription(def);
				e.getChannel().sendMessage(eb.build()).queue();
			} else if (args[1].equalsIgnoreCase("economy")) {
				eb.setDescription("`!profile <User>` View a users profile\n"
						+ "`!slots <Amount>` Bet an amount of money on a Fruit Machine\n"
						+ "`!higherlower <Amount>` Bet an amount of money on a game of higher lower (1-100)\n"
						+ "`!quiz` Get a random quiz question, and the first to answer wins DBux\n"
						+ "`!give <User> <Amount>` Transfer some of your money to another user");
				eb.setFooter("<> means an argument is required - () means an argument is optional", null) ;
				e.getChannel().sendMessage(eb.build()).queue();
			} else if (args[1].equalsIgnoreCase("moderation")) {
				eb.setDescription("`!warn <User> (Reason)` Issues a user with a warning\n"
						+ "`!mute <User> (Time) (Reason)` Mutes a user from speaking with an optional duration\n"
						+ "`!kick <User> (Reason)` Kicks the user from the server\n"
						+ "`!ban <User< (Time) (Reason)` Bans the user from the server with an optional duration\n"
						+ "`!purge <1-99>` Purge an amount of messages from chat\n"
						+ "`!lock` Locks the current channel from people without a permission override from speaking\n"
						+ "`!unlock` Unlocks the current channel\n"
						+ "`!history <User>` Looks up a users past 20 punishments");
				eb.setFooter("<> means an argument is required - () means an argument is optional", null) ;
				e.getChannel().sendMessage(eb.build()).queue();
			} else if (args[1].equalsIgnoreCase("music")) {
				eb.setDescription("`!play <Song Name / YouTube URL` Plays a song from YouTube\n"
						+ "`!pause` Pauses the current song\n"
						+ "`!stop` Stops the current song from playing\n"
						+ "`!queue` Displays the current song queue\n"
						+ "`!skip` Skips the current song\n"
						+ "`!clearqueue` Clears the song queue\n"
						+ "`!playing` Displays what song is currently playing\n"
						+ "`!join` Forces the bot to join your current voice channel");
				eb.setFooter("<> means an argument is required - () means an argument is optional", null) ;
				e.getChannel().sendMessage(eb.build()).queue();
			} else if (args[1].equalsIgnoreCase("misc")) {
				eb.setDescription("`!ping` Checks the bot's latency between discord and itself\n"
						+ "`!trello` Displays the Development Trello\n"
						+ "`!search <Text>` Searches the development trello for an existing suggestion/bug\n"
						+ "`!suggest <Title - Description>` Suggests an idea for improving the bot\n"
						+ "`!reportbug <Title - Description` Reports a bug regarding the bot\n"
						+ "`info` Displays information about the server\n"
						+ "`!whois <User>` Displays information about a user\n"
						+ "`!getid <Category/Emote/Member/Role/Channel` Gets the ID code for an object\n"
						+ "`!shard` Displays basic shard information");
				eb.setFooter("<> means an argument is required - () means an argument is optional", null) ;
				e.getChannel().sendMessage(eb.build()).queue();
			} else {
				eb.setDescription(def);
				e.getChannel().sendMessage(eb.build()).queue();
			}
		}
	}
}
