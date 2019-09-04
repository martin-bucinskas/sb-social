package com.martinb.sbsocial.images;

import com.mongodb.Mongo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ImageRepositoryTests {

    @Autowired
    ImageRepository repository;

    @Autowired
    MongoOperations operations;

    @Before
    public abstract void setUp();

    @Test
    public void findAllImages() {
        Flux<Image> images = repository.findAll();
        StepVerifier.create(images)
                .recordWith(ArrayList::new)
                .expectNextCount(3)
                .consumeRecordedWith(results -> {
                    assertThat(results).hasSize(3);
                    assertThat(results)
                            .extracting(Image::getName)
                            .contains("test-image-1.jpg",
                                    "test-image-2.jpg",
                                    "test-image-3.jpg");
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void findByName() {
        Mono<Image> image = repository.findByName("test-image-1.jpg");
        StepVerifier.create(image)
                .expectNextMatches(results -> {
                    assertThat(results.getName()).isEqualTo("test-image-1.jpg");
                    assertThat(results.getId()).isEqualTo("1");
                    return true;
                });
    }
}
