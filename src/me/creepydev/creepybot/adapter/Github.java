package me.creepydev.creepybot.adapter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;

public class Github {

	public static LinkedList<GHCommit> getRepoCommits(GHRepository repository) {
		try {
			return repository.listCommits().toSet().stream().collect(Collectors.toCollection(LinkedList::new));
		} catch (IOException e) {
		}

		return new LinkedList<GHCommit>();
	}

}
