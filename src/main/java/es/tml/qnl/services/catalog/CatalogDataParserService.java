package es.tml.qnl.services.catalog;

import es.tml.qnl.model.mongo.Season;

public interface CatalogDataParserService {

	void parseDataFromUrl(String leagueCode, Season season);
}
