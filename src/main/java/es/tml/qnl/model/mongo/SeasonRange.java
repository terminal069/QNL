package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "seasonRange")
public class SeasonRange {

	private int year;
	
	private String seasonRange;
}
