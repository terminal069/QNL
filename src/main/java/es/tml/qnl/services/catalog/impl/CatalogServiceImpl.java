package es.tml.qnl.services.catalog.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.catalog.LoadDataRequest;
import es.tml.qnl.beans.catalog.LoadDataResponse;
import es.tml.qnl.exceptions.QNLException;
import es.tml.qnl.services.catalog.CatalogService;

@Service
public class CatalogServiceImpl implements CatalogService {

	@Override
	public LoadDataResponse loadData(LoadDataRequest request) {
		
		LoadDataResponse response = new LoadDataResponse();
		
		if (response.getMessage() == null) {
			throw new QNLException(HttpStatus.INTERNAL_SERVER_ERROR, "Generic error from hell");
		}
		
		return response;
	}

}
