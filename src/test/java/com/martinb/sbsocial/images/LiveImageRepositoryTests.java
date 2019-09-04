package com.martinb.sbsocial.images;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class LiveImageRepositoryTests extends ImageRepositoryTests {

    @Before
    @Override
    public void setUp() {
        operations.dropCollection(Image.class); // <- Remove this for testing on live db?

        operations.insert(new Image("1", "test-image-1.jpg"));
        operations.insert(new Image("2", "test-image-2.jpg"));
        operations.insert(new Image("3", "test-image-3.jpg"));

        operations.findAll(Image.class).forEach(image -> {
            System.out.println(image.toString());
        });
    }
}
