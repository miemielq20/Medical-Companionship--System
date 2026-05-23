package com.example.mc_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HomeSlideDTO {
    private Long id;
    private String stype;
    @JsonProperty("stype_link")
    private String stypeLink;
    private String title;
    @JsonProperty("stype_text")
    private String stypeText;
    @JsonProperty("pic_image_url")
    private String picImageUrl;
}
