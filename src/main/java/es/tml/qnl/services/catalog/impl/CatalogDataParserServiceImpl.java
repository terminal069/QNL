package es.tml.qnl.services.catalog.impl;

import java.io.IOException;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import es.tml.qnl.exceptions.QNLException;
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
	
	@Override
	public void parseDataFromUrl(String url) {
		
		// Get elements from url and class tag and parse them
		getDocument(url)
			.getElementsByClass(roundClassTag).forEach(round -> {
				round.getElementsByClass(resultClassTag).forEach(result -> {
					parseResults(result);
				});
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
	
	private void parseResults(Element result) {
		
		String local = result.getElementsByClass(localClassTag).first().text();
		String visitor = result.getElementsByClass(visitorClassTag).first().text();
		String localRes = result.getElementsByClass(localResClassTag).first().text();
		String visitorRes = result.getElementsByClass(visitorResClassTag).first().text();
		
		
	}

}
