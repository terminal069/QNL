package es.tml.qnl.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "statResultSequence")
public class StatResultSequence {

	@Id
	private String id;
	
	private String sequence;
	
	private int localWinner;
	
	private int tied;
	
	private int visitorWinner;
}
