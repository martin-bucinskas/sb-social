package com.martinb.sbsocial.images;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataMongoTest
public class EmbeddedImageRepositoryTests extends ImageRepositoryTests {

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
}
