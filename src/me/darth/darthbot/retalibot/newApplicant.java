package me.darth.darthbot.retalibot;

import java.nio.channels.Channel;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class newApplicant extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (e.getGuild().getId().equals("393499439739961366") && args[0].equalsIgnoreCase("!newapp") && e.getMember().getRoles().contains(e.getGuild().getRoleById("557702978455339009"))) {
			if (args.length < 1 || e.getMessage().getMentionedMembers().size() == 0 || e.getMessage().getMentionedMembers().size() > 1) {
				e.getChannel().sendMessage("Invalid Syntax `!newapp <Tagged Applicant>`").queue();
				return;
			}
			e.getMessage().delete().complete();
			Member target = e.getMessage().getMentionedMembers().get(0);
			long cid = -1;
			if (target.getNickname() == null) {
				cid = e.getGuild().getCategoryById("569281781619097600").createTextChannel(target.getEffectiveName()).complete().getIdLong();
			} else {
				cid = e.getGuild().getCategoryById("569281781619097600").createTextChannel(target.getNickname()).complete().getIdLong();
			}
			TextChannel c = e.getGuild().getTextChannelById(cid);
			c.getManager().putPermissionOverride(target, EnumSet.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE), null).complete();
			Calendar cal = Calendar.getInstance();
			String mid = c.sendMessage("**"+target.getAsMention()+" | Stage 1**"
				+ "\nThanks for showing an interest in the Staff team! We will be reviewing your application over the course of the next week, and you will be notified of the status of your application here!"
				+ "\n\n**Stage:**\n**1)** Reviewing your application in detail :stopwatch: \n**2)** Invited for an Interview\n**3)** Attended Interview:\n**4)** Making Final Decision:"
				+ "\n\n**Key**\n:white_check_mark: `Section Passed`\n:stopwatch: `At this stage`\n:no_entry: `Failed Stage`\n**Good Luck!**\n*Any questions? Message <@336469495344529408> below!*").complete().getId();
			Message msg = c.getMessageById(mid).complete();
			msg.pin().complete();
			msg.addReaction("1⃣").complete();
			msg.addReaction("2⃣").complete();
			msg.addReaction("3⃣").complete();
			msg.addReaction("✅").complete();
			msg.addReaction("🚫").complete();
			e.getChannel().sendMessage(":white_check_mark: "+e.getMember().getAsMention()+", applicant has been successfully registered!").complete().delete().queueAfter(30, TimeUnit.SECONDS);
		}
		
		
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (!e.getGuild().getId().equals("393499439739961366")) {
			return;
		}
		if (!e.getUser().isFake() && e.getGuild().getCategoryById("569281781619097600").getTextChannels().contains(e.getChannel()) && !e.getUser().isBot()) {
			if (!e.getMember().getRoles().contains(e.getGuild().getRoleById("557702978455339009"))) {
				e.getReaction().removeReaction(e.getMember().getUser()).queue();
				return;
			}
			Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
			//e.getChannel().sendMessage("`"+e.getReaction().getReactionEmote().getName()+"`").queue();
			if (e.getReaction().getReactionEmote().getName().equals("1⃣")) { 
				msg.editMessage("**"+msg.getMentionedMembers().get(0).getAsMention()+" | Stage 2**"
					+ "\nThanks for showing an interest in the Staff team! We will be reviewing your application over the course of the next week, and you will be notified of the status of your application here!"
					+ "\n\n**Stage:**\n**1)** Reviewing your application in detail :white_check_mark: \n**2)** Invited for an Interview :stopwatch:\n**3)** Attended Interview:\n**4)** Making Final Decision:"
					+ "\n\n-**Key**\n:white_check_mark: `Section Passed`\n:stopwatch: `At this stage`\n:no_entry: `Failed Stage`\n**Good Luck!**").queue();
			}
			if (e.getReaction().getReactionEmote().getName().equals("2⃣")) { 
				msg.editMessage("**"+msg.getMentionedMembers().get(0).getAsMention()+" | Stage 3**"
					+ "\nThanks for showing an interest in the Staff team! We will be reviewing your application over the course of the next week, and you will be notified of the status of your application here!"
					+ "\n\n**Stage:**\n**1)** Reviewing your application in detail :white_check_mark: \n**2)** Invited for an Interview :white_check_mark:\n**3)** Attended Interview: :stopwatch:\n**4)** Making Final Decision:"
					+ "\n\n-**Key**\n:white_check_mark: `Section Passed`\n:stopwatch: `At this stage`\n:no_entry: `Failed Stage`\n**Good Luck!**").queue();
			}
			if (e.getReaction().getReactionEmote().getName().equals("3⃣")) { 
				msg.editMessage("**"+msg.getMentionedMembers().get(0).getAsMention()+" | Stage 4**"
					+ "\nThanks for showing an interest in the Staff team! We will be reviewing your application over the course of the next week, and you will be notified of the status of your application here!"
					+ "\n\n**Stage:**\n**1)** Reviewing your application in detail :white_check_mark: \n**2)** Invited for an Interview :white_check_mark:\n**3)** Attended Interview: :white_check_mark:\n**4)** Making Final Decision: :stopwatch:"
					+ "\n\n-**Key**\n:white_check_mark: `Section Passed`\n:stopwatch: `At this stage`\n:no_entry: `Failed Stage`\n**Good Luck!**").queue();
			}
			if (e.getReaction().getReactionEmote().getName().equals("✅")) {
				msg.editMessage("**"+msg.getMentionedMembers().get(0).getAsMention()+" | Accepted ✅**"
						+ "\nThanks for showing an interest in the Staff team! We will be reviewing your application over the course of the next week, and you will be notified of the status of your application here!"
						+ "\n\n**Stage:**\n**1)** Reviewing your application in detail :white_check_mark: \n**2)** Invited for an Interview :white_check_mark:\n**3)** Attended Interview: :white_check_mark:\n**4)** Making Final Decision: :white_check_mark: **ACCEPTED!**"
						+ "\n\n-**Key**\n:white_check_mark: `Section Passed`\n:stopwatch: `At this stage`\n:no_entry: `Failed Stage`\n**Good Luck!**").queue();
				e.getGuild().getController().addSingleRoleToMember(msg.getMentionedMembers().get(0), e.getGuild().getRoleById("489851380279017513")).queue(); //st
				e.getGuild().getController().addSingleRoleToMember(msg.getMentionedMembers().get(0), e.getGuild().getRoleById("399332438079176715")).queue(); //ds
				e.getGuild().getController().addSingleRoleToMember(msg.getMentionedMembers().get(0), e.getGuild().getRoleById("432562748484878357")).queue(); //h
			}
			if (e.getReaction().getReactionEmote().getName().equals("🚫")) {
				msg.editMessage("**"+msg.getMentionedMembers().get(0).getAsMention()+" | Denied 🚫**"
						+ "\nThanks for showing an interest in the Staff team!\n\n**Unfortunately, we don't believe you're ready to join just yet.**"
						+ " <@!336469495344529408> will reach out to you shortly with details as to why you have been denied, and how to improve next time."
						+ "\n\n**You may re-apply in 2 weeks\n`Please acknowledge you've seen this in chat below so we can remove the channel`**").queue();
			}
		}
		
	}

}
