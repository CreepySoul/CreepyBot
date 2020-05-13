package me.creepydev.creepybot.messages;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import me.creepydev.creepybot.Main;
import me.creepydev.creepybot.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class Services {

	private TextChannel channel;
	private List<EmbedBuilder> services = new ArrayList<EmbedBuilder>() {
		{
			add(Utils.buildEmbed("Quality discord bot", Color.cyan,
					"Need a Discord bot to manage your server? \nNeed to connect your minecraft server to Discord? Or something else? \nYou are in the right place!",
					"Discord Bot", "Click to start discord bot development", null, "https://toppng.com/uploads/preview/discordbot-bot-discord-11563261320iwm1tpnosh.png",
					new Field("test", "test value", true)));
			add(Utils.buildEmbed("Quality custom plugins", Color.yellow,
					"You want an additional server functionality but no plugin exists for that? Have yours developed!", "Plugins", "Click to start plugin development",
					null, "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcR7uz6yVNNU1iofsM-WElmy6FWb9-KsGIZ9RBfeNHBrucgrlVgV&usqp=CAU",
					new Field("test", "test value", true)));
		}
	};

	public Services(String channelID) {
		channel = Main.getJDA().getTextChannelById(channelID);
		setServicesMesages();
	}

	public void setServicesMesages() {
		channel.getHistory().retrievePast(100).complete().forEach(message -> {
			if (message.getEmbeds().size() != 1) {
				message.delete().queue();
			}

			for (EmbedBuilder service : services) {
				if (message.getEmbeds().get(0).getTitle().equals(service.build().getTitle())) {
					services.remove(service);
					message.clearReactions().queue();
					message.addReaction("U+1F6D2").queue();
					break;
				}
			}
		});

		for (EmbedBuilder service : services) {
			channel.sendMessage(service.build()).complete().addReaction("U+1F6D2").queue();
		}
	}

}
