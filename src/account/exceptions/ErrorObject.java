package account.exceptions;


import lombok.Data;

import java.time.LocalDate;

@Data
public class ErrorObject {
    private LocalDate timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
