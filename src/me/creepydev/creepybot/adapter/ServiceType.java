package me.creepydev.creepybot.adapter;

import me.creepydev.creepybot.Main;
import net.dv8tion.jda.api.entities.Message;

public enum ServiceType {
	DISCORD, PLUGIN;
	
	public static ServiceType getServiceType(Message message) {
		for (ServiceType key : Main.getServices().getMessages().keySet()) {
			if (Main.getServices().getMessages().get(key).getIdLong() == message.getIdLong()) {
				return key;
			}
		}

		return null;
	}

}
