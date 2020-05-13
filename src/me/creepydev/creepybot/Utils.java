package me.creepydev.creepybot;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class Utils {

	public static EmbedBuilder buildEmbed(String title, Color color, String description, String author, String footer, String image, String thumbnail, Field... fields) {
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
		
		for (Field field : fields) {
			embed.addField(field);
		}
		
		return embed;
	}
	
}
