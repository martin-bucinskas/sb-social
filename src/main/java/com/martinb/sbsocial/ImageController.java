package com.martinb.sbsocial;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;

@Slf4j
@RestController
public class ImageController {

    private final String API_BASE_PATH = "/api";

    @GetMapping(API_BASE_PATH + "/images")
    Flux<Image> images() {
        return Flux.just(
                new Image("1", "test-image-1.jpg"),
                new Image("2", "test-image-2.jpg"),
                new Image("3", "test-image-3.jpg")
        );
    }

    @PostMapping(API_BASE_PATH + "/images")
    Mono<Void> create(@RequestBody Flux<Image> images) {
        return images.map(image -> {
            log.info("We will save " + image + " to a Reactive DB soon!");
            return image;
        })
                .then();
    }
}
