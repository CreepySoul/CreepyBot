package me.creepydev.creepybot;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import me.creepydev.creepybot.messages.Services;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Main {

	private static JDA jda = null;
	private static GitHub github = null;

	public static void main(String[] args) throws LoginException, IOException, InterruptedException {
		jda = new JDABuilder(AccountType.BOT).setToken(args[0]).setActivity(Activity.playing("💻 Develops hard")).build().awaitReady();
		github = new GitHubBuilder().withOAuthToken(args[1]).build();

		new Services("710080788334641173");
	}

	public static JDA getJDA() {
		return jda;
	}

	public static GitHub getGitHub() {
		return github;
	}

}
