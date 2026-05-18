package com.example.mc_server.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeleteCompanionRequest {
    private List<CompanionIdItem> id;
}
