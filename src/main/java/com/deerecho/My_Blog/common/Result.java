package com.deerecho.My_Blog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> Result<T> success() {
        return success("操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(HttpStatus.OK.getCode());
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public static <T> Result<T> error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "操作失败");
    }

    public static <T> Result<T> error(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static <T> Result<T> error(HttpStatus httpStatus, String message) {
        Result<T> result = new Result<>();
        result.setCode(httpStatus.getCode());
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public boolean isSuccess() {
        return this.code != null && this.code == HttpStatus.OK.getCode();
    }

    public Result<T> withData(T data) {
        this.setData(data);
        return this;
    }

    public enum HttpStatus {
        OK(200, "OK"),
        CREATED(201, "Created"),
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        CONFLICT(409, "Conflict"),
        UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable");

        private final int code;
        private final String phrase;

        HttpStatus(int code, String phrase) {
            this.code = code;
            this.phrase = phrase;
        }

        public int getCode() {
            return code;
        }

        public String getPhrase() {
            return phrase;
        }
    }
}
