package me.darth.darthbot.commands;

import java.awt.Color;

import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Shard extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		ShardManager sm = me.darth.darthbot.main.Main.sm;
		if (args[0].equalsIgnoreCase("!shard")) {
			for (int x = 0 ; x < sm.getShardsTotal() ; x++) {
				if (sm.getShardById(x).getGuildById(e.getGuild().getId()) != null) {
					e.getChannel().sendMessage("Running on Shard "+x+" / "+sm.getShardsTotal()+"\nAverage Global Ping: `"+sm.getAveragePing()+"`").queue();
				}
			}
		}
		
		if (args[0].equalsIgnoreCase("!reboot") || args[0].equalsIgnoreCase("!restart")) {
			Guild g = me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940");
			if (g.getMember(e.getMember().getUser()).getRoles().contains(g.getRoleById("569464005416976394"))) {
				me.darth.darthbot.main.Main.sm.getTextChannelById("569883444126023680").sendMessage(new EmbedBuilder()
						.setAuthor("Shard Manager - Full Restart", null, e.getGuild().getIconUrl()).setDescription("All shards have been rebooted by "+e.getMember().getAsMention())
						.setColor(Color.red).setFooter(e.getGuild().toString(), null).build()).queue();
				Message msg = e.getChannel().sendMessage(":white_check_mark: Restarting all Shards").complete();
				sm.restart();
			} else {
				e.getChannel().sendMessage(":no_entry: You do not have permission to use this command!").queue();
			}
		}
	}

}
