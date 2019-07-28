package me.darth.darthbot.commands;

import java.awt.Color;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Avatar extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!avatar") || args[0].equalsIgnoreCase("!profilepic") || args[0].equalsIgnoreCase("!pfp")) {
			Member target = null;
			if (args.length < 2) {
				target = e.getMember();
			} else if (!e.getMessage().getMentionedMembers().isEmpty()) {
				target = e.getMessage().getMentionedMembers().get(0);
			} else {
				target = me.darth.darthbot.main.Main.findUser(e.getMessage().getContentRaw().replace(args[0]+" ", ""), e.getGuild());
			}
			if (target == null) {
				e.getChannel().sendMessage(":no_entry: User not found!").queue();
				return;
			}
			EmbedBuilder eb = new EmbedBuilder().setAuthor(target.getEffectiveName()+"'s Profile Picture", target.getUser().getEffectiveAvatarUrl(), target.getUser().getEffectiveAvatarUrl())
				.setThumbnail(target.getUser().getEffectiveAvatarUrl()).setDescription("Link to "+target.getAsMention()+"'s avatar: **"+target.getUser().getEffectiveAvatarUrl()+"**")
				.setColor(Color.blue);
			e.getChannel().sendMessage(eb.build()).queue();
		}
		
	}

}
