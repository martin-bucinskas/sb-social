package com.martinb.sbsocial;

import com.martinb.sbsocial.images.Image;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class InitDatabase {

    @Bean
    CommandLineRunner init(MongoOperations mongoOperations) {
        return args -> {
          mongoOperations.dropCollection(Image.class);

          // Preloading sample data
          mongoOperations.insert(new Image("1", "test-image-1.jpg"));
          mongoOperations.insert(new Image("2", "test-image-2.jpg"));
          mongoOperations.insert(new Image("3", "test-image-3.jpg"));

          mongoOperations.findAll(Image.class).forEach(image -> {
              System.out.println(image.toString());
          });
        };
    }
}
