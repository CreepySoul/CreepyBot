package me.creepydev.creepybot.notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.creepydev.creepybot.Main;
import me.creepydev.creepybot.adapter.ServiceType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NotificationsManager extends ListenerAdapter {

	private TextChannel channel;
	private Notifications notifications;
	private List<User> antiNotifs = new ArrayList<User>();

	public NotificationsManager(String channelID) {
		this.channel = Main.getJDA().getTextChannelById(channelID);
		this.notifications = new Notifications(this.channel);

		try {
			this.notifications.getMessage().retrieveReactionUsers("🔕").queue(users -> {
				antiNotifs = users;
			});
		} catch (Exception e) {
		}
	}

	public List<User> getAntiNotifsUsers() {
		return this.antiNotifs;
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getUser().isBot()) {
			return;
		}

		if (event.getChannel().getIdLong() == this.channel.getIdLong()) {
			if (event.getReaction().getReactionEmote().getEmoji().equals("U+1F515")) {
				this.antiNotifs.add(event.getUser());
			}
		}
	}

	public void sendNotif(User user, Notif notif, Map<String, Object> placeholders) {
		if (this.antiNotifs.contains(user)) {
			return;
		}
		
		user.openPrivateChannel().queue(channel -> {
			channel.sendMessage(notif.getMessage()
					.replace("$user", placeholders.containsKey("$user") ? ((User) placeholders.get("$user")).getAsMention() : "")
					.replace("$type", placeholders.containsKey("$type") ? ((ServiceType) placeholders.get("$type")).toString() : "")
					.replace("$name", placeholders.containsKey("$name") ? ((String) placeholders.get("$name")) : "")).queue();
		});
	}

	public void sendNotif(Role role, Notif notif, Map<String, Object> placeholders) {
		Main.getJDA().getGuilds().get(0).getMembersWithRoles(role).stream().map(Member::getUser).forEach(user -> {
			if (this.antiNotifs.contains(user)) {
				return;
			}
			
			user.openPrivateChannel().queue(channel -> {
				channel.sendMessage(notif.getMessage()
						.replace("$user", ((User) placeholders.get("$user")).getAsMention())
						.replace("$type", ((ServiceType) placeholders.get("$type")).toString())
						.replace("$name", ((String) placeholders.get("$name")))).queue();
			});
		});
	}

	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		if (event.getUser().isBot()) {
			return;
		}

		if (event.getChannel().getIdLong() == this.channel.getIdLong()) {
			if (event.getReaction().getReactionEmote().getEmoji().equals("U+1F515")) {
				this.antiNotifs.remove(event.getUser());
			}
		}
	}

}
