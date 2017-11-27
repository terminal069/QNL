package es.tml.qnl.model.mongo;

import java.util.List;

import lombok.Data;

@Data
public class Season {

	private int code;
	
	private String name;
	
	private List<Round> rounds;
}
