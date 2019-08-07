package me.darth.darthbot.main;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class autoReply extends ListenerAdapter {
	
	@Override
	public void onMessageUpdate(MessageUpdateEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		
		//temp anti link system
		if (!e.getGuild().getId().equals("568849490425937940")) {
			return;
		}
		String msg = e.getMessage().getContentRaw().replace(" ", "").toLowerCase();
		if (msg.contains("www.") || msg.contains("http://") || msg.contains("https://") || msg.contains(".com") || msg.contains(".co.uk")
			|| !e.getMessage().getInvites().isEmpty() || msg.contains(".gg")) {
			if (msg.contains("trello.com") || msg.contains("discordapp.com") || e.getMember().getRoles().contains(e.getGuild().getRoleById("589797120207486988"))
					|| e.getMember().getRoles().contains(e.getGuild().getRoleById("589800268795871243")) || e.getAuthor().isBot()) {
				return;
			} else {
				e.getMessage().delete().queue();
				e.getChannel().sendMessage(e.getMember().getAsMention()+", no links allowed!").queue();
			}
		}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (e.getChannel().getId().equals("604018500272390193")) {
			e.getMessage().addReaction("✅").queue();
		}
		if (e.getAuthor().getId().equals("159770472567799808") && e.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!say")
				|| e.getAuthor().getId().equals("328581145190989825") && e.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!say")) {
			e.getMessage().delete().queue();
			e.getChannel().sendMessage(e.getMessage().getContentRaw().replace(e.getMessage().getContentRaw().split(" ")[0]+" ", "")).queue();
		}
		if (e.getMessage().getMentionedMembers().contains(e.getGuild().getMemberById("569461469154902016"))) {
			e.getChannel().sendMessage("My ears are burning :fire: - You can use `!commands` for a list of bot commands :)").complete().delete().queueAfter(15, TimeUnit.SECONDS);
		}
		if (args[0].equalsIgnoreCase("!patreon")) {
			e.getChannel().sendMessage("Feeling generous? Support the Development of the bot here >> https://www.patreon.com/darthbot :heart:").queue();
		}
		if (args[0].equalsIgnoreCase("!rr") || args[0].equalsIgnoreCase("!rolerewards")) {
			e.getChannel().sendMessage("Looking for role rewards? They've moved to `!lr` or `!levelrewards`!").queue();
		}
		
		//temp anti link system
		if (!e.getGuild().getId().equals("568849490425937940")) {
			return;
		}
		String msg = e.getMessage().getContentRaw().replace(" ", "").toLowerCase();
		if (msg.contains("www.") || msg.contains("http://") || msg.contains("https://") || msg.contains(".com") || msg.contains(".co.uk")
			|| !e.getMessage().getInvites().isEmpty() || msg.contains(".gg")) {
			if (msg.contains("trello.com") || msg.contains("discordapp.com") || e.getMember().getRoles().contains(e.getGuild().getRoleById("589797120207486988"))
					|| e.getMember().getRoles().contains(e.getGuild().getRoleById("589800268795871243")) || e.getAuthor().isBot()) {
				return;
			} else {
				e.getMessage().delete().queue();
				e.getChannel().sendMessage(e.getMember().getAsMention()+", no links allowed!").queue();
			}
		}
		
		if (msg.contains("how") || msg.contains("why") || msg.contains("what") || msg.contains("?")) {
			if (!e.getMessage().getMentionedMembers().isEmpty() && e.getMessage().getMentionedMembers().contains(e.getGuild().getMemberById("159770472567799808"))
				|| msg.contains("darth")) {
				if (!e.getMember().getRoles().contains(e.getGuild().getRoleById("589797120207486988")) && !e.getChannel().getId().equals("574246600570830853")) {
					e.getChannel().sendMessage("All questions regarding the bot should be asked in <#574246600570830853> so others can benefit from the response!").complete().delete().queueAfter(30, TimeUnit.SECONDS);
				}
			}
		}
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		if (e.getAuthor().equals(e.getJDA().getSelfUser())) {
			return;
		}
		e.getAuthor().openPrivateChannel().queue((channel) ->
	    {
	    	if (e.getMessage().getInvites().size() == 0) {
	    		e.getChannel().sendMessage("Responding to DMs is a bit beyond me yet. If you're looking for Support, join the DarthBot Server! https://discord.gg/nVpzgJG").queue();
	    	} else {
	    		e.getChannel().sendMessage("I'm currently in an open beta! If you'd like to have me on your server, invite me using this link >> https://discordapp.com/api/oauth2/authorize?client_id=569461469154902016&permissions=8&scope=bot").queue();
	    	}
	    });
	}
	
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		if (e.getGuild().getDefaultChannel() != null) {
			e.getGuild().getDefaultChannel().sendMessage("This server looks pretty cool, even cooler now I'm here :sunglasses: - Customise me by using the command `!setup`!").queue();
			me.darth.darthbot.main.Main.sm.getVoiceChannelById("583379618682241048").getManager().setName("Serving "+e.getJDA().getGuilds().size()+" Guilds!").queue();
		}
	}
	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		if (e.getGuild().getDefaultChannel() != null) {
			e.getGuild().getDefaultChannel().sendMessage("This server looks pretty cool, even cooler now I'm here :sunglasses: - Customise me by using the command `!setup`!").queue();
			me.darth.darthbot.main.Main.sm.getVoiceChannelById("583379618682241048").getManager().setName("Serving "+e.getJDA().getGuilds().size()+" Guilds!").queue();
		}
	}
}