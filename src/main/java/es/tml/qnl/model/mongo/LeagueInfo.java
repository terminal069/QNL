package es.tml.qnl.model.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "leagueInfo")
public class LeagueInfo {

	private String code;
	
	private String name;
	
	private List<SeasonInfo> seasonsInfo;
}
