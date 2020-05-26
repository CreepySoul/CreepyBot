package me.creepydev.creepybot.listeners;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHRelease;

import me.creepydev.creepybot.Main;
import me.creepydev.creepybot.Utils;
import me.creepydev.creepybot.Validation;
import me.creepydev.creepybot.projet.Projet;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEvent extends ListenerAdapter {

	private Map<TextChannel, File> files = new HashedMap<TextChannel, File>();
	private Map<TextChannel, Validation> validations = new HashedMap<TextChannel, Validation>();

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.getMessage().getAttachments().isEmpty()) {
			if (Main.getProjetManager().getProjetWithCategory(event.getTextChannel().getParent()) != null) {
				Projet projet = Main.getProjetManager().getProjetWithCategory(event.getTextChannel().getParent());

				Iterator<Attachment> attachements = event.getMessage().getAttachments().iterator();
				while (attachements.hasNext()) {
					Attachment attachment = attachements.next();

					if (!files.containsKey(event.getTextChannel())) {
						files.put(event.getTextChannel(), Utils.createRandomFolder());
					}

					try {
						attachment.downloadToFile(new File(files.get(event.getTextChannel()), attachment.getFileName())).get();
					} catch (InterruptedException e) {
					} catch (ExecutionException e) {
					}
				}

				event.getMessage().delete().queue();

				if (validations.containsKey(event.getTextChannel())) {
					validations.get(event.getTextChannel()).unregister();
				}

				validations.put(event.getTextChannel(),
						new Validation(event.getTextChannel().sendMessage("**Do you want to release these files ?**").complete(), validation -> {
							if (validation.getClickedEmote().equals("U+2705")) {
								GHRelease release = null;
								try {
									release = projet.getRepository().createRelease("v" + String.valueOf(projet.getRepository().listReleases().toList().size() + 1))
											.create();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								try {
									for (File file : files.get(event.getTextChannel()).listFiles()) {
										if (release != null) {
											release.uploadAsset(file, file.getName());
										}
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

							File file = files.get(event.getTextChannel());
							new Timer().schedule(new TimerTask() {

								@Override
								public void run() {
									try {
										FileUtils.deleteDirectory(file);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}, 300000);

							files.remove(event.getTextChannel());
							validations.remove(event.getTextChannel());
						}, event.getAuthor(), "U+2705", "U+274C"));
			}
		}
	}

}
