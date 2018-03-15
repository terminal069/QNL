package es.tml.qnl.beans.prediction;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticWeight {

	private String name;
	
	private BigDecimal weight;
}
