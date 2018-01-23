package es.tml.qnl.services.statistics;

import es.tml.qnl.beans.statistics.StatisticsRequest;
import es.tml.qnl.enums.StatisticsType;

public interface StatisticsService {

	void calculateStatistics(StatisticsRequest request, StatisticsType statisticsType);
	
}
