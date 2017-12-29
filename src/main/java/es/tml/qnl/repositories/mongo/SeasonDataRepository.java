package es.tml.qnl.repositories.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.SeasonData;

public interface SeasonDataRepository extends MongoRepository<SeasonData, String> {

	@Query("{ 'currentSeason': false }")
	List<SeasonData> findNotCurrentSeasons();
	
	// Ojo con currentSeason. Sólo se tienen que cargar datos de rounds si currentSeason = false.
	// Se debería arrastrar de SeasonData el booleano y poner en cada Season si es current o no.
	// De esa forma al obtener las Seasons de cada League se puede filtrar y obtener sólo las que no son current
}
