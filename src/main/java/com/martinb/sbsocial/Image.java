package com.martinb.sbsocial;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Image {

    private int id;
    private String name;

    public Image(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
