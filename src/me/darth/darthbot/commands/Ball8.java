package me.darth.darthbot.commands;
 
import java.util.Random;
import java.util.Scanner;
 
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
 
public class Ball8 extends ListenerAdapter {
    //CLASS BY THE ONE AND ONLY GENGAR :)
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot() && !e.getAuthor().equals(e.getJDA().getSelfUser())|| e.getAuthor().isFake()) {
			return;
		}
        String[] args = e.getMessage().getContentRaw().split(" ");
        if (args[0].equalsIgnoreCase("!8ball")) {
            if (args.length > 1) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Magic 8 Ball", null, "https://www.incandescentwaxmelts.com/wp-content/uploads/2017/07/8-BALL-SCENTSY-WARMER-2.png").setColor(660066);
                eb.setDescription("Consulting the oracles.....");
                Message msg = e.getChannel().sendMessage(eb.build()).complete();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                Random rand = new Random();
                int n = rand.nextInt(20);
                n += 1;
                if (n == 1) {
                    eb.setDescription("It is certain.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 2) {
                    eb.setDescription("It is decidedly so.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 3) {
                    eb.setDescription("Without a doubt.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 4) {
                    eb.setDescription("Yes - definitely.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 5) {
                    eb.setDescription("You may rely on it.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 6) {
                    eb.setDescription("As I see it, yes.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 7) {
                    eb.setDescription("Most likely.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 8) {
                    eb.setDescription("Outlook good.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 9) {
                    eb.setDescription("Yes.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 10) {
                    eb.setDescription("Signs point to yes.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 11) {
                    eb.setDescription("Reply hazy, try again.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 12) {
                    eb.setDescription("Ask again later.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 13) {
                    eb.setDescription("Better not tell you now.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 14) {
                    eb.setDescription("Cannot predict now.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 15) {
                    eb.setDescription("Concentrate and ask again.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 16) {
                    eb.setDescription("Don't count on it.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 17) {
                    eb.setDescription("My reply is no.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 18) {
                    eb.setDescription("My sources say no.");
                    msg.editMessage(eb.build()).queue();
                }
                else if (n == 19) {
                    eb.setDescription("Outlook not so good.");
                    msg.editMessage(eb.build()).queue();
                }
                else {
                    eb.setDescription("Very doubtful.");
                    msg.editMessage(eb.build()).queue();
                }
            }
            else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Magic 8 Ball", null, "https://www.incandescentwaxmelts.com/wp-content/uploads/2017/07/8-BALL-SCENTSY-WARMER-2.png").setColor(660066);
                eb.setDescription("An answer requires a question.");
                Message msg = e.getChannel().sendMessage(eb.build()).complete();
           
            }
               
        }
    }
}