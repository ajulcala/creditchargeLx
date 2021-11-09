package com.creditcharge.app.models.dao;

import com.creditcharge.app.models.documents.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CreditDao extends ReactiveMongoRepository<Credit, String> {
    Flux<Credit> findByCreditCardId(String id);
}
