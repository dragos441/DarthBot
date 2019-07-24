package me.darth.darthbot.music;

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

import org.json.JSONArray;
import org.json.JSONObject;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MusicCommand extends ListenerAdapter {
	
	public static MusicManager manager = new MusicManager();
	
	@SuppressWarnings("deprecation")
	public static String ytsearch(String search, TextChannel channel) {
		URL url;
		try {
			url = new URL("https://www.googleapis.com/youtube/v3/search?part=snippet&q="+URLEncoder.encode(search)+"&key=AIzaSyBG13lW2pi41WjHORZNdFxMELhgEBBE0Fk");
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
			JSONArray arr = obj.getJSONArray("items");
			for (int x = 0 ; x < arr.length() ; x++) {
				if (!arr.getJSONObject(x).get("id").toString().contains("playlistId")) {
					return "https://www.youtube.com/watch?v="+arr.getJSONObject(x).get("id").toString()
							.replace("{\"kind\":\"youtube#video\",\"videoId\":\"", "").replace("\"}", "");
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
		return null;
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		if (e.getChannelLeft().getMembers().size() <= 1) {
			if (manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack() != null) {
				manager.getPlayer(e.getGuild()).getAudioPlayer().setPaused(true);
			}
		}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if(e.getGuild() == null || e.getAuthor().isFake()) {
			return;
		}
		EmbedBuilder eb = new EmbedBuilder().setAuthor("Music", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl()).setColor(Color.orange);
		VoiceChannel voiceChannel = e.getGuild().getMember(e.getMember().getUser()).getVoiceState().getChannel();
		if (args[0].equalsIgnoreCase("!join") || args[0].equalsIgnoreCase("!summon")) {
			if(voiceChannel == null){
				eb.setDescription("Please join a voice channel!");
				e.getChannel().sendMessage(eb.build()).queue();
				return;
			}
			e.getGuild().getAudioManager().openAudioConnection(voiceChannel);
			eb.setDescription("I have joined your current Voice Channel!");
			e.getChannel().sendMessage(eb.build()).queue();
		}
		if (args[0].equalsIgnoreCase("!play") || args[0].equalsIgnoreCase("!queue") || args[0].equalsIgnoreCase("!q")) {
			try {
				String t = args[1];
				t=t+"";
				if(voiceChannel == null){
					eb.setDescription("Please join a voice channel!");
					e.getChannel().sendMessage(eb.build()).queue();
					return;
				}
				e.getGuild().getAudioManager().openAudioConnection(voiceChannel);
			} catch (ArrayIndexOutOfBoundsException e1) {
				if (args[0].equalsIgnoreCase("!queue") || args[0].equalsIgnoreCase("!q")) {
					eb.setColor(Color.yellow);
					eb.setAuthor("Music - Queue", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
					ArrayList<AudioTrack> list = new ArrayList<AudioTrack>(manager.getPlayer(e.getGuild()).getListener().getTracks());
					list.add(0, manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack());
					int x = 0;
					try {
						for(x = 0 ; x < list.size() && x < 10 ; x++) {
							AudioTrack t = list.get(x);
							long s = t.getDuration() / 1000;
							int m = 0;
							while (s >= 60) {
								s=s-60;
								m=m+1;
							}
							if (s < 10) {
								s=s*10;
							}
							eb.addField("#"+x+" - "+t.getInfo().title, t.getInfo().uri+" `"+m+":"+s+"`", false);
						}
					} catch (NullPointerException e2) {
						e.getChannel().sendMessage("The queue is empty!").queue();
						return;
					}
					eb.setFooter("("+x+"/"+list.size()+")", null);
					Message msg = e.getChannel().sendMessage(eb.build()).complete();
					if (list.size() > 10) {
						msg.addReaction("⬅").queue();
						msg.addReaction("➡").queue();
					}
					return;
				} else {
					e.getChannel().sendMessage("Invalid Syntax: `!play <song>` / `!queue (song)`").queue();
					return;
				}
			}
			if (e.getMessage().getContentRaw().contains("?list=") || e.getMessage().getContentRaw().contains("&list=")) {
				e.getChannel().sendMessage(":no_entry: Adding playlists is currently unavailable!").queue();
				return;
			}
			ArrayList<AudioTrack> list = new ArrayList<AudioTrack>(manager.getPlayer(e.getGuild()).getListener().getTracks());
			long time = 0;
			for (int x = 0 ; x < list.size() ; x++) {
				time = time + list.get(x).getDuration();
			}
			time = time / 1000;
			if (time > 36000) {
				e.getChannel().sendMessage(":no_entry: The queue is already over 10 hours long!").queue();
				return;
			}
			
			if (!e.getMessage().getContentRaw().toLowerCase().contains("youtube.com")) {
				manager.loadTrack(e.getChannel(), ytsearch(e.getMessage().getContentRaw().replace(args[0]+" ", ""), e.getChannel()), e.getJDA().getSelfUser().getEffectiveAvatarUrl(), e.getGuild());
			} else {
				manager.loadTrack(e.getChannel(), e.getMessage().getContentRaw().replace(args[0]+" ", ""), e.getJDA().getSelfUser().getEffectiveAvatarUrl(), e.getGuild());
			}
			manager.getPlayer(e.getGuild()).getAudioPlayer().setPaused(false);
		}
		if (args[0].equalsIgnoreCase("!stop") || args[0].equalsIgnoreCase("!leave")) {
			manager.getPlayer(e.getGuild()).getListener().getTracks().clear();
			manager.getPlayer(e.getGuild()).getListener().nextTrack();
			e.getGuild().getAudioManager().closeAudioConnection();
			eb.setDescription("Stopped playing and left!");
			e.getChannel().sendMessage(eb.build()).queue();
		}
		if (args[0].equalsIgnoreCase("!pause") || args[0].equalsIgnoreCase("!unpause") || args[0].equalsIgnoreCase("!resume")) {
			if (manager.getPlayer(e.getGuild()).getAudioPlayer().isPaused()) {
				manager.getPlayer(e.getGuild()).getAudioPlayer().setPaused(false);
				eb.setDescription("Player unpaused!");
				
			} else {
				manager.getPlayer(e.getGuild()).getAudioPlayer().setPaused(true);
				eb.setDescription("Player paused!");
			}
			e.getChannel().sendMessage(eb.build()).queue();
		}
		if (args[0].equalsIgnoreCase("!skip")) {
			if(!e.getGuild().getAudioManager().isConnected() && !e.getGuild().getAudioManager().isAttemptingToConnect()){
				eb.setDescription("There is nothing currently playing!");
				e.getChannel().sendMessage(eb.build()).queue();
				return;
			}
			String prevtrack = manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title;
			manager.getPlayer(e.getGuild()).skipTrack();
			eb.setDescription("Track **"+prevtrack+"** has been skipped, now playing **"
			+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title+"**");
			e.getChannel().sendMessage(eb.build()).queue();
		}
		if (args[0].equalsIgnoreCase("!clearqueue")) {
			if (manager.getPlayer(e.getGuild()).getListener().getTracks().isEmpty()){
				eb.setDescription("The queue is empty!");
				e.getChannel().sendMessage(eb.build()).queue();
				return;
			}
			manager.getPlayer(e.getGuild()).getListener().getTracks().clear();
			eb.setDescription("The song queue has been cleared!");
			e.getChannel().sendMessage(eb.build()).queue();
		}
		if (args[0].equalsIgnoreCase("!playing") || args[0].equalsIgnoreCase("!nowplaying") || args[0].equalsIgnoreCase("!track")) {
			eb.setDescription("Currently playing **"+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title+"**");
			e.getChannel().sendMessage(eb.build()).queue();
		}
		
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getReactionEmote().getName().equals("➡") && !e.getMember().getUser().isBot()) { 
			try {
				Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
				if (e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getAuthor().getName().equals("Music - Queue")) {
					EmbedBuilder eb = new EmbedBuilder().setColor(Color.yellow).setAuthor("Music - Queue", null, e.getGuild().getIconUrl());
					int start = Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText().replace("(", "").replace(")", "").split("/")[0]);
					ArrayList<AudioTrack> list = new ArrayList<AudioTrack>(manager.getPlayer(e.getGuild()).getListener().getTracks());
					if (manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack() != null) {
						eb.addField("Currently Playing", "**"+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title+"** "
					+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().uri, false);
					}
					int x = start;
					int end = start + 10;
					for(x = start ; x < list.size() && x <= end; x++) {
						AudioTrack t = list.get(x);
						long s = t.getDuration() / 1000;
						int m = 0;
						while (s > 60) {
							s=s-60;
							m=m+1;
						}
						eb.addField("#"+x+" - "+t.getInfo().title, t.getInfo().uri+" `"+m+":"+s+"`", false);
					}
					eb.setFooter("("+x+"/"+list.size()+")", null);
					msg.editMessage(eb.build()).queue();
				}
			} catch (NullPointerException e1) {return;}
		} else if (e.getReactionEmote().getName().equals("⬅") && !e.getMember().getUser().isBot()) { 
			try {
				Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
				if (e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getAuthor().getName().equals("Music - Queue")) {
					EmbedBuilder eb = new EmbedBuilder().setColor(Color.yellow).setAuthor("Music - Queue", null, e.getGuild().getIconUrl());
					int start = Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText().replace("(", "").replace(")", "").split("/")[0]);
					ArrayList<AudioTrack> list = new ArrayList<AudioTrack>(manager.getPlayer(e.getGuild()).getListener().getTracks());
					if (manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack() != null) {
						eb.addField("Currently Playing", "**"+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title+"** "
					+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().uri, false);
					}
					int x = start;
					int end = start - 10;
					for(x = start ; x >= end && x > 0; x--) {
						if (x < list.size()) {
							AudioTrack t = list.get(x);
							long s = t.getDuration() / 1000;
							int m = 0;
							while (s > 60) {
								s=s-60;
								m=m+1;
							}
							eb.addField("#"+x+" - "+t.getInfo().title, t.getInfo().uri+" `"+m+":"+s+"`", false);
						}
					}
					eb.setFooter("("+x+"/"+list.size()+")", null);
					msg.editMessage(eb.build()).queue();
				}
			} catch (NullPointerException e1) {return;}
		}
	}
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		if (e.getReactionEmote().getName().equals("➡") && !e.getMember().getUser().isBot()) { 
			try {
				Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
				if (e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getAuthor().getName().equals("Music - Queue")) {
					EmbedBuilder eb = new EmbedBuilder().setColor(Color.yellow).setAuthor("Music - Queue", null, e.getGuild().getIconUrl());
					int start = Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText().replace("(", "").replace(")", "").split("/")[0]);
					ArrayList<AudioTrack> list = new ArrayList<AudioTrack>(manager.getPlayer(e.getGuild()).getListener().getTracks());
					if (manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack() != null) {
						eb.addField("Currently Playing", "**"+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title+"** "
					+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().uri, false);
					}
					int x = start;
					int end = start + 10;
					for(x = start ; x < list.size() && x <= end; x++) {
						AudioTrack t = list.get(x);
						long s = t.getDuration() / 1000;
						int m = 0;
						while (s > 60) {
							s=s-60;
							m=m+1;
						}
						eb.addField("#"+x+" - "+t.getInfo().title, t.getInfo().uri+" `"+m+":"+s+"`", false);
					}
					eb.setFooter("("+x+"/"+list.size()+")", null);
					msg.editMessage(eb.build()).queue();
				}
			} catch (NullPointerException e1) {return;}
		} else if (e.getReactionEmote().getName().equals("⬅") && !e.getMember().getUser().isBot()) { 
			try {
				Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
				if (e.getChannel().getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getAuthor().getName().equals("Music - Queue")) {
					EmbedBuilder eb = new EmbedBuilder().setColor(Color.yellow).setAuthor("Music - Queue", null, e.getGuild().getIconUrl());
					int start = Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText().replace("(", "").replace(")", "").split("/")[0]);
					ArrayList<AudioTrack> list = new ArrayList<AudioTrack>(manager.getPlayer(e.getGuild()).getListener().getTracks());
					if (manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack() != null) {
						eb.addField("Currently Playing", "**"+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().title+"** "
					+manager.getPlayer(e.getGuild()).getAudioPlayer().getPlayingTrack().getInfo().uri, false);
					}
					int x = start;
					int end = start - 10;
					for(x = start ; x >= end && x > 0; x--) {
						AudioTrack t = list.get(x);
						long s = t.getDuration() / 1000;
						int m = 0;
						while (s > 60) {
							s=s-60;
							m=m+1;
						}
						eb.addField("#"+x+" - "+t.getInfo().title, t.getInfo().uri+" `"+m+":"+s+"`", false);
					}
					eb.setFooter("("+x+"/"+list.size()+")", null);
					msg.editMessage(eb.build()).queue();
				}
			} catch (NullPointerException e1) {return;}
		}
	}
}