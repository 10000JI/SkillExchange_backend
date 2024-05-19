package place.skillexchange.backend.common.util;

import place.skillexchange.backend.exception.board.CannotConvertNestedStructureException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// 세 개의 제너릭 파라미터 = K: 엔티티의 key 타입, E: 엔티티의 타입, D: 엔티티가 변환된 DTO의 타입.
public class NestedConvertHelper<K, E, D> {

    private List<E> entities; //플랫한 구조의 엔티티 목록
    private Function<E, D> toDto; //엔티티를 DTO로 변환해주는 Function
    private Function<E, E> getParent; //엔티티의 부모 엔티티를 반환해주는 Function
    private Function<E, K> getKey; //엔티티의 Key(우리는 id)를 반환해주는 Function
    private Function<D, List<D>> getChildren; //DTO의 children 리스트를 반환해주는 Function

    //NestedConvertHelper의 인스턴스를 생성하는 스태틱 팩토리 메소드 newInstance, 메소드 레벨에 제너릭 타입 파라미터를 지정
    public static <K, E, D> NestedConvertHelper newInstance(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren) {
        //전달받는 함수를 이용한 타입 추론에 의해 각각의 제너릭 타입이 추론
        return new NestedConvertHelper<K, E, D>(entities, toDto, getParent, getKey, getChildren);
    }

    //private 생성자를 이용하여 인스턴스를 생성하고 초기화
    private NestedConvertHelper(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren) {
        this.entities = entities;
        this.toDto = toDto;
        this.getParent = getParent;
        this.getKey = getKey;
        this.getChildren = getChildren;
    }

    //계층형 변환 작업
    public List<D> convert() {
        try {
            //부모 카테고리의 id를 오름차순으로 정렬
            //entities를 순차적으로 탐색하면서, 어떤 카테고리의 부모 카테고리 id는 반드시 해당 카테고리보다 앞서서 탐색
            return convertInternal();
        } catch (NullPointerException e) {
            //만약 그렇지 않다면, NullPointerExceptinon이 발생
            //CannotConvertNestedStructureException 예외가 발생
            throw CannotConvertNestedStructureException.EXCEPTION;
        }
    }

    private List<D> convertInternal() {
        //구현하기 위해 Map을 이용
        Map<K, D> map = new HashMap<>();
        //roots에는 자식 엔티티가 없는 루트 엔티티가 담기게 되고, 최종적인 반환 값은 roots
        List<D> roots = new ArrayList<>();

        // entities를 순차적으로 탐색
        for (E e : entities) {
            //탐색된 엔티티를 DTO로 변환 후
            D dto = toDto(e);
            //map에 넣어줌
            //이미 탐색된 부모 엔티티의 DTO는, 어떤 자식 엔티티를 탐색할 때 반드시 Map에 담겨있어야 함
            //그렇지 않다면 NullPointerException 예외가 발생하여 변환 작업이 실패
            map.put(getKey(e), dto);
            //부모가 있다면
            if (hasParent(e)) {
                E parent = getParent(e);
                K parentKey = getKey(parent);
                //Map에서 부모의 DTO를 찾아줌
                D parentDto = map.get(parentKey);
                //부모 DTO의 children으로, 지금 탐색하는 엔티티의 DTO를 넣어줌
                getChildren(parentDto).add(dto);
            } else {
                //부모가 없다면, 루트 엔티티
                roots.add(dto);
            }
        }
        return roots;
    }

    private boolean hasParent(E e) {
        return getParent(e) != null;
    }

    private E getParent(E e) {
        return getParent.apply(e);
    }

    private D toDto(E e) {
        return toDto.apply(e);
    }

    private K getKey(E e) {
        return getKey.apply(e);
    }

    private List<D> getChildren(D d) {
        return getChildren.apply(d);
    }
}