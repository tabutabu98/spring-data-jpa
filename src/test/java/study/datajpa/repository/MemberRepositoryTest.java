package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.domain.Team;
import study.datajpa.dto.MemberDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;
    @Autowired
    MemberQueryRepository memberQueryRepository;

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 변경 감지
//        findMember1.setUsername("member!!!!!!");

        // 리스트 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findMember("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // List는 null로 반환되지 않는다.
        List<Member> result = memberRepository.findListByUsername("asdfasdf");
        System.out.println("result = " + result.size());

        // 단건일 경우 결과가 없을 시 null을 반환한다.(Spring Data JPA)
        Member findMember = memberRepository.findMemberByUsername("asdfasdf");
        System.out.println("findMember = " + findMember);

        /**
         * java 8 이후로는 Optional을 사용
         * 값이 2개 이상이라면 예외가 터진다.
         * Spring은 여러가지 기술과 연결할 것을 염두하여 동일한 예외를 내려준다.
         */
        Optional<Member> findMember2 = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findMember2 = " + findMember2);
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        /**
         * Page
         */
        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

//        for (Member member : content) {
//            System.out.println("member = " + member);
//        }
//        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);    // 조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5);   // 전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0);  // 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2);  // 전체 페이지 개수
        assertThat(page.isFirst()).isTrue();    // 첫번째 항목인가?
        assertThat(page.hasNext()).isTrue();    // 다음 페이지가 있는가?

        /**
         * Slice
         */
//        // when
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
//
//        // then
//        List<Member> content = page.getContent();
//
//        assertThat(content.size()).isEqualTo(3);    // 조회된 데이터 수
////        assertThat(page.getTotalElements()).isEqualTo(5);   // 전체 데이터 수
//        assertThat(page.getNumber()).isEqualTo(0);  // 페이지 번호
////        assertThat(page.getTotalPages()).isEqualTo(2);  // 전체 페이지 개수
//        assertThat(page.isFirst()).isTrue();    // 첫번째 항목인가?
//        assertThat(page.hasNext()).isTrue();    // 다음 페이지가 있는가?
    }

    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        /**
         * 벌크 연산은 DB에 바로 쿼리를 날려서 값을 변경하기 때문에 영속성 컨텍스트에서는 변경감지를하지 못한다.
         * 따라서 벌크 연산을 할 경우 flush와 clear을 진행해야한다.
         * 혹은 JpaRepository의 @Modifiying에서 clearAutomatically 옵션을 true로 해둔다.
         */
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5.getAge());

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // given
        /**
         * member1 -> teamA
         * member2 -> teamB
         */
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
//        Member member2 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

//        // when 'N + 1 이슈'
//        // select Member
//        List<Member> members = memberRepository.findAll();
//        for (Member member : members) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.teamClass = " + member.getTeam().getClass());
//            System.out.println("member.team = " + member.getTeam().getName());
//        }

//        // when 'fetch join'
//        List<Member> members = memberRepository.findMemberFetchJoin();
//        for (Member member : members) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.teamClass = " + member.getTeam().getClass());
//            System.out.println("member.team = " + member.getTeam().getName());
//        }

//        // when '@EntityGraph'
//        List<Member> members = memberRepository.findAll();
//        for (Member member : members) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.teamClass = " + member.getTeam().getClass());
//            System.out.println("member.team = " + member.getTeam().getName());
//        }

        // when 'EntityGraph + Username'
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

//        // when
//        // 변경감지가 일어남
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");
//        em.flush();
//        em.clear();

        // when 'QueryHint', 성능을 최적화하기 위한 기능(hibernate)
        // 변경감지가 일어남
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        System.out.println("findMember = " + findMember.getUsername());
        em.flush();
        em.clear();
    }

    @Test
    public void lock() {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        // 변경감지가 일어남
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        // given
        List<Member> result = memberRepository.findMemberCustom();
    }
    
    @Test
    public void specBasic() throws Exception {
        // given
        createTeamAndMember();

        // when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        // then
        assertThat(result.size()).isEqualTo(1);
    }
    
    @Test
    public void queryByExample() throws Exception {
        // given
        createTeamAndMember();

        // when
        /**
         * Probe
         */
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);
        /**
         * java primitive type 은 null 이 없기 때문에 이를 피해가기 위해서는 matcher을 사용하여 검색 조건에서 제외해야한다.
         */
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");
        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections() throws Exception {
        // given
        createTeamAndMember();

        // when
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");
        for (UsernameOnly usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
        }
    }

    @Test
    public void projections2() throws Exception {
        // given
        createTeamAndMember();

        // when
        List<UsernameOnlyDto> result = memberRepository.findProjections2ByUsername("m1", UsernameOnlyDto.class);
        for (UsernameOnlyDto usernameOnlyDto : result) {
            System.out.println("usernameOnlyDto = " + usernameOnlyDto.getUsername());
        }
    }

    @Test
    public void projections3() throws Exception {
        // given
        createTeamAndMember();

        // when
        List<NestedClosedProjections> result = memberRepository.findProjections2ByUsername("m1", NestedClosedProjections.class);
        for (NestedClosedProjections nestedClosedProjections : result) {
            String username = nestedClosedProjections.getUsername();
            System.out.println("username = " + username);
            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("teamName = " + teamName);
        }
    }

    @Test
    public void nativeQuery() throws Exception {
        // given
        createTeamAndMember();

        // when
        Member result = memberRepository.findByNativeQuery("m1");
        System.out.println("result = " + result);

        // then

    }

    @Test
    public void nativeQuery2() throws Exception {
        // given
        createTeamAndMember();

        // when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }

        // then

    }

    private void createTeamAndMember() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
    }
}