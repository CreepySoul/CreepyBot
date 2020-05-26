package me.creepydev.creepybot;

import java.io.File;
import java.io.IOException;
import javax.security.auth.login.LoginException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import me.creepydev.creepybot.commands.Admin;
import me.creepydev.creepybot.listeners.MemberJoinEvent;
import me.creepydev.creepybot.listeners.MessageEvent;
import me.creepydev.creepybot.listeners.ReactionAddEvent;
import me.creepydev.creepybot.notifications.NotificationsManager;
import me.creepydev.creepybot.projet.ProjetManager;
import me.creepydev.creepybot.serializer.Persist;
import me.creepydev.creepybot.services.Services;
import me.creepydev.creepybot.services.Stats;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Main {

	private static JDA jda = null;
	private static GitHub github = null;
	private static Persist persist;

	private static File folder;

	private static Services services;
	private static NotificationsManager notificationsManager;
	private static ProjetManager projetManager;
	private static Stats stats;

	public static void main(String[] args) throws LoginException, IOException, InterruptedException {
		jda = new JDABuilder(AccountType.BOT).setToken(args[0]).setActivity(Activity.playing("💻 Develops hard")).build().awaitReady();
		github = new GitHubBuilder().withOAuthToken(args[1]).build();
		persist = new Persist();

		folder = new File(args[2]);

		services = new Services("710080788334641173");
		notificationsManager = new NotificationsManager("710508992761298986");
		projetManager = persist.getFile(ProjetManager.class).exists() ? persist.load(ProjetManager.class) : new ProjetManager();
		stats = new Stats("710527621133238402");

		// Listeners
		jda.addEventListener(new ReactionAddEvent());
		jda.addEventListener(new MemberJoinEvent());
		jda.addEventListener(new MessageEvent());

		// Commands
		jda.addEventListener(new Admin());

		projetManager.startUpdateTask();

		Runtime.getRuntime().addShutdownHook(new Thread() {

			public void run() {
				if (projetManager != null) {
					persist.save(projetManager);
				}
			}

		});
	}

	public static JDA getJDA() {
		return jda;
	}

	public static GitHub getGitHub() {
		return github;
	}

	public static File getFolder() {
		return folder;
	}

	public static Services getServices() {
		return services;
	}

	public static NotificationsManager getNotificationsManager() {
		return notificationsManager;
	}

	public static ProjetManager getProjetManager() {
		return projetManager;
	}

	public static Stats getStats() {
		return stats;
	}

}
