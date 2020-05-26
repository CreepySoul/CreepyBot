package me.creepydev.creepybot;

import java.util.List;
import java.util.function.Consumer;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Validation extends ListenerAdapter {

	private Message message;
	private User user;
	private String[] unicodes;
	private Consumer<Validation> consumer;
	private ReactionEmote clickedEmote;

	public Validation(Message message, Consumer<Validation> consumer, User user, String... unicodes) {
		this.message = message;
		this.user = user;
		this.consumer = consumer;
		this.unicodes = unicodes;

		Main.getJDA().addEventListener(this);
		for (String unicode : this.unicodes) {
			this.message.addReaction(unicode).queue();
		}
	}

	public Validation(Message message, Consumer<Validation> consumer, User user, List<String> unicodes) {
		this.message = message;
		this.user = user;
		this.consumer = consumer;
		this.unicodes = unicodes.toArray(new String[0]);

		Main.getJDA().addEventListener(this);
		for (String unicode : this.unicodes) {
			try {
				this.message.addReaction(unicode).queue();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getUser().isBot()) {
			return;
		}

		if (event.getMessageId().equals(this.message.getId())) {
			if (event.getUser().getId().equals(this.user.getId())) {
				this.clickedEmote = event.getReactionEmote();
				this.consumer.accept(this);
				this.message.delete().complete();

				Main.getJDA().getEventManager().unregister(this);
			} else {
				event.getReaction().removeReaction(this.user).complete();
			}
		}
	}

	public Message getMessage() {
		return this.message;
	}

	public User getUser() {
		return this.user;
	}

	public String[] getUnicodes() {
		return this.unicodes;
	}

	public Consumer<Validation> getConsumer() {
		return this.consumer;
	}

	public String getClickedEmote() {
		return this.clickedEmote.getAsCodepoints();
	}
	
	public String getIntactClickedEmote() {
		return this.clickedEmote.getEmoji();
	}
	
	public void unregister() {
		this.message.delete().complete();
		Main.getJDA().getEventManager().unregister(this);
	}

}
