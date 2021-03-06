package me.darth.darthbot.testserver;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class FAQ extends ListenerAdapter {

	static String fm = "**Have a question about DarthBot?** Type it in chat below, and it'll get answered soon by Darth.\n*Before you do though, please scroll up and make sure "
			+ "your question hasn't already been answered ^^*";
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
         if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
		if (e.getGuild().getId().equals("568849490425937940") && e.getChannel().getId().equals("574246600570830853") && !e.getAuthor().isBot()) {
			EmbedBuilder eb = new EmbedBuilder().setAuthor("Asked by "+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator(), null, e.getAuthor().getEffectiveAvatarUrl())
				.addField("Question", e.getMessage().getContentDisplay(), false).setColor(Color.orange).setFooter(e.getMember().getUser().getId(), null);
			Message msg = e.getGuild().getTextChannelById("574250427462320168").sendMessage("`Command incoming!`").embed(eb.build()).complete();
			msg.editMessage("!r "+msg.getId()).queue();
			e.getGuild().getTextChannelById("574250427462320168").sendMessage("<@&569463842552152094> (New Question)").complete().delete().queueAfter(10, TimeUnit.SECONDS);
			e.getChannel().sendMessage(e.getMember().getAsMention()+", your question has been forwarded and will be answered soon!").complete().delete().queueAfter(10, TimeUnit.SECONDS);
			msg.addReaction("⛔").queue();
			e.getMessage().delete().queue();
		}
		
		if (e.getChannel().equals(me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getTextChannelById("574250427462320168")) && !e.getAuthor().isBot()
				&& e.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!r")) {
			String[] args = e.getMessage().getContentRaw().split(" ");
			MessageEmbed msg = e.getChannel().getMessageById(args[1]).complete().getEmbeds().get(0);
			Member sender = e.getGuild().getMemberById(msg.getFooter().getText());
			EmbedBuilder eb = null;
			try {
				eb = new EmbedBuilder().setTitle(msg.getFields().get(0).getValue()).setColor(Color.green)
				.setDescription(e.getMessage().getContentRaw().replace(args[0]+" ", "").replace(args[1]+" ", ""))
				.setFooter(msg.getAuthor().getName().split("#")[0], sender.getUser().getEffectiveAvatarUrl());
			} catch (IllegalArgumentException e1) {
				eb = new EmbedBuilder().setDescription("**"+msg.getFields().get(0).getValue()+"**\n"+e.getMessage().getContentRaw().replace(args[0]+" ", "").replace(args[1]+" ", ""))
				.setColor(Color.green)
				.setFooter(msg.getAuthor().getName().split("#")[0], sender.getUser().getEffectiveAvatarUrl());
			}
			eb.setFooter("Answered by "+e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());
			e.getGuild().getTextChannelById("574246600570830853").sendMessage(eb.build()).queue();
			e.getGuild().getTextChannelById("574246600570830853").sendMessage(sender.getAsMention()+" - Your question has been"
					+ " answered!").complete().delete().queueAfter(30, TimeUnit.SECONDS);
			e.getChannel().sendMessage("`#574246600570830853` successfully answered").complete().delete().queueAfter(10, TimeUnit.SECONDS);
			e.getChannel().getMessageById(args[1]).complete().delete().queue();
			e.getMessage().delete().queue();
			
			List<Message> messages = e.getGuild().getTextChannelById("574246600570830853").getHistory().retrievePast(5).complete();
			for (int x = 0 ; x < messages.size() ; x++) {
				if (messages.get(x).getContentRaw().equals(fm)) {
					messages.get(x).delete().queue();
				}
			}
			e.getGuild().getTextChannelById("574246600570830853").sendMessage(fm).queue();
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {

		if (e.getChannel().equals(me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getTextChannelById("574250427462320168")) 
			&& !e.getMember().getUser().isBot() && e.getMember().getUser().getId().equals("159770472567799808") && e.getReactionEmote().getName().equals("⛔")) {
			if (e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getAuthor().getName().contains("Asked by")) {
				e.getChannel().getMessageById(e.getMessageId()).complete().delete().queue();
			}
		}
	}

}
