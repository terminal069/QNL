package es.tml.qnl.model.mongo;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "combination")
public class Combination {

	private String code;
	
	private BigDecimal interval;
	
	private int totalStats;
	
	private int hits;
	
	private BigDecimal hitPercentage;
	
	private List<Weight> weights;
}
