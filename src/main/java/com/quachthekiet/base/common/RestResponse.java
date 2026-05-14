package com.quachthekiet.base.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestResponse<T> {
    private int code;
    private Object message;
    private T data;

    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>(200, "Success", data);
    }
}
