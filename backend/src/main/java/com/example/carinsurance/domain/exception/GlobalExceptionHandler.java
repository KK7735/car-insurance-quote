package com.example.carinsurance.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// グローバル例外ハンドラ。下位層でスローされた特定の例外を集中的に標準化された HTTP レスポンスにマッピングし、Tomcat のデフォルトのエラーページがフロントエンドに公開されるのを防ぐ。
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bean Validation (例: @NotNull, @AssertTrue) によってスローされたバリデーション例外を統一的に処理し、400 Bad Request を返して、フィールドレベルとクラスレベルのエラーを集約する。
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        
        // フィールドレベルの基本的なバリデーションエラーを捕捉する
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage()));
        
        // @AssertTrue などのクラスレベルに基づくクロスフィールドバリデーションエラーを捕捉する
        ex.getBindingResult().getGlobalErrors().forEach(error -> 
            fieldErrors.put(error.getObjectName(), error.getDefaultMessage()));

        ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR", "入力チェックエラー", fieldErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("NOT_FOUND", ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AdminAuthException.class)
    public ResponseEntity<ErrorResponse> handleAdminAuthException(AdminAuthException ex) {
        ErrorResponse errorResponse = new ErrorResponse("UNAUTHORIZED", ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleSystemError(Exception ex) {
        // ドキュメントの要件を厳密に遵守する：詳細なスタックトレースは返さない
        ErrorResponse errorResponse = new ErrorResponse("SYSTEM_ERROR", "想定外エラーが発生しました", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
