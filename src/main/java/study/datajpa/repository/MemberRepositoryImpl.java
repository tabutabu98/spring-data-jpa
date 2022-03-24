package study.datajpa.repository;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import study.datajpa.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor    // private final
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
