package es.tml.qnl.services.catalog.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import es.tml.qnl.data.Teams;
import es.tml.qnl.exceptions.QNLException;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.Season;
import es.tml.qnl.model.mongo.Team;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.TeamRepository;
import es.tml.qnl.services.catalog.CatalogDataParserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CatalogDataParserServiceImpl implements CatalogDataParserService {

	private static final String QNL_DATAPARSER_CLASSTAG_ROUND = "qnl.dataParser.classTag.round";
	private static final String QNL_DATAPARSER_CLASSTAG_RESULT = "qnl.dataParser.classTag.result";
	private static final String QNL_DATAPARSER_CLASSTAG_LOCAL = "qnl.dataParser.classTag.local";
	private static final String QNL_DATAPARSER_CLASSTAG_VISITOR = "qnl.dataParser.classTag.visitor";
	private static final String QNL_DATAPARSER_CLASSTAG_LOCALRES = "qnl.dataParser.classTag.localRes";
	private static final String QNL_DATAPARSER_CLASSTAG_VISITORRES = "qnl.dataParser.classTag.visitorRes";
	private static final String QNL_POINTS_WIN = "qnl.points.win";
	private static final String QNL_POINTS_DRAW = "qnl.points.draw";
	private static final String QNL_POINTS_LOSE = "qnl.points.lose";
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_ROUND + ":#{null}}")
	private String roundClassTag;

	@Value("${" + QNL_DATAPARSER_CLASSTAG_RESULT + ":#{null}}")
	private String resultClassTag;
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_LOCAL + ":#{null}}")
	private String localClassTag;
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_VISITOR + ":#{null}}")
	private String visitorClassTag;
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_LOCALRES + ":#{null}}")
	private String localResClassTag;
	
	@Value("${" + QNL_DATAPARSER_CLASSTAG_VISITORRES + ":#{null}}")
	private String visitorResClassTag;
	
	@Value("${" + QNL_POINTS_WIN + ":#{null}}")
	private Integer win;
	
	@Value("${" + QNL_POINTS_DRAW + ":#{null}}")
	private Integer draw;
	
	@Value("${" + QNL_POINTS_LOSE + ":#{null}}")
	private Integer lose;
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	
	
	private int roundNumber;
	private int seasonCode;
	private String leagueCode;
	private Map<String, Integer> globalPoints = new HashMap<>();
	
	@Override
	public void parseDataFromUrl(String leagueCode, Season season) {
		
		// Initialize
		initialize(leagueCode, season.getCode());
		
		// Delete old data to be parsed
		deleteData();
		
		// Get document from url and parse it
		getDocument(season.getUrl())
			.getElementsByClass(roundClassTag).forEach(roundElement -> {
				parseRound(roundElement);
			});
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
			if (className.startsWith("ij")) {
				roundNumber = Integer.parseInt(className.substring(2));
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
		
		log.info("Round" + roundNumber + ": " + local + " " + localRes + " - " + visitorRes + " " + visitor
				+ " (" + globalPoints.get(local) + " - " + globalPoints.get(visitor) + ")");
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
	
	private void addGlobalPoints(String team, int localRoundPoints) {
		
		int points = globalPoints.get(team) == null ? 0 : globalPoints.get(team);
		
		globalPoints.put(team, points + localRoundPoints);
	}

}
