package es.tml.qnl.services.catalog;

import java.util.List;

import es.tml.qnl.beans.catalog.GetRoundRequest;
import es.tml.qnl.beans.catalog.GetRoundResponse;
import es.tml.qnl.beans.catalog.GetTeamsResponse;
import es.tml.qnl.beans.catalog.LoadDataRequest;

public interface CatalogService {

	void generateSeasons();
	
	void loadData(LoadDataRequest request);
	
	void loadAllData();

	List<GetRoundResponse> getRound(GetRoundRequest request);

	List<GetTeamsResponse> getTeams();
	
}
