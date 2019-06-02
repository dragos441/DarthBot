package me.darth.darthbot.retalibot;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GetRoles extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String[] args = event.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!rolepost") && event.getMember().getRoles().contains(event.getGuild().getRoleById("393796810918985728"))) {
			Message msg = event.getMessage();
			msg.addReaction(event.getGuild().getEmoteById("520969750113943564")).queue(); //uploadalert
			msg.addReaction(event.getGuild().getEmoteById("520302822953189376")).queue(); //pc
			msg.addReaction(event.getGuild().getEmoteById("520297851243331594")).queue(); //xbox
			msg.addReaction(event.getGuild().getEmoteById("520302979719495680")).queue(); //ps
			msg.addReaction(event.getGuild().getEmoteById("398122253285588993")).queue(); //gta
			msg.addReaction(event.getGuild().getEmoteById("463478181677236225")).queue(); //fortnite
			msg.addReaction(event.getGuild().getEmoteById("463478305862189061")).queue(); //pubg
			msg.addReaction(event.getGuild().getEmoteById("463478344919547904")).queue(); //rocketleauge
			msg.addReaction(event.getGuild().getEmoteById("520298421324873741")).queue(); //fifa
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		long msg = e.getMessageIdLong();
		TextChannel channel = e.getGuild().getTextChannelById("520296771201794056");
		if (!e.getChannel().equals(channel)) {
			return;
		}
		long targetmsg = e.getChannel().getMessageById("520970311311687700").complete().getIdLong();
		//e.getChannel().sendMessage(""+msg+"\n"+targetmsg).queue();
		
		if (msg == targetmsg) {
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("520969750113943564").getIdLong()) { //uploadalert
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520302822953189376").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("518117432557043723")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("518117432557043723").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("520302822953189376").getIdLong()) { //pc
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520302822953189376").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("447881115827568641")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("447881115827568641").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("520297851243331594").getIdLong()) { //xbox
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520297851243331594").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("447881017265487893")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("447881017265487893").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("520302979719495680").getIdLong()) { //ps
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520302979719495680").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("447881062316638219")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("447881062316638219").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("398122253285588993").getIdLong()) { //gta
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("398122253285588993").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("442426704015785984")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("442426704015785984").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("463478181677236225").getIdLong()) { //fortnite
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("463478181677236225").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("442338161524473868")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("442338161524473868").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("463478305862189061").getIdLong()) { //pubg
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("463478305862189061").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("442338266537132032")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("442338266537132032").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("463478344919547904").getIdLong()) { //rocketleauge
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("463478305862189061").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("442352703918571520")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("442352703918571520").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("520298421324873741").getIdLong()) { //fifa
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("490246223026978827")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("490246223026978827").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("526044640525287444").getIdLong()) { //streamnotifs
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("526104047019622420")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("526104047019622420").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("529625858571370497").getIdLong()) { //nsfw
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("529622747219886100")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("529622747219886100").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("532991329391607818").getIdLong()) { //COD
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("442425490297585674")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("442425490297585674").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("533805471191269386").getIdLong()) { //CSGO
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("444918088404697089")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("444918088404697089").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("532991523743203332").getIdLong()) { //Payday 2
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("458984193339162624")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("458984193339162624").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("533805534881513487").getIdLong()) { //Rainbow 6
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("462722521968672769")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("462722521968672769").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("532990605421314058").getIdLong()) { //Drop Notifs
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("482537738822746132")).reason("#get-roles auto role").reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("482537738822746132").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("547194777741951020").getIdLong()) { //Apex Legends
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("547194942167056394")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("547194942167056394").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("556491003289403403").getIdLong()) { //Server Announcements
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById("556491275969495040")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you joined the **"+e.getGuild().getRoleById("556491275969495040").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			//526044640525287444
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		long msg = e.getMessageIdLong();
		TextChannel channel = e.getGuild().getTextChannelById("520296771201794056");
		if (!e.getChannel().equals(channel)) {
			return;
		}
		long targetmsg = e.getChannel().getMessageById("520970311311687700").complete().getIdLong();
		//e.getChannel().sendMessage(""+msg+"\n"+targetmsg).queue();
		
		if (msg == targetmsg) {
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("520969750113943564").getIdLong()) { //uploadalert
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520302822953189376").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("518117432557043723")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("518117432557043723").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("520302822953189376").getIdLong()) { //pc
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520302822953189376").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("447881115827568641")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("447881115827568641").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("520297851243331594").getIdLong()) { //xbox
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520297851243331594").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("447881017265487893")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("447881017265487893").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("520302979719495680").getIdLong()) { //ps
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520302979719495680").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("447881062316638219")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("447881062316638219").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("398122253285588993").getIdLong()) { //gta
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("398122253285588993").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("442426704015785984")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("442426704015785984").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("463478181677236225").getIdLong()) { //fortnite
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("463478181677236225").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("442338161524473868")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("442338161524473868").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("463478305862189061").getIdLong()) { //pubg
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("463478305862189061").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("442338266537132032")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("442338266537132032").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("463478344919547904").getIdLong()) { //rocketleauge
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("463478305862189061").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("442352703918571520")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("442352703918571520").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("520298421324873741").getIdLong()) { //fifa
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("490246223026978827")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("490246223026978827").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("526044640525287444").getIdLong()) { //streamnotifs
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("526104047019622420")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("526104047019622420").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("529625858571370497").getIdLong()) { //nsfw
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("529622747219886100")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("529622747219886100").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("532991329391607818").getIdLong()) { //COD
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("442425490297585674")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("442425490297585674").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("533805471191269386").getIdLong()) { //CSGO
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("444918088404697089")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("444918088404697089").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("532991523743203332").getIdLong()) { //Payday 2
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("458984193339162624")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("458984193339162624").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("533805534881513487").getIdLong()) { //Rainbow 6
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("462722521968672769")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("462722521968672769").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("532990605421314058").getIdLong()) { //Drop Notifs
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("482537738822746132")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("482537738822746132").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("547194777741951020").getIdLong()) { //Apex Legends
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("547194942167056394")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("547194942167056394").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
			if (e.getReactionEmote().getIdLong() == e.getGuild().getEmoteById("556491003289403403").getIdLong()) { //Server Announcements
				//e.getChannel().sendMessage(""+e.getReactionEmote().getIdLong()+"~"+e.getGuild().getEmoteById("520298421324873741").getIdLong()).reason("#get-roles auto role").queue();;
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getRoleById("556491275969495040")).reason("#get-roles auto role").queue();;
				channel.sendMessage(e.getMember().getAsMention()+", you left the **"+e.getGuild().getRoleById("556491275969495040").getName()+"** role").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
		}
	}
}
