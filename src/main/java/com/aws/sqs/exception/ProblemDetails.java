package com.aws.sqs.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProblemDetails extends Throwable {
    private int status;
    private String message;
    private String description;
}
