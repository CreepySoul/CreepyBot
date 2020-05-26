package me.creepydev.creepybot.listeners;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import me.creepydev.creepybot.Main;
import me.creepydev.creepybot.ProjetDashboard;
import me.creepydev.creepybot.ProjetMenus;
import me.creepydev.creepybot.adapter.Menus;
import me.creepydev.creepybot.adapter.ServiceType;
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
		} else if (!Main.getProjetManager().getProjets().stream().filter(project -> project.getDashboardMessage().getIdLong() == event.getMessageIdLong())
				.collect(Collectors.toList()).isEmpty()) {
			Projet projet = Main.getProjetManager().getProjets().stream().filter(project -> project.getDashboardMessage().getIdLong() == event.getMessageIdLong())
					.collect(Collectors.toList()).get(0);

			if (event.getReactionEmote().getEmoji().equals("🔄")) {
				ProjetDashboard.updateDashboard(projet);
			} else if (event.getReactionEmote().getEmoji() == "❌") {
				projet.getCategory().getChannels().forEach(channel -> {
					channel.delete().complete();
				});
				projet.getRole().delete().complete();
				projet.getCategory().delete().complete();

				Main.getProjetManager().getProjets().remove(projet);
			} else if (event.getReactionEmote().getEmoji().equals("📖")) {
				ProjetMenus.showMenu(projet, Menus.Commits);
			} else if (event.getReactionEmote().getEmoji().equals("🔧")) {
				System.out.println("bug update");
			} else if (event.getReactionEmote().getEmoji().equals("🛒")) {
				System.out.println("buy upgrade");
			} else if (event.getReactionEmote().getEmoji().equals("📦")) {
				System.out.println("dl");
			}

			event.getReaction().removeReaction(event.getUser()).queue();
		}
	}

}
