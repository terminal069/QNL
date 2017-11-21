package es.tml.qnl.util.enums;

import java.util.ArrayList;
import java.util.List;

public class Seasons {

	private static List<String> seasons = new ArrayList<>();
	
	public static void addSeason(String season) {
		
		seasons.add(season);
	}
	
	public static List<String> getSeasons() {
		
		return seasons;
	}
	
	public static int getTotalSeasons() {
		
		return seasons.size();
	}
	
	public static String to_String() {
		
		StringBuilder sb = new StringBuilder().append("[ ");
		
		seasons.forEach(season -> {
			sb.append(season).append(" ");
		});
		
		return sb.append("]").toString();
	}
}
