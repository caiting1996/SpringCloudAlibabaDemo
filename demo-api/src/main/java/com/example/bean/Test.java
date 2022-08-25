package com.example.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    private String version;

    private String productId;

    private Boolean isUsed;

    private Boolean isUpdated;

    private String hash;

    private String localUrl;
}
