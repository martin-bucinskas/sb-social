package com.martinb.sbsocial;

import com.martinb.sbsocial.images.ImageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.IOException;

@Controller
public class HomeController {

    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{filename:.+}";

    private final ImageService imageService;

    public HomeController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = BASE_PATH + "/" + FILENAME + "/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<?>> oneRawImage(@PathVariable String filename) {
        return imageService.findOneImage(filename).log("oneRawImage-findOneImage").map(resource -> {
            try {
                return ResponseEntity.ok()
                        .contentLength(resource.contentLength())
                        .body(new InputStreamResource(resource.getInputStream()));
            } catch(IOException e) {
                return ResponseEntity.badRequest().body("Couldn't find " + filename + " => " + e.getMessage());
            }
        })
                .log("oneRawImage-map");
    }

    @PostMapping(value = BASE_PATH)
    public Mono<String> createFile(@RequestPart(name = "file") Flux<FilePart> files) {
        return imageService.createImage(files)
                .log("createFile-createImage")
                .then(Mono.just("redirect:/").log("createFile-just"))
                .log("createFile-then");
    }

    @DeleteMapping(BASE_PATH + "/" + FILENAME)
    public Mono<String> deleteFile(@PathVariable String filename) {
        return imageService.deleteImage(filename)
                .log("deleteFile-deleteImage")
                .then(Mono.just("redirect:/").log("deleteFile-just"))
                .log("deleteFile-then");
    }

    @GetMapping("/")
    public Mono<String> index(Model model) {
        model.addAttribute("images", imageService.findAllImages());
        return Mono.just("index").log("index-just");
    }
}
