package me.creepydev.creepybot.adapter;

public enum OrderStatus {
	WAITING("⏳- Waiting for Validation", "⏳"),
	ACCEPTED("🔍- Accepted", "🔍"),
	DEVELOPMENT("👨‍💻- In Development", "👨‍💻"),
	COMPLETED("📦- Completed", "📦"),
	UPDATING("🔧- Updating", "🔧");

	private String name;
	private String unicode;

	OrderStatus(String name, String unicode) {
		this.name = name;
		this.unicode = unicode;
	}

	public String getName() {
		return this.name;
	}

	public String getUnicode() {
		return this.unicode;
	}

}
