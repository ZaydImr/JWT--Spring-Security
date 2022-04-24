package com.security.jwt.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse {

    private int httpStatusCode; // 200, 404, ..
    private HttpStatus httpStatus;
    private String reason;
    private String message;

}
