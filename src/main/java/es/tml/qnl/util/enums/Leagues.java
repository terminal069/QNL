package es.tml.qnl.util.enums;

import java.util.ArrayList;
import java.util.List;

public class Leagues {

	private static List<String> leagues = new ArrayList<>();
	
	public static void addLeague(String league) {
		
		leagues.add(league);
	}
	
	public static List<String> getLeagues() {
		
		return leagues;
	}

	public static int getTotalLeagues() {
		
		return leagues.size();
	}

	public static String to_String() {
		
		StringBuilder sb = new StringBuilder().append("[ ");
		
		leagues.forEach(leagues -> {
			sb.append(leagues).append(" ");
		});
		
		return sb.append("]").toString();
	}
}
