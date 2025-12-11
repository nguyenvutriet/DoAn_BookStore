package com.example.project_bookstore.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject {
    private HttpStatus status;
    private String message;
    private Object data;
}
