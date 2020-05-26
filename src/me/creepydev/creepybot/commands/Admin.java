package me.creepydev.creepybot.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.creepydev.creepybot.Main;
import me.creepydev.creepybot.ProjetDashboard;
import me.creepydev.creepybot.Validation;
import me.creepydev.creepybot.adapter.OrderStatus;
import me.creepydev.creepybot.projet.Projet;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Admin extends ListenerAdapter {

	public String command = "!admin";
	public List<String> subCommands = Arrays.asList("order-remove", "order-valid", "set-status", "toggle-tracking");
	public List<String> roles = Arrays.asList("710481231321628773");

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}

		if (event.getMessage().getContentDisplay().split(" ")[0].equalsIgnoreCase(command)) {
			String[] args = event.getMessage().getContentDisplay().split(" ");

			if (event.getMember().getRoles().stream().noneMatch(role -> roles.contains(role.getId())) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)
					&& !event.getMember().isOwner()) {
				event.getMessage().delete().queue();

				return;
			}

			if (args.length > 1) {
				if (subCommands.contains(args[1].toLowerCase())) {
					if (args[1].equalsIgnoreCase("order-remove")) {
						List<String> commandArgs = Arrays.asList("-saveRepo");
						
						Projet projet = Main.getProjetManager().getProjetWithCategory(event.getTextChannel().getParent());

						projet.getCategory().getChannels().forEach(channel -> {
							channel.delete().complete();
						});
						projet.getRole().delete().complete();
						projet.getCategory().delete().complete();

						if (Arrays.asList(args).stream().noneMatch(arg -> commandArgs.contains(arg))) {
							if (projet.getRepository() != null) {
								try {
									projet.getRepository().delete();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}

						Main.getProjetManager().getProjets().remove(projet);
					} else if (args[1].equalsIgnoreCase("order-valid")) {
						Projet projet = Main.getProjetManager().getProjetWithCategory(event.getTextChannel().getParent());
						try {
							projet.validProjet(event.getTextChannel(), event.getAuthor());
						} catch (IOException e) {
							e.printStackTrace();
						}
						event.getMessage().delete().queue();
					} else if (args[1].equalsIgnoreCase("set-status")) {
						Projet projet = Main.getProjetManager().getProjetWithCategory(event.getTextChannel().getParent());

						new Validation(event.getTextChannel().sendMessage("**Please choose a status**").complete(), status -> {
							String unicode = status.getIntactClickedEmote();

							for (OrderStatus orderStatus : OrderStatus.values()) {
								if (unicode.equals(orderStatus.getUnicode())) {
									projet.setOrderStatus(orderStatus);
									ProjetDashboard.updateDashboard(projet);
									break;
								}
							}
						}, event.getAuthor(), Arrays.asList(OrderStatus.values()).stream().map(OrderStatus::getUnicode)
								.filter(unicode -> unicode != projet.getOrderStatus().getUnicode()).collect(Collectors.toList()));
						event.getMessage().delete().queue();
					} else if (args[1].equalsIgnoreCase("toggle-tracking")) {
						Projet projet = Main.getProjetManager().getProjetWithCategory(event.getTextChannel().getParent());
						
						projet.toggleTrackingTime();
						event.getMessage().delete().queue();
						ProjetDashboard.updateDashboard(projet);
					}
				}
			}
		}
	}

}
