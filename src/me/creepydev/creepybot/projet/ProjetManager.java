package me.creepydev.creepybot.projet;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import me.creepydev.creepybot.ProjetDashboard;
import net.dv8tion.jda.api.entities.Category;

public class ProjetManager {

	private List<Projet> projets = new ArrayList<Projet>();

	public List<Projet> getProjets() {
		return this.projets;
	}

	public Projet getProjetWithCategory(Category category) {
		return getProjetWithCategory(category.getIdLong());
	}

	public Projet getProjetWithCategory(Long categoryID) {
		return this.projets.stream().filter(projet -> projet.getCategory().getId().equals(categoryID.toString())).collect(Collectors.toList()).get(0);
	}
	
	public void startUpdateTask() {
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				for (Projet projet : getProjets()) {
					ProjetDashboard.updateDashboard(projet);
				}
			}
		}, 15000, 60000);
	}
}
