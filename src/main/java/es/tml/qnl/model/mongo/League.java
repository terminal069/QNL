package es.tml.qnl.model.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "league")
public class League {

	private String code;
	
	private List<Season> seasons;
}
