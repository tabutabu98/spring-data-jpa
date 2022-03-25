package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    /**
     * 인터페이스기반의 클로즈 프로젝션
     */
//    String getUsername();

    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
