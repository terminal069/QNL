package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "league")
public class League {

	private String code;
	
	private String name;
	
	private String prefix;
	
	private String suffix;
}
