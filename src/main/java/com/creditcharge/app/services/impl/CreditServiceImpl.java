package com.creditcharge.app.services.impl;

import com.creditcharge.app.models.dao.CreditDao;
import com.creditcharge.app.models.documents.Credit;
import com.creditcharge.app.models.dto.CreditCard;
import com.creditcharge.app.services.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
public class CreditServiceImpl implements CreditService {
    private final WebClient webClient;
    private final ReactiveCircuitBreaker reactiveCircuitBreaker;
    @Value("${config.base.apigatewey}")
    String url;

    public CreditServiceImpl(ReactiveResilience4JCircuitBreakerFactory circuitBreakerFactory) {
        this.webClient = WebClient.builder().baseUrl(this.url).build();
        this.reactiveCircuitBreaker = circuitBreakerFactory.create("creditcard");
    }

    @Autowired
    CreditDao dao;

    @Override
    public Mono<CreditCard> findCreditCard(String id) {
        return reactiveCircuitBreaker.run(webClient.get().uri(this.url,id).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(CreditCard.class),
                throwable -> {return this.getDefaultCreditCard(); });
    }

    public Mono<CreditCard> getDefaultCreditCard() {
        Mono<CreditCard> creditCard = Mono.just(new CreditCard("0", null, null,null,null,null));
        return creditCard;
    }

    @Override
    public Mono<Credit> create(Credit t) {
        return dao.save(t);
    }

    @Override
    public Flux<Credit> findAll() {
        return dao.findAll();
    }

    @Override
    public Mono<Credit> findById(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<Credit> update(Credit t) {
        return dao.save(t);
    }

    @Override
    public Mono<Boolean> delete(String t) {
        return dao.findById(t)
                .flatMap(credit -> dao.delete(credit).then(Mono.just(Boolean.TRUE)))
                .defaultIfEmpty(Boolean.FALSE);
    }

    @Override
    public Mono<Long> findCountCreditCardId(String t) {
        return dao.findByCreditCardId(t).count();
    }

    @Override
    public Mono<Double> findTotalConsumptionCreditCardId(String t) {
        return dao.findByCreditCardId(t)
                .collectList()
                .map(credit -> credit.stream().mapToDouble(cdt -> cdt.getAmount()).sum());
    }
}
