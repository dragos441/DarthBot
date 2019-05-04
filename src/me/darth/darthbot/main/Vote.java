package me.darth.darthbot.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Vote extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		if (e.getChannel().getId().equals("569465506369765396") && e.getMessage().getContentRaw().contains("trello.com")) {
			String[] urlsplit = e.getMessage().getContentRaw().split("/");
			for (int x = 0 ; x < urlsplit.length ; x++) {
				if (urlsplit[x].equals("c")) {
					x=x+1;
					e.getChannel().sendMessage("ID: "+urlsplit[x]).queue();
					break;
				}
			}
			String url = "https://api.trello.com/1/card/k7UNFbE0?fields=name&customFieldItems=true&key=68203d3c0219e66cb264d77cad3031de&token=6be68efc8c4017ca24c55ce3ccca7fff22d12d1a24406138dd17045139a0a25a";
			String body = "";
			try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
	            HttpPost request = new HttpPost(url);
	            StringEntity params = new StringEntity(body);
	            request.addHeader("content-type", "application/json");
	            request.setEntity(params);
	            HttpResponse result = httpClient.execute(request);
	            String json = EntityUtils.toString(result.getEntity(), "UTF-8");

	            com.google.gson.Gson gson = new com.google.gson.Gson();
	            int num = gson.fromJson("Number", null);
	            continue here

	            System.out.println(num);

	        } catch (IOException ex) {
	        }

		}
		
	}

}

//https://api.trello.com/1/card/k7UNFbE0?fields=name&customFieldItems=true&key=68203d3c0219e66cb264d77cad3031de&token=6be68efc8c4017ca24c55ce3ccca7fff22d12d1a24406138dd17045139a0a25a