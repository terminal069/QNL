package es.tml.qnl.services.catalog.impl;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import es.tml.qnl.data.Teams;
import es.tml.qnl.exceptions.QNLException;
import es.tml.qnl.model.mongo.Team;
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
	
	@Autowired
	private TeamRepository teamRepository;
	
	private int round;
	
	@Override
	public void parseDataFromUrl(String url) {
		
		// Get document from url and parse it
		getDocument(url)
			.getElementsByClass(roundClassTag).forEach(roundElement -> {
				parseRound(roundElement);
			});
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
				round = Integer.parseInt(className.substring(2));
			}
		});
		
		roundElement.getElementsByClass(resultClassTag).forEach(resultElement -> {
			parseResults(resultElement);
		});
	}
	
	private void parseResults(Element resultElement) {
		
		String local = resultElement.getElementsByClass(localClassTag).first().text();
		String visitor = resultElement.getElementsByClass(visitorClassTag).first().text();
		String localRes = resultElement.getElementsByClass(localResClassTag).first().text();
		String visitorRes = resultElement.getElementsByClass(visitorResClassTag).first().text();
		
		loadTeam(local, visitor);
		
		log.info("Round" + round + ": " + local + " " + localRes + " - " + visitorRes + " " + visitor);
	}
	
	private void loadTeam(String... teams) {
		
		for (String team : teams) {
			if (!Teams.existTeam(team)) {
				teamRepository.save(new Team(team));
				Teams.addTeam(team);
			}
		}
	}

}
