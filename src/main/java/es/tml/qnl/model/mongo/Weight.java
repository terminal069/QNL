package es.tml.qnl.model.mongo;

import java.math.BigDecimal;

import es.tml.qnl.services.statistics.util.StatisticsType.StatisticType;
import lombok.Data;

@Data
public class Weight {

	private StatisticType statistic;
	
	private BigDecimal weight;
}
