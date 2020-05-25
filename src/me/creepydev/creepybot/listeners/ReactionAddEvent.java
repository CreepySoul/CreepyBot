package me.creepydev.creepybot.listeners;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;

import me.creepydev.creepybot.Main;
import me.creepydev.creepybot.adapter.ServiceType;
import me.creepydev.creepybot.notifications.Notif;
import me.creepydev.creepybot.projet.Projet;
import me.creepydev.creepybot.services.form.Form;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionAddEvent extends ListenerAdapter {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getUser().isBot()) {
			return;
		}

		if (Main.getServices().getMessages().values().stream().map(Message::getIdLong).collect(Collectors.toList()).contains(event.getMessageIdLong())) {
			ServiceType type = ServiceType.getServiceType(Main.getServices().getMessages().values().stream()
					.filter(message -> message.getIdLong() == event.getMessageIdLong()).collect(Collectors.toList()).get(0));
			event.getReaction().removeReaction(event.getUser()).queue();

			if (Form.forms.stream().map(Form::getUser).anyMatch(user -> user.getIdLong() == event.getUser().getIdLong())) {
				return;
			}

			new Form(event.getUser(), type, new Consumer<Form>() {

				@Override
				public void accept(Form form) {
					new Projet(form.getServiceType(), form.getUser(), form.getReponses().get("name"), form.getReponses().get("desc"), form.getReponses().get("budget"),
							form.getReponses().get("deadline")).buildProjet();
				}
			});
		}
	}

}
