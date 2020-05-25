package me.creepydev.creepybot.services;

import java.awt.Color;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import me.creepydev.creepybot.Main;
import me.creepydev.creepybot.Utils;
import me.creepydev.creepybot.adapter.ServiceType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class Services {

	private TextChannel channel;
	private Map<ServiceType, Message> messages = new HashedMap<ServiceType, Message>();

	@SuppressWarnings("serial")
	private Map<ServiceType, EmbedBuilder> services = new HashedMap<ServiceType, EmbedBuilder>() {
		{
			put(ServiceType.DISCORD, Utils.buildEmbed("Quality discord bot", Color.cyan,
					"Need a Discord bot to manage your server? \nNeed to connect your minecraft server to Discord? Or something else? \nYou are in the right place!",
					"Discord Bot", "Click to start discord bot development", null, "https://toppng.com/uploads/preview/discordbot-bot-discord-11563261320iwm1tpnosh.png",
					null,
					new Field("👨‍💻 Language", "Bot developed in Java with JDA", true),
					new Field("⭐ Quality", "Get a high-class Bot", true),
					new Field("🏎️ Performances", "Combined power and lightweight", true),
					new Field("🔗 MC Bridge", "Link your Minecraft server to your Discord server", true),
					new Field("☁️ Hosting", "Hosting possible directly on your Minecraft server", true)).addBlankField(true));
			put(ServiceType.PLUGIN, Utils.buildEmbed("Quality custom plugins", Color.yellow,
					"You want an additional server functionality but no plugin exists for that? Have yours developed!", "Plugins", "Click to start plugin development",
					null, "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcR7uz6yVNNU1iofsM-WElmy6FWb9-KsGIZ9RBfeNHBrucgrlVgV&usqp=CAU", null,
					new Field("⭐ Quality", "Get a plugin that surpasses perfection", true),
					new Field("🏎️ Performances", "Combined power and lightweight, long live the 20 of tps", true),
					new Field("📜 Bug-free warranty", "A bug ? Fast and free correction", true),
					new Field("👨‍💻 Flex Plugins", "Ability to add functionality several months after development", true),
					new Field("🔌 Advanced Plugins", "Ability to integrate databases, API management, and more", true)).addBlankField(true));
		}
	};

	public Services(String channelID) {
		this.channel = Main.getJDA().getTextChannelById(channelID);
		setServicesMesages();
	}

	public void setServicesMesages() {
		this.channel.getHistory().retrievePast(100).complete().forEach(message -> {
			if (message.getEmbeds().size() != 1) {
				message.delete().queue();
			}

			for (ServiceType type : services.keySet()) {
				EmbedBuilder service = services.get(type);
				if (message.getEmbeds().get(0).getTitle().equals(service.build().getTitle())) {
					message.clearReactions().queue(reac -> {
						message.addReaction("U+1F6D2").queue();
					});

					services.remove(type);
					messages.put(type, message);
					break;
				}
			}
		});

		services.forEach((type, embed) -> {
			Message message = channel.sendMessage(embed.build()).complete();
			message.addReaction("U+1F6D2").queue();

			messages.put(type, message);
		});
	}

	public Map<ServiceType, Message> getMessages() {
		return messages;
	}
	
	public TextChannel getTextChannel() {
		return this.channel;
	}

}
