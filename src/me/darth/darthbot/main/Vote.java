package me.darth.darthbot.main;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParser;
import com.mysql.fabric.Response;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Vote extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!upvote")) {
			URL url;
				String command = ("curl -X PUT -H \"Content-Type: application/json\" \\\r\n" + 
						"https://api.trello.com/1/card/eXxPgT2F/customField/5ccf12c164f02142f0cfc823/item \\\r\n" + 
						"-d '{\r\n" + 
						"  \"idValue\": \"99\",\r\n" + 
						"  \"key\": \"68203d3c0219e66cb264d77cad3031de\",\r\n" + 
						"  \"token\": \"6be68efc8c4017ca24c55ce3ccca7fff22d12d1a24406138dd17045139a0a25a\"\r\n" + 
						"}'");
				ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
				try {
					Process process = processBuilder.start();
					InputStream inputStream = process.getInputStream();
					process.destroy();
					int exitCode = process.exitValue();
					System.out.print(exitCode);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					Process process = Runtime.getRuntime().exec(command);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
		if (e.getChannel().getId().equals("569465506369765396") && e.getMessage().getContentRaw().contains("trello.com")) {
			String[] urlsplit = e.getMessage().getContentRaw().split("/");
			String cardID = null;
			String link = null;
			for (int x = 0 ; x < args.length ; x++) {
				if (args[x].contains("trello.com")) {
					link = args[x];
				}
			}
			for (int x = 0 ; x < urlsplit.length ; x++) {
				if (urlsplit[x].equals("c")) {
					x=x+1;
					cardID=urlsplit[x];
					//e.getChannel().sendMessage("ID: "+urlsplit[x]).queue();
					break;
				}
			}
			
			if (cardID == null) {
				return;
			}
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
				JSONArray arrname = obj.getJSONArray("customFieldItems");
				String name = obj.getString("name").toString();
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
				EmbedBuilder eb = new EmbedBuilder().setTitle(name, link)
						.addField("Votes", ""+votes, true).setFooter("React with +1 or -1 to vote for this suggestions priority!", null);
				if (votes < 0) {
					eb.setColor(Color.red);
				} else if (votes > 0 && votes < 3) {
					eb.setColor(Color.yellow);
				} else {
					eb.setColor(Color.green);
				}
				Message msg = e.getChannel().sendMessage("Reactions don't work yet because darth's a shit dev").embed(eb.build()).complete();
				msg.addReaction(me.darth.darthbot.main.Main.jda.getGuildById("568849490425937940").getEmoteById("574532922321797120")).queue();
				msg.addReaction(me.darth.darthbot.main.Main.jda.getGuildById("568849490425937940").getEmoteById("574532942437810177")).queue();
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

}

//https://api.trello.com/1/card/k7UNFbE0?fields=name&customFieldItems=true&key=68203d3c0219e66cb264d77cad3031de&token=6be68efc8c4017ca24c55ce3ccca7fff22d12d1a24406138dd17045139a0a25a