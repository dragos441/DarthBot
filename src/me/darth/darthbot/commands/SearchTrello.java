package me.darth.darthbot.commands;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SearchTrello extends ListenerAdapter {

	public static TreeMap<Integer, String> searchTrello(String[] args) {
		Trello trello = new TrelloImpl("36c6ca5833a315746f43a1d6eee885b4", "dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847", new ApacheHttpClient());
		trello.getBoardCards("EndaJ5Op");
		TreeMap<Integer, String> map = new TreeMap<>(Collections.reverseOrder());
		ArrayList<Integer> added = new ArrayList<Integer>();
		List<Card> cards = trello.getBoardCards("EndaJ5Op");
		for (int x = 0 ; x < cards.size() && added.size() <= 10 ; x++) {
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
				map.put(accuracy, "**"+c.getName()+"** ("+c.getShortUrl()+")");
			}
			
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
