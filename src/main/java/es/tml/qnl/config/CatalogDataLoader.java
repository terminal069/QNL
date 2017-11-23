package es.tml.qnl.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import es.tml.qnl.data.Teams;
import es.tml.qnl.repositories.mongo.TeamRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CatalogDataLoader {

	@Autowired
	private TeamRepository teamRepository;
	
	@PostConstruct
	public void init() {
		
		log.info("Initializing team data");
		
		teamRepository.findAll().forEach(team -> {
			Teams.addTeam(team.getName());
		});
		
		log.debug(Teams.to_String());
	}
}
