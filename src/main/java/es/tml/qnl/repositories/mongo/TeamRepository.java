package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.tml.qnl.model.mongo.Team;

public interface TeamRepository extends MongoRepository<Team, String> {

}
