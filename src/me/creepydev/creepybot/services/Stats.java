package me.creepydev.creepybot.services;

import java.awt.Color;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

import me.creepydev.creepybot.Main;
import me.creepydev.creepybot.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Stats {

	private TextChannel channel;
	private Message message;
	private User user = Main.getJDA().getUserById(251993240406851584L);

	private EmbedBuilder stats = Utils.buildEmbed("Statistiques", Color.white, "Here are the statistics of " + user.getAsMention(), user.getName(), "Last update", null,
			"https://static.vecteezy.com/system/resources/previews/000/500/506/non_2x/stats-icon-design-vector.jpg", Instant.now(),
			new Field("test", "test value", true));

	public Stats(String channelID) {
		channel = Main.getJDA().getTextChannelById(channelID);
		setStatsMessage();

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				updateStatsMessage();
			}
		}, 60000, 60000);
	}

	public void setStatsMessage() {
		channel.getHistory().retrievePast(100).complete().forEach(message -> {
			if (message.getEmbeds().size() != 1) {
				message.delete().queue();
			}

			if (message.getEmbeds().get(0).getTitle().equals(stats.build().getTitle())) {
				this.message = message;
			}
		});

		if (this.message == null) {
			channel.sendMessage(stats.build()).queue();
		}
	}

	public void updateStatsMessage() {
		if (this.message != null) {
			this.message.editMessage(stats.build()).queue();
		}
	}

}
