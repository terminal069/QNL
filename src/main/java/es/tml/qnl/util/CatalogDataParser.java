package es.tml.qnl.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import es.tml.qnl.data.Teams;
import es.tml.qnl.exceptions.QNLException;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.Season;
import es.tml.qnl.model.mongo.Team;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.TeamRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CatalogDataParser {

	private static final String QNL_DATAPARSER_CLASSTAG_ROUND = "qnl.dataParser.classTag.round";
	private static final String QNL_DATAPARSER_CLASSTAG_ROUNDPREFIX = "qnl.dataParser.classTag.roundPrefix";
	private static final String QNL_DATAPARSER_CLASSTAG_RESULT = "qnl.dataParser.classTag.result";
	private static final String QNL_DATAPARSER_CLASSTAG_LOCAL = "qnl.dataParser.classTag.local";
	private static final String QNL_DATAPARSER_CLASSTAG_VISITOR = "qnl.dataParser.classTag.visitor";
	private static final String QNL_DATAPARSER_CLASSTAG_LOCALRES = "qnl.dataParser.classTag.localRes";
	private static final String QNL_DATAPARSER_CLASSTAG_VISITORRES = "qnl.dataParser.classTag.visitorRes";
	private static final String QNL_POINTS_WIN = "qnl.points.win";
	private static final String QNL_POINTS_DRAW = "qnl.points.draw";
	private static final String QNL_POINTS_LOSE = "qnl.points.lose";
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_ROUND + "}")
	private String roundClassTag;
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_ROUNDPREFIX + "}")
	private String roundPrefix;

	@Value("${" + QNL_DATAPARSER_CLASSTAG_RESULT + "}")
	private String resultClassTag;
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_LOCAL + "}")
	private String localClassTag;
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_VISITOR + "}")
	private String visitorClassTag;
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_LOCALRES + "}")
	private String localResClassTag;
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_VISITORRES + "}")
	private String visitorResClassTag;
	
	@Value("${" + QNL_POINTS_WIN + "}")
	private Integer win;
	
	@Value("${" + QNL_POINTS_DRAW + "}")
	private Integer draw;
	
	@Value("${" + QNL_POINTS_LOSE + "}")
	private Integer lose;
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	private int roundNumber;
	private int seasonCode;
	private String leagueCode;
	private Map<String, Integer> globalPoints = new HashMap<>();
	
	public void parseDataFromUrl(String leagueCode, Season season) {
		
		log.debug("Parsing data from league '{}' and season '{}'", leagueCode, season.getName());
		
		initialize(leagueCode, season.getCode());
		
		// Delete old data to be parsed
		deleteData();
		
		// Get document from url and parse it
		getDocument(season.getUrl())
			.getElementsByClass(roundClassTag).forEach(roundElement -> {
				parseRound(roundElement);
			});
	}
	
	public void parseCurrentDataFromUrl(String leagueCode, Season season, int round) {
		
		log.debug("Parsing data from league '{}' and season '{}' to round '{}'", leagueCode, season.getName(), round);
	}

	private void initialize(String leagueCode, int seasonCode) {
		
		this.seasonCode = seasonCode;
		this.leagueCode = leagueCode;
		this.roundNumber = 0;
		this.globalPoints.clear();
	}

	private void deleteData() {
		
		roundRepository.deleteByLeagueAndSeason(leagueCode, seasonCode);
	}

	private Document getDocument(String url) {
		
		try {
			return Jsoup.connect(url).get();
		} catch (IOException e) {
			log.error("Error getting document from url '" + url + "': ", e);
			throw new QNLException(
				HttpStatus.INTERNAL_SERVER_ERROR, 
				"Error getting document from url '" + url + "': " + e.getMessage());
		}
	}
	
	private void parseRound(Element roundElement) {
		
		roundElement.classNames().forEach(className -> {
			if (className.startsWith(roundPrefix)) {
				roundNumber = Integer.parseInt(className.substring(roundPrefix.length()));
			}
		});
		
		roundElement.getElementsByClass(resultClassTag).forEach(resultElement -> {
			parseResults(resultElement);
		});
	}
	
	private void parseResults(Element resultElement) {
		
		// Load teams
		String local = resultElement.getElementsByClass(localClassTag).first().text();
		String visitor = resultElement.getElementsByClass(visitorClassTag).first().text();
		loadTeam(local, visitor);
		
		// Calculate round points
		int localRes = Integer.parseInt(resultElement.getElementsByClass(localResClassTag).first().text());
		int visitorRes = Integer.parseInt(resultElement.getElementsByClass(visitorResClassTag).first().text());
		int localRoundPoints = calculatePoints(localRes, visitorRes);
		int visitorRoundPoints = calculatePoints(visitorRes, localRes);
		
		// Add global points
		addGlobalPoints(local, localRoundPoints);
		addGlobalPoints(visitor, visitorRoundPoints);
		
		// Save data
		roundRepository.save(new Round(
				roundNumber,
				seasonCode,
				leagueCode,
				local,
				visitor,
				localRes,
				visitorRes,
				globalPoints.get(local),
				globalPoints.get(visitor)));
	}
	
	private void loadTeam(String... teams) {
		
		for (String team : teams) {
			if (!Teams.existTeam(team)) {
				teamRepository.save(new Team(team));
				Teams.addTeam(team);
			}
		}
	}
	
	private int calculatePoints(int resA, int resB) {
		
		return resA > resB ? win : (resA == resB ? draw : lose);
	}
	
	private void addGlobalPoints(String team, int roundPoints) {
		
		int points = globalPoints.get(team) == null ? 0 : globalPoints.get(team);
		
		globalPoints.put(team, points + roundPoints);
	}

}
