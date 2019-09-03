package com.martinb.sbsocial.images;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@NoArgsConstructor
public class Image {

    @Id
    private String id;
    private String name;
    private List<String> tags;

    public Image(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
