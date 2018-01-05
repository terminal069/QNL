package es.tml.qnl.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import es.tml.qnl.model.mongo.GenericRound;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.RoundPrediction;
import es.tml.qnl.model.mongo.Season;
import es.tml.qnl.model.mongo.Team;
import es.tml.qnl.repositories.mongo.RoundPredictionRepository;
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
	private static final String QNL_DATAPARSER_NOT_DISPUTED = "qnl.dataParser.notDisputed";
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
	
	@Value("${" + QNL_DATAPARSER_NOT_DISPUTED + "}")
	private String notDisputed;
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private RoundPredictionRepository roundPredictionRepository;
	
	private int roundNumber;
	private int seasonCode;
	private String leagueCode;
	private Map<String, Integer> globalPoints = new HashMap<>();
	
	
	public void parseDataFromUrl(String leagueCode, Season season) {
		
		log.debug("Parsing data from league '{}' and season '{}'", leagueCode, season.getName());
		
		initialize(leagueCode, season.getCode());
		
		// Get document from url and parse it
		getDocument(season.getUrl())
			.getElementsByClass(roundClassTag)
			.forEach(roundElement -> parseRound(roundElement));
	}
	
	public void parsePartialDataFromUrl(String leagueCode, Season season, int toRound) {
		
		log.debug("Parsing data from league '{}' and season '{}', to round '{}'",
				leagueCode, season.getName(), toRound);
		
		initialize(leagueCode, season.getCode());
		
		getDocument(season.getUrl())
			.getElementsByClass(roundClassTag)
			.forEach(roundElement -> parsePartialRound(roundElement, toRound));
	}

	private void initialize(String leagueCode, int seasonCode) {
		
		this.seasonCode = seasonCode;
		this.leagueCode = leagueCode;
		this.globalPoints.clear();
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
		
		List<GenericRound> rounds = new ArrayList<>();
		
		roundElement.getElementsByClass(resultClassTag).forEach(resultElement -> {
			rounds.add(parseResults(resultElement));
		});
		
		calculatePositions(rounds);

		rounds.forEach(round -> roundRepository.save(new Round(
				round.getRoundNumber(),
				round.getSeasonCode(),
				round.getLeagueCode(),
				round.getLocal(),
				round.getVisitor(),
				round.getLocalRes(),
				round.getVisitorRes(),
				round.getLocalPoints(),
				round.getVisitorPoints(),
				round.getLocalPosition(),
				round.getVisitorPosition())));
	}
	
	private Round parseResults(Element resultElement) {
		
		// Load teams
		String local = resultElement.getElementsByClass(localClassTag).first().text();
		String visitor = resultElement.getElementsByClass(visitorClassTag).first().text();
		loadTeam(local, visitor);
		
		// Calculate round data
		int localRes = Integer.parseInt(resultElement.getElementsByClass(localResClassTag).first().text());
		int visitorRes = Integer.parseInt(resultElement.getElementsByClass(visitorResClassTag).first().text());
		int localRoundPoints = calculatePoints(localRes, visitorRes);
		int visitorRoundPoints = calculatePoints(visitorRes, localRes);
		
		// Add global points
		addGlobalPoints(local, localRoundPoints);
		addGlobalPoints(visitor, visitorRoundPoints);
		
		return new Round(
			roundNumber,
			seasonCode,
			leagueCode,
			local,
			visitor,
			localRes,
			visitorRes,
			globalPoints.get(local),
			globalPoints.get(visitor));
	}
	
	private void parsePartialRound(Element roundElement, int toRound) {
		
		roundElement.classNames().forEach(className -> {
			if (className.startsWith(roundPrefix)) {
				roundNumber = Integer.parseInt(className.substring(roundPrefix.length()));
			}
		});
		
		List<GenericRound> rounds = new ArrayList<>();
		
		if (roundNumber < toRound) {
			roundElement.getElementsByClass(resultClassTag).forEach(resultElement -> {
				RoundPrediction round = parsePartialResults(resultElement);
				if (round != null) {
					rounds.add(round);
				}
			});
			
			calculatePositions(rounds);
			
			rounds.forEach(round -> roundPredictionRepository.save(new RoundPrediction(
					round.getRoundNumber(),
					round.getSeasonCode(),
					round.getLeagueCode(),
					round.getLocal(),
					round.getVisitor(),
					round.getLocalRes(),
					round.getVisitorRes(),
					round.getLocalPoints(),
					round.getVisitorPoints(),
					round.getLocalPosition(),
					round.getVisitorPosition())));
		}
	}

	private RoundPrediction parsePartialResults(Element resultElement) {
		
		RoundPrediction round = null;
		
		String localResTemp = resultElement.getElementsByClass(localResClassTag).first().text();
		String visitorResTemp = resultElement.getElementsByClass(visitorResClassTag).first().text();
		
		if (!localResTemp.equals(notDisputed) && !visitorResTemp.equals(notDisputed)) {
		
			// Calculate round data
			int localRes = Integer.parseInt(localResTemp);
			int visitorRes = Integer.parseInt(visitorResTemp);
			int localRoundPoints = calculatePoints(localRes, visitorRes);
			int visitorRoundPoints = calculatePoints(visitorRes, localRes);
			
			// Add global points
			String local = resultElement.getElementsByClass(localClassTag).first().text();
			String visitor = resultElement.getElementsByClass(visitorClassTag).first().text();
			addGlobalPoints(local, localRoundPoints);
			addGlobalPoints(visitor, visitorRoundPoints);
			
			round = new RoundPrediction(
				roundNumber,
				seasonCode,
				leagueCode,
				local,
				visitor,
				localRes,
				visitorRes,
				globalPoints.get(local),
				globalPoints.get(visitor));
		}
		
		return round;
	}
	
	private int calculatePoints(int resA, int resB) {
		
		return resA > resB ? win : (resA == resB ? draw : lose);
	}
	
	private void addGlobalPoints(String team, int roundPoints) {
		
		int points = globalPoints.get(team) == null ? 0 : globalPoints.get(team);
		
		globalPoints.put(team, points + roundPoints);
	}
	
	private void loadTeam(String... teams) {
		
		for (String team : teams) {
			if (!Teams.existTeam(team)) {
				teamRepository.save(new Team(team));
				Teams.addTeam(team);
			}
		}
	}
	
	private void calculatePositions(List<GenericRound> rounds) {

		List<Integer> points = new ArrayList<>();
		
		rounds.forEach(round -> {
			points.add(round.getLocalPoints());
			points.add(round.getVisitorPoints());
		});
		
		Collections.sort(points);
		Collections.reverse(points);
		
		rounds.forEach(round -> {
			round.setLocalPosition(getTeamPosition(points, round.getLocalPoints()));
			round.setVisitorPosition(getTeamPosition(points, round.getVisitorPoints()));
		});
	}
	
	private Integer getTeamPosition(List<Integer> points, int teamPoints) {
		
		int position = points.indexOf(teamPoints);
		points.set(points.lastIndexOf(teamPoints), -1);
		
		return position + 1;
	}
	
}
