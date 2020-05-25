package me.creepydev.creepybot.notifications;

public enum Notif {
	New_Projet("$user ordered a project ($type): $name"),
	Projet_Accepted("$user has accepted your order: $name");
	
	private String message;
	
	private Notif(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
