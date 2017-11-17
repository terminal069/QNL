package es.tml.qnl.services.catalog;

import es.tml.qnl.beans.catalog.LoadDataRequest;
import es.tml.qnl.beans.catalog.LoadDataResponse;

public interface CatalogService {

	public LoadDataResponse loadData(LoadDataRequest request);
}
