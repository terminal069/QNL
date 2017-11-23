package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "team")
public class Team {

	private String name;
	
	public Team(String name) {
		this.name = name;
	}
}
