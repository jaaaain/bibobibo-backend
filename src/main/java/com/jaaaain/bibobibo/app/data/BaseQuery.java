package com.jaaaain.bibobibo.app.data;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseQuery {
    @Min(1)
    private Integer page = 1; // 大于1，默认为1
    @Min(1)
    private Integer size = 10; // 大于1，默认为10
    private String sort;
    private String order;
}
