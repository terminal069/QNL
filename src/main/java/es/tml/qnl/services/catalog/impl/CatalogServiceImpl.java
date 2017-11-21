package es.tml.qnl.services.catalog.impl;

import org.springframework.stereotype.Service;

import es.tml.qnl.beans.catalog.LoadDataRequest;
import es.tml.qnl.beans.catalog.LoadDataResponse;
import es.tml.qnl.services.catalog.CatalogService;
import es.tml.qnl.util.enums.Leagues;
import es.tml.qnl.util.enums.Seasons;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CatalogServiceImpl implements CatalogService {

	@Override
	public LoadDataResponse loadData(LoadDataRequest request) {
		
		LoadDataResponse response = new LoadDataResponse();
		
		// Se obtienen los datos que hay que cargar en BBDD
		if (request.isFullLoad()) {
			log.info(Seasons.getTotalSeasons() + " " + Seasons.to_String());
			log.info(Leagues.getTotalLeagues() + " " + Leagues.to_String());
		}
		
		// Se guardan los datos en BBDD
		
		return response;
	}

}
