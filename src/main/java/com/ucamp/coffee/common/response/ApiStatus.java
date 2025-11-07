package com.ucamp.coffee.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiStatus {
    /* 성공 */
    OK(HttpStatus.OK, "요청 처리에 성공했습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT, "내용이 존재하지 않습니다."),

    /* 실패 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메소드입니다."),
    CONFLICT(HttpStatus.BAD_REQUEST, "현재 사용 중인 구독권이 존재하여 상태를 변경할 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스를 사용할 수 없습니다."),
    MENU_LINKED_TO_SUBSCRIPTION(HttpStatus.BAD_REQUEST, "해당 메뉴가 구독권에 포함되어 있어 이름, 타입, 상태를 수정할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}