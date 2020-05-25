package me.creepydev.creepybot;

import java.awt.Color;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class Utils {

	public static EmbedBuilder buildEmbed(String title, Color color, String description, String author, String footer, String image, String thumbnail,
			TemporalAccessor timestamp, Field... fields) {
		EmbedBuilder embed = new EmbedBuilder();

		if (title != null) {
			embed.setTitle(title);
		}
		if (color != null) {
			embed.setColor(color);
		}
		if (description != null) {
			embed.setDescription(description);
		}
		if (author != null) {
			embed.setAuthor(author);
		}
		if (footer != null) {
			embed.setFooter(footer);
		}
		if (image != null) {
			embed.setImage(image);
		}
		if (thumbnail != null) {
			embed.setThumbnail(thumbnail);
		}

		if (timestamp != null) {
			embed.setTimestamp(timestamp);
		}

		for (Field field : fields) {
			embed.addField(field);
		}

		return embed;
	}

	public static EmbedBuilder buildEmbed(String title, Color color, String description, String author, String footer, String image, String thumbnail,
			TemporalAccessor timestamp, List<Field> fields) {
		EmbedBuilder embed = new EmbedBuilder();

		if (title != null) {
			embed.setTitle(title);
		}
		if (color != null) {
			embed.setColor(color);
		}
		if (description != null) {
			embed.setDescription(description);
		}
		if (author != null) {
			embed.setAuthor(author);
		}
		if (footer != null) {
			embed.setFooter(footer);
		}
		if (image != null) {
			embed.setImage(image);
		}
		if (thumbnail != null) {
			embed.setThumbnail(thumbnail);
		}

		if (timestamp != null) {
			embed.setTimestamp(timestamp);
		}

		for (Field field : fields) {
			embed.addField(field);
		}

		return embed;
	}

	public static String moneyStylizer(float money) {
		return "$" + String.valueOf(money);
	}

	public static String numberCleaner(String input) {
		input = input.replaceAll("[^\\d.]", "");
		if (input.length() > 0) {
			return input;
		} else {
			return "0";
		}
	}

}
