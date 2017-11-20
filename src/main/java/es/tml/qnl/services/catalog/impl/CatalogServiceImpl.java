package es.tml.qnl.services.catalog.impl;

import org.springframework.stereotype.Service;

import es.tml.qnl.beans.catalog.LoadDataRequest;
import es.tml.qnl.beans.catalog.LoadDataResponse;
import es.tml.qnl.services.catalog.CatalogService;

@Service
public class CatalogServiceImpl implements CatalogService {

	@Override
	public LoadDataResponse loadData(LoadDataRequest request) {
		
		LoadDataResponse response = new LoadDataResponse();
		
		return response;
	}

}
