package me.creepydev.creepybot.notifications;

import java.awt.Color;

import me.creepydev.creepybot.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Notifications {

	private TextChannel channel;
	private Message mes;
	private EmbedBuilder embedBuilder = Utils.buildEmbed("Deactivate the notifications", Color.orange,
			"Do you no longer wish to receive notifications by private message?\nNo problem, just react to this emote 🔕.\nYou can cancel this action by deleting your reaction.",
			"Notifications", null, null, null, null);

	public Notifications(TextChannel channel) {
		this.channel = channel;
		setNotificationMessage();
	}

	public void setNotificationMessage() {
		for (Message mes : this.channel.getHistory().retrievePast(100).complete()) {
			if (mes.getEmbeds().size() > 0) {
				if (mes.getEmbeds().get(0).getTitle().equalsIgnoreCase(embedBuilder.build().getTitle())) {
					this.mes = mes;
					return;
				}
			}
		}

		this.channel.sendMessage(this.embedBuilder.build()).queue(message -> {
			message.addReaction("U+1F515").queue();
			this.mes = message;
		});
	}

	public Message getMessage() {
		return this.mes;
	}

}
