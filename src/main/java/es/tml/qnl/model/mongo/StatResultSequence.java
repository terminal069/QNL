package es.tml.qnl.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NonNull;

@Data
@Document(collection = "statResultSequence")
public class StatResultSequence {

	@Id
	private String id;
	
	@NonNull
	private String sequence;
	
	private int localWinner;
	
	private int tied;
	
	private int visitorWinner;
}
