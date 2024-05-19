package place.skillexchange.backend.talent.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class SubjectCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long id;

    //카테고리 이름
    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    //부모 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubjectCategory parent;

    public SubjectCategory(String subjectName, SubjectCategory parent) {
        this.subjectName = subjectName;
        this.parent = parent;
    }
}
