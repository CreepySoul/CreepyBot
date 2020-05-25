package me.creepydev.creepybot;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import me.creepydev.creepybot.adapter.Github;
import me.creepydev.creepybot.adapter.OrderStatus;
import me.creepydev.creepybot.projet.Projet;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class ProjetDashboard {

	public static void buildDashboard(Projet projet) {
		@SuppressWarnings("serial")
		List<MessageEmbed.Field> fields = new ArrayList<MessageEmbed.Field>();
		try {
			fields = new ArrayList<MessageEmbed.Field>() {
				{
					add(new Field("Statut", projet.getOrderStatus().getName(), true));
					add(new Field("Price - (Budget)", (projet.getCost() == 0 ? "$??" : Utils.moneyStylizer(projet.getCost())) + " - ("
							+ Utils.moneyStylizer(Float.parseFloat(Utils.numberCleaner(projet.getBudget()))) + " budget)", true));
					add(new Field("Deadline", projet.getDeadline().equalsIgnoreCase("x") ? "❌" : projet.getDeadline(), true));
					add(new Field("Paid", projet.getOrderStatus() == OrderStatus.WAITING ? "❌" : Utils.moneyStylizer(projet.getPaid()), true));
					add(new Field("Work Tracker", projet.getOrderStatus() == OrderStatus.WAITING || projet.getOrderStatus() == OrderStatus.ACCEPTED ? "❌"
							: projet.hasTrackingTime() ? "tracking" : "❌", true));
					add(new Field("Commits", projet.getOrderStatus() == OrderStatus.WAITING || projet.getOrderStatus() == OrderStatus.ACCEPTED ? "❌"
							: Github.getRepoCommits(projet.getRepository()).size() + " commits", true));
					add(new Field("Last Commits",
							projet.getOrderStatus() == OrderStatus.WAITING || projet.getOrderStatus() == OrderStatus.ACCEPTED ? "❌"
									: Github.getRepoCommits(projet.getRepository()).isEmpty() ? "❌"
											: Github.getRepoCommits(projet.getRepository()).get(Github.getRepoCommits(projet.getRepository()).size() - 1)
													.getCommitShortInfo().getMessage(),
							true));
				}
			};
		} catch (IOException e) {
		}

		EmbedBuilder embed = Utils.buildEmbed("Dashboard - Track the progress of your order", Color.green, projet.getDesc(), projet.getName(), "Last update", null,
				"https://cdn.discordapp.com/embed/avatars/0.png", Instant.now(), fields);

		projet.getMainChannel().sendMessage(embed.build()).queue(message -> {
			projet.setDashboard(message);
		});
	}

	public static void updateDashboard(Projet projet) {
		@SuppressWarnings("serial")
		List<MessageEmbed.Field> fields = new ArrayList<MessageEmbed.Field>();
		try {
			fields = new ArrayList<MessageEmbed.Field>() {
				{
					add(new Field("Statut", projet.getOrderStatus().getName(), true));
					add(new Field("Price - (Budget)", (projet.getCost() == 0 ? "$??" : Utils.moneyStylizer(projet.getCost())) + " - ("
							+ Utils.moneyStylizer(Float.parseFloat(Utils.numberCleaner(projet.getBudget()))) + " budget)", true));
					add(new Field("Deadline", projet.getDeadline().equalsIgnoreCase("x") ? "❌" : projet.getDeadline(), true));
					add(new Field("Paid", projet.getOrderStatus() == OrderStatus.WAITING ? "❌" : Utils.moneyStylizer(projet.getPaid()), true));
					add(new Field("Work Tracker", projet.getOrderStatus() == OrderStatus.WAITING || projet.getOrderStatus() == OrderStatus.ACCEPTED ? "❌"
							: projet.hasTrackingTime() ? "tracking" : "❌", true));
					add(new Field("Commits", projet.getOrderStatus() == OrderStatus.WAITING || projet.getOrderStatus() == OrderStatus.ACCEPTED ? "❌"
							: Github.getRepoCommits(projet.getRepository()).size() + " commits", true));
					add(new Field("Last Commits",
							projet.getOrderStatus() == OrderStatus.WAITING || projet.getOrderStatus() == OrderStatus.ACCEPTED ? "❌"
									: Github.getRepoCommits(projet.getRepository()).isEmpty() ? "❌"
											: Github.getRepoCommits(projet.getRepository()).get(Github.getRepoCommits(projet.getRepository()).size() - 1)
													.getCommitShortInfo().getMessage(),
							true));
				}
			};
		} catch (IOException e) {
		}

		EmbedBuilder embed = Utils.buildEmbed("Dashboard - Track the progress of your order", Color.green, projet.getDesc(), projet.getName(), "Last update", null,
				"https://cdn.discordapp.com/embed/avatars/0.png", Instant.now(), fields);

		projet.getDashboardMessage().editMessage(embed.build()).queue();
	}

}
