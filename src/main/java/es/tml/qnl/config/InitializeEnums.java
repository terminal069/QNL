package es.tml.qnl.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import es.tml.qnl.repositories.mongo.LeagueRepository;
import es.tml.qnl.repositories.mongo.SeasonRepository;
import es.tml.qnl.util.enums.Leagues;
import es.tml.qnl.util.enums.Seasons;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class InitializeEnums {
	
	@Autowired
	private SeasonRepository seasonRepository;
	
	@Autowired
	private LeagueRepository leagueRepository;

	@PostConstruct
	public void init() {
		
		// Seasons
		log.info("Initializing seasons enum");
		
		seasonRepository.findAll().forEach(season -> {
			Seasons.addSeason(season.getSeason());
		});
		
		log.info(Seasons.to_String());
		
		// Leagues
		log.info("Initializing leagues enum");
		
		leagueRepository.findAll().forEach(league -> {
			Leagues.addLeague(league.getLeague());
		});
		
		log.info(Leagues.to_String());
		
	}
}
