package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "season")
public class Season {

	private String season;
}
