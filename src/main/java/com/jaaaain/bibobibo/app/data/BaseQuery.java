package com.jaaaain.bibobibo.app.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseQuery {
    private Integer page;
    private Integer size;
    private String sort;
    private String order;
}
