package com.martinb.sbsocial.images;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataMongoTest
public class EmbeddedBlockingImageServiceTests extends ImageRepositoryTests {


    @Before
    @Override
    public void setUp() {
        operations.dropCollection(Image.class);

        operations.insert(new Image("1", "test-image-1.jpg"));
        operations.insert(new Image("2", "test-image-2.jpg"));
        operations.insert(new Image("3", "test-image-3.jpg"));

        operations.findAll(Image.class).forEach(image -> {
            System.out.println(image.toString());
        });
    }

    @Test
    @Override
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
                .thenAwait(Duration.ofSeconds(10))
                .expectComplete()
                .verify();
    }
}
