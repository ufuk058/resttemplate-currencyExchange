package com.rest_template.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseWrapper {
    private boolean success;
    private String message;
    private Integer code;
    private Object data;
}
