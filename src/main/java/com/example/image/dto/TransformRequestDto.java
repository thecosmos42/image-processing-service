package com.example.image.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransformRequestDto {

    private Resize resize;
    private Crop crop;
    private Integer rotate;
    private String format;
    private Filters filters;


    public static class Resize{
        private Integer width;
        private Integer height;
    }

    public static class Crop{
        private Integer width;
        private Integer height;
        private Integer x;
        private Integer y;
    }

    public static class Filters{
        private Boolean sepia;
        private Boolean grayscale;
    }
}
