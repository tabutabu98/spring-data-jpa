package study.datajpa.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private String username;

    /**
     * Entity는 기본 생성자가 필요하다.
     * 프록시 기술과 조인하기 위해서
     */
    protected Member() {
    }

    public Member(String username) {
        this.username = username;
    }
}
