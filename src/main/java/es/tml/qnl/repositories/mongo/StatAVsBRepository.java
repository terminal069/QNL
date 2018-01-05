package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatAVsB;

public interface StatAVsBRepository extends MongoRepository<StatAVsB, String> {

	@Query(value = "{ 'local': ?0, 'visitor': ?1 }")
	StatAVsB findByLocalAndVisitor(String local, String visitor);
}
