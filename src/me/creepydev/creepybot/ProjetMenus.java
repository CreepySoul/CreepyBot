package me.creepydev.creepybot;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.collections4.map.HashedMap;
import org.kohsuke.github.GHCommit;

import me.creepydev.creepybot.adapter.Menus;
import me.creepydev.creepybot.projet.Projet;
import net.dv8tion.jda.api.entities.Message;

public class ProjetMenus {

	public static Map<Projet, List<Message>> guis = new HashedMap<Projet, List<Message>>();

	@SuppressWarnings("serial")
	public static void showMenu(Projet projet, Menus menu) {
		if (guis.containsKey(projet)) {
			for (Message message : guis.get(projet)) {
				message.delete().queue();
			}

			guis.remove(projet);
		}

		if (menu == Menus.Commits) {
			List<GHCommit> commits = new ArrayList<GHCommit>();
			try {
				commits = projet.getRepository().listCommits().toList();
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (GHCommit commit : commits) {
				try {
					projet.getMainChannel().sendMessage(Utils.buildEmbed(Utils.dateFormat.format(commit.getCommitDate()), Color.yellow,
							commit.getCommitShortInfo().getMessage(), "Commit #" + (commits.size() - commits.indexOf(commit)), null, null, null, null).build())
							.queue(message -> {
								if (guis.containsKey(projet)) {
									guis.get(projet).add(message);
								} else {
									guis.put(projet, new ArrayList<Message>() {
										{
											add(message);

										}
									});
								}
							});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					if (guis.containsKey(projet)) {
						for (Message message : guis.get(projet)) {
							message.delete().queue();
						}

						guis.remove(projet);
					}
				}
			}, 30000);
		} else if (menu == Menus.Downloads) {

		}
	}

}
