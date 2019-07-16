package me.darth.darthbot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import org.trello4j.Trello;
import org.trello4j.TrelloImpl;
import org.trello4j.model.Card;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SearchTrello extends ListenerAdapter {

	public static TreeMap<Integer, String> searchTrello(String[] args) {
		TreeMap<Integer, String> map = new TreeMap<>(Collections.reverseOrder());
		Trello trello = new TrelloImpl("36c6ca5833a315746f43a1d6eee885b4", "dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847");
		ArrayList<Integer> added = new ArrayList<Integer>();
		List<Card> cards = trello.getCardsByBoard("5cbc6c2d584c75132d2d08cb");
		for (int x = 0 ; x < cards.size() && added.size() <= 10 ; x++) {
			try {
				int accuracy = 0;
				Card c = cards.get(x);
				for (int i = 1 ; i < args.length ; i++) {
					if (c.getName().toLowerCase().contains(args[i].toLowerCase())) {
						accuracy=accuracy+1;
						
					}
				}
				accuracy=accuracy * 100 / (args.length - 1);
				if (accuracy > 0) {
					while (added.contains(accuracy)) {
						accuracy=accuracy-1;
					}
					added.add(accuracy);
					map.put(accuracy, "**"+c.getName()+"** ("+c.getUrl()+")");
				}
			} catch (ArithmeticException e1) {}
		}
		return map;
	
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		
		if (args[0].equalsIgnoreCase("!search")) {
			TreeMap<Integer, String> map = searchTrello(args);    
			EmbedBuilder eb = new EmbedBuilder().setAuthor("Search", null, e.getGuild().getIconUrl());
			int x = 0;
			TreeMap<Integer, String> smap = map;
			while (!map.isEmpty() && x < 5) {
				eb.addField("Accuracy: "+map.firstKey()+"%", map.firstEntry().getValue(), false);
				map.remove(map.firstKey());
			}
			e.getChannel().sendMessage(smap+"").embed(eb.build()).queue();
		}
	}

}
