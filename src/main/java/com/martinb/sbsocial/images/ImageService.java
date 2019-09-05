package com.martinb.sbsocial.images;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;
import static org.springframework.data.mongodb.core.query.Criteria.byExample;

@Service
public class ImageService {

    private static String UPLOAD_ROOT = "image-uploads";

    private final ResourceLoader resourceLoader;
    private final ImageRepository imageRepository;
    private final MeterRegistry meterRegistry;

    @Autowired
    private ReactiveMongoOperations mongoOperations;

    public ImageService(ResourceLoader resourceLoader, ImageRepository imageRepository, MeterRegistry meterRegistry) {
        this.resourceLoader = resourceLoader;
        this.imageRepository = imageRepository;
        this.meterRegistry = meterRegistry;
    }

    public Flux<Image> findAllImages() {
        return imageRepository.findAll()
                .log("findAll");
    }

    public Mono<Resource> findOneImage(String filename) {
        return Mono.fromSupplier(() -> resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename))
                .log("findOneImage");
    }

    public Mono<Void> createImage(Flux<FilePart> files) {
        return files.log("createImage-files").flatMap(file -> {
            Mono<Image> saveDatabaseImage = imageRepository.save(new Image(UUID.randomUUID().toString(), file.filename()))
                    .log("createImage-save");

            Mono<Void> copyFile = Mono.just(Paths.get(UPLOAD_ROOT, file.filename()).toFile())
                    .log("createImage-pickTarget")
                    .map(destFile -> {
                        try {
                            destFile.createNewFile();
                            return destFile;
                        } catch(IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .log("createImage-newFile")
                    .flatMap(file::transferTo)
                    .log("createImage-copy");

            Mono<Void> countFile = Mono.fromRunnable(() -> {
                meterRegistry
                        .summary("files.uploaded.bytes")
                        .record(Paths.get(UPLOAD_ROOT, file.filename()).toFile().length());
            });

            return Mono.when(saveDatabaseImage, copyFile, countFile)
                    .log("createImage-when");
        })
                .log("createImage-flatMap")
                .then()
                .log("createImage-done");
    }

    public Mono<Void> deleteImage(String filename) {
        Mono<Void> deleteDatabaseImage = imageRepository.findByName(filename)
                .log("deleteImage-find")
                .flatMap(imageRepository::delete)
                .log("deleteImage-record");

        Mono<Object> deleteFile = Mono.fromRunnable(() -> {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        })
                .log("deleteImage-file");

        return Mono.when(deleteDatabaseImage, deleteFile)
                .log("deleteImage-when")
                .then()
                .log("deleteImage-then");
    }
}
