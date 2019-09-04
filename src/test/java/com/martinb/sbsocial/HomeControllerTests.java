package com.martinb.sbsocial;

import com.martinb.sbsocial.images.Image;
import com.martinb.sbsocial.images.ImageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = HomeController.class)
@Import({ThymeleafAutoConfiguration.class})
public class HomeControllerTests {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ImageService imageService;

    @Test
    public void baseRouteShouldListAllImages() {
        Image testImage1 = new Image("1", "test-image-1.jpg");
        Image testImage2 = new Image("2", "test-image-2.jpg");

        given(imageService.findAllImages())
                .willReturn(Flux.just(testImage1, testImage2));

        EntityExchangeResult<String> result = webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).returnResult();

        verify(imageService).findAllImages();
        verifyNoMoreInteractions(imageService);
        assertThat(result.getResponseBody())
                .contains("<title>SBSocial</title>")
                .contains("<a href=\"/images/test-image-1.jpg/raw\">")
                .contains("<a href=\"/images/test-image-2.jpg/raw\">");
    }

    @Test
    public void fetchingImageTest() {
        given(imageService.findOneImage(any()))
        .willReturn(Mono.just(new ByteArrayResource("data".getBytes())));

        webTestClient.get()
                .uri("/images/test-image-1.jpg/raw")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("data");
        verify(imageService).findOneImage("test-image-1.jpg");
        verifyNoMoreInteractions(imageService);
    }

    @Test
    public void fetchingNullImageShouldFail() throws IOException {
        Resource resource = mock(Resource.class);

        given(resource.getInputStream())
                .willThrow(new IOException("Bad File"));
        given(imageService.findOneImage(any()))
                .willReturn(Mono.just(resource));

        webTestClient.get()
                .uri("/images/test-image-1.jpg/raw")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Couldn't find test-image-1.jpg => Bad File");

        verify(imageService).findOneImage("test-image-1.jpg");
        verifyNoMoreInteractions(imageService);
    }

    @Test
    public void deleteImageTest() {
        given(imageService.deleteImage(any())).willReturn(Mono.empty());

        webTestClient
                .delete().uri("/images/test-image-1.jpg")
                .exchange()
                .expectStatus().isSeeOther()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/");

        verify(imageService).deleteImage("test-image-1.jpg");
        verifyNoMoreInteractions(imageService);
    }
}
