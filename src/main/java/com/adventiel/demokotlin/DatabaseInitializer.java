package com.adventiel.demokotlin;

import com.adventiel.demokotlin.model.Cow;
import com.adventiel.demokotlin.repository.CowRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.util.Arrays.asList;
import static reactor.core.publisher.Flux.fromIterable;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    private CowRepository cowRepository;

    public DatabaseInitializer(CowRepository cowRepository) {
        this.cowRepository = cowRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Cow marguerite = new Cow("Marguerite", LocalDateTime.of(2017, 9, 28, 13, 30));
        Cow laNoiraude = new Cow("La Noiraude");

        // uncomment if targetting a real MongoDB Database (not embedded)
//        cowRepository.deleteAll()
//                .block();

        fromIterable(asList(marguerite, laNoiraude))
                .flatMap( cow -> {
                    System.out.println("saving " + cow.getName());
                    return cowRepository.save(cow);
                }).blockLast();
    }
}
