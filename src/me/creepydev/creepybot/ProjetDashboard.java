package me.creepydev.creepybot;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import me.creepydev.creepybot.adapter.Github;
import me.creepydev.creepybot.adapter.OrderStatus;
import me.creepydev.creepybot.projet.Projet;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class ProjetDashboard {

	@SuppressWarnings("serial")
	public static void buildDashboard(Projet projet) {
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
											: Github.getRepoCommits(projet.getRepository()).getFirst().getCommitShortInfo().getMessage(),
							true));
				}
			};
		} catch (IOException e) {
		}

		EmbedBuilder embed = Utils.buildEmbed("Dashboard - Track the progress of your order", Color.green, projet.getDesc(), projet.getName(), "Last update", null,
				"https://cdn.discordapp.com/embed/avatars/0.png", Instant.now(), fields);

		projet.getMainChannel().sendMessage(embed.build()).queue(message -> {
			projet.setDashboard(message);
			putReactions(message, projet);
		});
	}

	@SuppressWarnings("serial")
	public static void updateDashboard(Projet projet) {
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
											: Github.getRepoCommits(projet.getRepository()).getFirst().getCommitShortInfo().getMessage(),
							true));
					add(getEmojiField(projet));
				}
			};
		} catch (IOException e) {
		}

		EmbedBuilder embed = Utils.buildEmbed("Dashboard - Track the progress of your order", Color.green, projet.getDesc(), projet.getName(), "Last update", null,
				"https://cdn.discordapp.com/embed/avatars/0.png", Instant.now(), fields);

		projet.getDashboardMessage().editMessage(embed.build()).queue(message -> putReactions(message, projet));
	}

	public static Field getEmojiField(Projet projet) {
		switch (projet.getOrderStatus()) {
		case WAITING:
			return new Field("Reactions", "❌ (Delete Order) | 🔄 (Refresh)", false);
		case ACCEPTED:
			return new Field("Reactions", "🔄 (Refresh)", false);
		case DEVELOPMENT:
			return new Field("Reactions", "📖 (Commits List) | 🔄 (Refresh)", false);
		case COMPLETED:
			return new Field("Reactions", "📖 (Commits List) | 🔧 (Report bug) | 🛒 (Order update)\n📦 (Downloads) | 🔄 (Refresh)", false);
		case UPDATING:
			return new Field("Reactions", "📖 (Commits List) | 📦 (Downloads) | 🔄 (Refresh)", false);

		default:
			return null;
		}
	}

	@SuppressWarnings("serial")
	public static void putReactions(Message message, Projet projet) {
		Map<OrderStatus, List<String>> reactions = new HashedMap<OrderStatus, List<String>>() {
			{
				put(OrderStatus.WAITING, Arrays.asList("❌", "🔄"));
				put(OrderStatus.ACCEPTED, Arrays.asList("🔄"));
				put(OrderStatus.DEVELOPMENT, Arrays.asList("📖", "🔄"));
				put(OrderStatus.COMPLETED, Arrays.asList("📖", "🔧", "🛒", "📦", "🔄"));
				put(OrderStatus.UPDATING, Arrays.asList("📖", "📦", "🔄"));
			}
		};

		for (int i = 0; i < reactions.get(projet.getOrderStatus()).size(); i++) {
			if (!message.getReactions().get(i).getReactionEmote().getEmoji().equals(reactions.get(projet.getOrderStatus()).get(i))) {
				message.clearReactions().queue(reac -> {
					for (String reaction : reactions.get(projet.getOrderStatus())) {
						message.addReaction(reaction).queue();
					}
				});
				break;
			}
		}
	}

}
