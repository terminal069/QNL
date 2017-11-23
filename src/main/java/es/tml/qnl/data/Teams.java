package es.tml.qnl.data;

import java.util.HashSet;
import java.util.Set;

public class Teams {

	private static Set<String> teams = new HashSet<>();
	
	public static Set<String> getTeams() {
		
		return teams;
	}
	
	public static void addTeam(String team) {
		
		if (!teams.contains(team)) {
			teams.add(team);
		}
	}
	
	public static boolean existTeam(String team) {
		
		return teams.contains(team);
	}

	public static String to_String() {
		
		StringBuilder sb = new StringBuilder();
		
		teams.forEach(team -> {
			sb.append("[").append(team).append("] ");
		});
		
		return sb.toString();
	}
}
