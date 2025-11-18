package com.bit.common.web.handler;

import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.exception.base.BaseException;
import com.bit.common.core.enums.exception.ErrorCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Datetime: 2025年11月17日21:52
 * @Author: Eleven52AC
 * @Description:
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有自定义业务异常
     * @Author: Eleven52AC
     * @Description:
     * @param e
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public ApiResponse<?> handleBaseException(BaseException e) {
        return ApiResponse.error(e.getCode(), e.getMsg());
    }

    /**
     * 处理参数校验异常
     * @Author: Eleven52AC
     * @Description:
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidException(MethodArgumentNotValidException e) {
        return ApiResponse.error(
                ErrorCode.BAD_REQUEST.getCode(),
                e.getBindingResult().getFieldError().getDefaultMessage()
        );
    }

    /**
     * 捕获未预期的异常（兜底）
     * @Author: Eleven52AC
     * @Description:
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleOtherException(Exception e) {
        return ApiResponse.error(
                ErrorCode.SYSTEM_ERROR.getCode(),
                e.getMessage()
        );
    }

}
