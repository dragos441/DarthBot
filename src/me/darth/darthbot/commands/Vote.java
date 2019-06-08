package me.darth.darthbot.commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Vote extends ListenerAdapter {
	
	static int lastUpdated = -1;
	
	public boolean canVote(String cardID, long userID) {
		URL url;
		try {
			url = new URL("https://api.trello.com/1/card/"+cardID+"/?key=36c6ca5833a315746f43a1d6eee885b4&token=dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			if (content.toString().contains(userID+"")) {
				return false;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public int getVotes(String cardID) {
		URL url;
		try {
			url = new URL("https://trello.com/1/card/"+cardID+"?fields=name&customFieldItems=true&key=68203d3c0219e66cb264d77cad3031de&token=6be68efc8c4017ca24c55ce3ccca7fff22d12d1a24406138dd17045139a0a25a&format=json");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			JSONObject obj = new JSONObject(content.toString());
			JSONArray arr = obj.getJSONArray("customFieldItems");
			String value = null;
			for (int x = 0 ; x < arr.length() ; x++) {
				value = arr.getJSONObject(x).get("value").toString();
			}
			int votes = -1;
			try {
				votes = Integer.parseInt(value.replace("{", "").replace("}", "").replace("\"", "").replace(":", "").replace("number", ""));
			} catch (NullPointerException e1) {
				votes = 0;
			}
			return votes;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return -99999;
	}
	
	public String cardID(String[] args, String[] split) {
		String cardID = null;
		for (int x = 0 ; x < split.length ; x++) {
			if (split[x].equals("c")) {
				x=x+1;
				cardID=split[x];
				//e.getChannel().sendMessage("ID: "+urlsplit[x]).queue();
				return cardID;
			}
		}
		
		return null;
	}
	@SuppressWarnings("deprecation")
	public boolean addVoted(String cardID, Long userID) {
		URL url;
		try {
			url = new URL("https://api.trello.com/1/cards/"+cardID+"?fields=all&attachments=false&attachment_fields=all"
					+ "&members=false&membersVoted=false&checkItemStates=false&checklists=none&checklist_fields=all&board=false&list=false"
					+ "&pluginData=false&stickers=false&sticker_fields=all&customFieldItems=false&key=36c6ca5833a315746f43a1d6eee885b4"
					+ "&token=dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close(); 
			JSONObject obj = new JSONObject(content.toString());
			String desc = obj.getString("desc").toString();
			String list = obj.getString("idList").toString();
			if (!list.equals("5cbc6c5a24c96885903fde3e")) {
				return false;
			}
			//System.out.print(list);
			desc=URLEncoder.encode(desc+","+userID);
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			try {
			    HttpPut request = new HttpPut("https://api.trello.com/1/cards/"+cardID+"?desc="+desc
			    		+ "&key=36c6ca5833a315746f43a1d6eee885b4&token=dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847");
			    request.addHeader("content-type", "application/json");
			    httpClient.execute(request);
			    //System.out.print(request.toString());
			    
			// handle response here...
			} catch (Exception ex) {
			    ex.printStackTrace();
			} finally {
			    try {
					httpClient.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public static void listSort() {
		Trello trello = new TrelloImpl("36c6ca5833a315746f43a1d6eee885b4", "dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847", new ApacheHttpClient());
		List<Card> cards = trello.getListCards("5cbc6c5a24c96885903fde3e");
		//System.out.print("c: "+cards.size());
		TreeMap<Integer, String> map = new TreeMap<>(Collections.reverseOrder());
		ArrayList<Integer> added = new ArrayList<>();
		for (int x = 0 ; x < cards.size() ; x++) {
			URL url;
			try {
				url = new URL("https://trello.com/1/card/"+cards.get(x).getId()+"?fields=all&customFieldItems=true&key=68203d3c0219e66cb264d77cad3031de"
						+ "&token=6be68efc8c4017ca24c55ce3ccca7fff22d12d1a24406138dd17045139a0a25a&format=json");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}
				in.close();
				JSONObject obj = new JSONObject(content.toString());
				JSONArray arr = obj.getJSONArray("customFieldItems");
				String value = null;
				for (int i = 0 ; i < arr.length() ; i++) {
					value = arr.getJSONObject(i).get("value").toString();
				}
				int votes = -1;
				try {
					votes = Integer.parseInt(value.replace("{", "").replace("}", "").replace("\"", "").replace(":", "").replace("number", ""));
				} catch (NullPointerException e1) {
					votes = 0;
				}
				votes=votes*1000;
				if (added.contains(votes)) {
					while (added.contains(votes)) {
						votes=votes+1;
					}
				}
				map.put(votes, cards.get(x).getId());
				added.add(votes);
				//System.out.println(cards.get(x).getUrl()+" - "+votes);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while (!map.isEmpty()) {
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			try {
			    HttpPut request = new HttpPut("https://api.trello.com/1/cards/"+map.firstEntry().getValue()+"?pos=bottom"//+map.firstKey()
						+ "&key=68203d3c0219e66cb264d77cad3031de&token=6be68efc8c4017ca24c55ce3ccca7fff22d12d1a24406138dd17045139a0a25a&format=json");
			    request.addHeader("content-type", "application/json");
			    httpClient.execute(request);
			    //System.out.print(request.toString());
			// handle response here...
			} catch (Exception ex) {
			    ex.printStackTrace();
			} finally {
			    try {
					httpClient.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			//System.out.print("\nSET "+map.firstEntry().getValue()+" TO POS "+pos);
			map.remove(map.firstKey());
		}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (!e.getGuild().getId().equals("568849490425937940")) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!upvote") || args[0].equalsIgnoreCase("!downvote")) {
			String cardID = cardID(e.getMessage().getContentRaw().split(" "), e.getMessage().getContentRaw().split("/"));
			int votes = getVotes(cardID);
			if (args[0].equalsIgnoreCase("!upvote")) {
				if (e.getMember().getRoles().contains(e.getGuild().getRoleById("582164371455606784"))) {
					votes=votes+3;
				} else {
					votes=votes+1;
				}
			} else {
				if (e.getMember().getRoles().contains(e.getGuild().getRoleById("582164371455606784"))) {
					votes=votes-3;
				} else {
					votes=votes-1;
				}
			}
			if (votes == -99999) {
				return;
			}
			if (canVote(cardID, e.getAuthor().getIdLong())) {
				boolean done = addVoted(cardID, e.getMember().getUser().getIdLong());
				if (!done) {
					e.getChannel().sendMessage("That is not a valid suggestion!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
					return;
				}
				CloseableHttpClient httpClient = HttpClientBuilder.create().build();
				try {
				    HttpPut request = new HttpPut("https://api.trello.com/1/card/"+cardID+"/customField/5cd078d3e7aeba2e3365ad81/item"
				    		+ "?key=36c6ca5833a315746f43a1d6eee885b4&token=dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847");
				    StringEntity params = new StringEntity("{ \"value\": { \"number\": \""+votes+"\" } }");
				    request.addHeader("content-type", "application/json");
				    request.setEntity(params);
				    httpClient.execute(request);
				    //System.out.print(request.toString());
				    
				// handle response here...
				} catch (Exception ex) {
				    ex.printStackTrace();
				} finally {
				    try {
						httpClient.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				e.getChannel().sendMessage(e.getMember().getAsMention()+", successfully added vote!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
				//listSort();
			} else {
				e.getChannel().sendMessage(e.getMember().getAsMention()+", you have already voted on this suggestion!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
			}
		}
		if (e.getMessage().getContentRaw().contains("trello.com")) {
			String[] urlsplit = e.getMessage().getContentRaw().split("/");
			String cardID = null;
			String link = null;
			for (int x = 0 ; x < args.length ; x++) {
				if (args[x].contains("trello.com")) {
					link = args[x].replace(",", "");
				}
			}
			for (int x = 0 ; x < urlsplit.length ; x++) {
				if (urlsplit[x].equals("c")) {
					x=x+1;
					cardID=urlsplit[x].split("/")[0];
					//e.getChannel().sendMessage("ID: "+urlsplit[x]).queue();
					break;
				}
			}
			if (cardID == null) {
				return;
			}
			URL url;
			try {
				url = new URL("https://trello.com/1/card/"+cardID+"?fields=all&customFieldItems=true&key=68203d3c0219e66cb264d77cad3031de&token=6be68efc8c4017ca24c55ce3ccca7fff22d12d1a24406138dd17045139a0a25a&format=json");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}
				in.close();
				JSONObject obj = new JSONObject(content.toString());
				JSONArray arr = obj.getJSONArray("customFieldItems");
				String name = obj.getString("name").toString();
				String desc = null;
				Member sm = e.getGuild().getMemberById(obj.getString("desc").toString().split("\n")[6].replace("> Submitter ID: `", "").replace("`", ""));
				desc = obj.getString("desc").toString().split("\n")[1];
				String value = null;
				for (int x = 0 ; x < arr.length() ; x++) {
					value = arr.getJSONObject(x).get("value").toString();
				}
				int votes = -1;
				try {
					votes = Integer.parseInt(value.replace("{", "").replace("}", "").replace("\"", "").replace(":", "").replace("number", ""));
				} catch (NullPointerException e1) {
					votes = 0;
				}
				EmbedBuilder eb = new EmbedBuilder().addField("Votes", ""+votes, true).setFooter(cardID, null).setDescription(desc);
				if (name.toString().length() < 256) {
					eb.setTitle(name, link);
				} else {
					String shortname = "";
					for (int x = 0 ; x < 256 ; x++) {
						shortname=shortname+name.toCharArray()[x];
					}
					eb.setTitle(shortname, link);
				}
				if (sm == null) {
					eb.setAuthor(obj.getString("desc").toString().split("\n")[5].replace("> ", "").replace("`", "").replace("`", ""), link, me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getIconUrl());
				} else {
					eb.setAuthor("Submitted by "+sm.getUser().getName()+"#"+sm.getUser().getDiscriminator(), link, sm.getUser().getEffectiveAvatarUrl());
				}
				if (votes < 0) {
					eb.setColor(Color.red);
				} else if (votes >= 0 && votes < 3) {
					eb.setColor(Color.yellow);
				} else {
					eb.setColor(Color.green);
				}
			
				Message msg = e.getChannel().sendMessage(eb.build()).complete();
				msg.addReaction(me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getEmoteById("574532922321797120")).queue();
				msg.addReaction(me.darth.darthbot.main.Main.sm.getGuildById("568849490425937940").getEmoteById("574532942437810177")).queue();
				if (args.length > 1) {
					e.getMessage().delete().queue();
				}
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


		}
		
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
		try {
			if (e.getReactionEmote().getId().equals("574532922321797120") || e.getReactionEmote().getId().equals("574532942437810177")) {
				String cardID = msg.getEmbeds().get(0).getFooter().getText();
				Trello trello = new TrelloImpl("36c6ca5833a315746f43a1d6eee885b4", "dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847", new ApacheHttpClient());
				if (trello.getCard(cardID).isClosed() && e.getChannel().getMessageById(e.getMessageId()).complete().getContentRaw().split(" ").length == 1) {
					msg.delete().queue();
				}
				int votes = getVotes(cardID);
				if (votes == -99999) {
					return;
				}
				if (e.getReactionEmote().getId().equals("574532922321797120")) {
					votes=votes+1;
				} else {
					votes=votes-1;
				}
				if (canVote(cardID, e.getMember().getUser().getIdLong())) {
					boolean done = addVoted(cardID, e.getMember().getUser().getIdLong());
					if (!done) {
						//e.getChannel().sendMessage("That is not a valid suggestion!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
						return;
					}
					CloseableHttpClient httpClient = HttpClientBuilder.create().build();
					try {
					    HttpPut request = new HttpPut("https://api.trello.com/1/card/"+cardID+"/customField/5cd078d3e7aeba2e3365ad81/item"
					    		+ "?key=36c6ca5833a315746f43a1d6eee885b4&token=dda51a3550614cf455f617c42d615a28c7b67bb4c96b225fa4ef82a08d7b7847");
					    StringEntity params = new StringEntity("{ \"value\": { \"number\": \""+votes+"\" } }");
					    request.addHeader("content-type", "application/json");
					    request.setEntity(params);
					    httpClient.execute(request);
					    //System.out.print(request.toString());
					    
					// handle response here...
					} catch (Exception ex) {
					    ex.printStackTrace();
					} finally {
					    try {
							httpClient.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					//e.getChannel().sendMessage(e.getMember().getAsMention()+", successfully added vote!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
					EmbedBuilder eb = new EmbedBuilder(msg.getEmbeds().get(0));
					eb.getFields().clear();
					eb.addField("Votes", ""+votes, false);
					if (votes < 0) {
						eb.setColor(Color.red);
					} else if (votes >= 0 && votes < 5) {
						eb.setColor(Color.orange); 
					} else {
						eb.setColor(Color.green);
					}
					msg.editMessage(eb.build()).queue();
					//listSort();
					
				} else {
					//e.getChannel().sendMessage(e.getMember().getAsMention()+", you have already voted on this suggestion!").complete().delete().queueAfter(15, TimeUnit.SECONDS);
				}
			}
		} catch (NullPointerException | IndexOutOfBoundsException e2) {}
	}
	
	

}

//https://api.trello.com/1/card/k7UNFbE0?fields=name&customFieldItems=true&key=68203d3c0219e66cb264d77cad3031de&token=6be68efc8c4017ca24c55ce3ccca7fff22d12d1a24406138dd17045139a0a25a