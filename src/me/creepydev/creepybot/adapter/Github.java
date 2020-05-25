package me.creepydev.creepybot.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;

public class Github {

	public static List<GHCommit> getRepoCommits(GHRepository repository) {
		try {
			return repository.listCommits().toList();
		} catch (IOException e) {
		}
		
		return new ArrayList<GHCommit>();
	}
	
}
