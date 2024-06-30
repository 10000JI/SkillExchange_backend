package place.skillexchange.backend.exception.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import place.skillexchange.backend.common.annotation.ExplainError;
import place.skillexchange.backend.common.dto.ErrorReason;
import place.skillexchange.backend.exception.BaseErrorCode;

import java.lang.reflect.Field;
import java.util.Objects;

import static place.skillexchange.backend.common.consts.ConstFields.*;

@Getter
@AllArgsConstructor
public enum BoardErrorCode implements BaseErrorCode {

    @ExplainError("존재하지 않는 게시물 번호일 경우")
    BOARD_NOT_FOUND(NOT_FOUND, "BOARD_404_1", "존재하지 않는 게시물 입니다."),
    @ExplainError("이미 스크랩한 게시물일 경우")
    ALREADY_SCRAPPED(NOT_FOUND, "BOARD_404_2", "이미 스크랩한 게시물 입니다."),
    @ExplainError("이미 재능교환 요청이 된 게시물일 경우")
    ALREADY_REQUESTSKILL(NOT_FOUND, "BOARD_404_3", "재능교환 요청이 된 게시물입니다"),
    @ExplainError("자기 자신에게 재능교환 요청을 보내려는 경우")
    SELF_REQUESTSKILL(NOT_FOUND, "BOARD_404_4", "자기 자신에게 재능교환 요청은 불가능합니다."),
    @ExplainError("재능교환 요청 수락 파라미터가 잘못된 경우")
    INVALID_REQUEST_SKILL(NOT_FOUND, "BOARD_404_5", "요청한 게시물 번호 또는 게스트 ID가 유효하지 않습니다."),
    @ExplainError("등록되지 않은 장소를 재능교환 게시물 장소로 등록하려고 하는 경우")
    PLACE_NOT_FOUND(NOT_FOUND, "PLACE_404_1", "해당 장소는 등록되지 않은 장소입니다."),
    @ExplainError("등록되지 않은 분야를 재능교환 게시물 분야로 등록하려고 하는 경우")
    SUBJECT_CATEGORY_NOT_FOUND(NOT_FOUND, "SUBJECT_CATEGORY_404_1", "등록되지 않은 분야입니다."),
    @ExplainError("존재하지 않는 댓글 번호일 경우")
    COMMENT_NOT_FOUND(NOT_FOUND, "COMMENT_404_1", "존재하지 않는 댓글 입니다."),
    @ExplainError("게시물 번호가 null인 경우")
    BOARD_NUM_NOT_FOUND(NOT_FOUND, "COMMENT_404_2", "게시글 번호를 입력해주세요"),
    @ExplainError("중첩된 구조의 변환 작업에서 변환이 불가능한 상황을 나타나는 예외")
    NESTED_STRUCTURE_CONVERSION_FAILURE(INTERNAL_SERVER, "CATEGORY_500_1", "중첩된 구조를 변환하는 도중 문제가 발생했습니다. 데이터 구조를 다시 확인해주세요."),
    @ExplainError("부모 카테고리로 재능교환 게시물을 찾으려고 하는 경우 (자식 카테고리 별 재능교환 게시물 목록 출력 가능)")
    SUBJECT_CATEGORY_BAD_REQUEST(BAD_REQUEST, "SUBJECT_CATEGORY_400_1", "선택한 카테고리에 대한 게시물을 찾을 수 없습니다. 부모 카테고리에 속한 게시물은 확인할 수 없습니다. 자식 카테고리로 다시 시도해주세요.");

    private Integer status;
    private String code;
    private String reason;

    public ErrorReason getErrorReason() {
        return ErrorReason.builder().message(reason).code(code).status(status).build();
    }

    @Override
    public String getExplainError() throws NoSuchFieldException {
        Field field = this.getClass().getField(this.name());
        ExplainError annotation = field.getAnnotation(ExplainError.class);
        return Objects.nonNull(annotation) ? annotation.value() : this.getReason();
    }
}
