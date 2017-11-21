package es.tml.qnl.services.catalog.impl;

import org.springframework.stereotype.Service;

import es.tml.qnl.beans.catalog.LoadDataRequest;
import es.tml.qnl.beans.catalog.LoadDataResponse;
import es.tml.qnl.services.catalog.CatalogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CatalogServiceImpl implements CatalogService {

	@Override
	public LoadDataResponse loadData(LoadDataRequest request) {
		
		LoadDataResponse response = new LoadDataResponse();
		
		// Get data to load into DB
		if (request.isFullLoad()) {
			
		}
		
		// Store data into DB
		
		return response;
	}

}
