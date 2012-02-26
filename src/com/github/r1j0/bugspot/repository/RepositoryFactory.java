package com.github.r1j0.bugspot.repository;

public class RepositoryFactory {

	public static Repository getInstance(final String type, final String url, final String username, final String password) {
		final String typeLowerCase = type.toLowerCase();

		if (typeLowerCase.equals("svn")) {
			return new SvnRepository(url, username, password);
		} else if (typeLowerCase.equals("git")) {
			return new GitRepository(url, username, password);
		}

		throw new IllegalArgumentException("No repository connector for repository type " + type + " available.");
	}

}
