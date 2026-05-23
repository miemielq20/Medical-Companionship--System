package com.example.mc_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HomeNavDTO extends HomeSlideDTO {
    @JsonProperty("cat_text")
    private String catText;
    private String tcolor;
}
