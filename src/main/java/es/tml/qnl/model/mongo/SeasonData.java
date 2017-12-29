package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "seasonData")
public class SeasonData {

	private int year;
	
	private String seasonRange;
}
