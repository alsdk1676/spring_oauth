package com.app.oauth.mapper;

import com.app.oauth.domain.OauthMemberVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class OauthMapperTests {

    @Autowired
    private MemberMapper memberMapper;

//    회원조회 테스트
    @Test
    public void selectTest() {
        log.info("{}", memberMapper.select(1L));
    }

//    회원 전체 조회 테스트
    @Test
    public void selectAllTest() {
        log.info("{}", memberMapper.selectAll());
    }

//    이메일로 아이디 찾기 테스트
    @Test
    public void selectByIdTest() {
        log.info("{}", memberMapper.selectByEmail("test1234@gmail.com"));
    }

//    회원가입 테스트
    @Test
    public void insertTest() {
        OauthMemberVO oauthMemberVO = new OauthMemberVO();
        oauthMemberVO.setMemberEmail("test12345@gmail.com");
        oauthMemberVO.setMemberPassword("1234");
        oauthMemberVO.setMemberName("홍길동");
        memberMapper.insert(oauthMemberVO);
    }

//    회원 수정 테스트
    @Test
    public void updateTest() {
//        기존의 컬럼들 다 넣어줘야 함!
//        조회 먼저 (이메일, 비밀번호로)
//        세션
//        OauthMemberVO oauthMemberVO = new OauthMemberVO(); // 세션
//        oauthMemberVO.setMemberEmail("test12345@gmail.com"); // 세션
//        id 먼저 찾아오기
        Long memberId = memberMapper.selectByEmail("test12345@gmail.com"); // 세션에서 가져올 정보
        memberMapper.select(memberId).ifPresent(member -> { // 존재할때만 update -> 받아야함
            OauthMemberVO oauthMemberVO = new OauthMemberVO();
            oauthMemberVO.setId(member.getId());
            oauthMemberVO.setMemberEmail("test123@gmail.com");
            oauthMemberVO.setMemberPassword("12345678");
            oauthMemberVO.setMemberName(member.getMemberName());
            oauthMemberVO.setMemberNickName("개복치 2단계");
            oauthMemberVO.setMemberPicture(member.getMemberPicture());
            oauthMemberVO.setMemberProvider(member.getMemberProvider());
            memberMapper.update(oauthMemberVO);
        }); // 옵셔널 객체 타입

    }
//    회원 탈퇴 테스트
    @Test
    public void deleteTest() {
        Long memberId = memberMapper.selectByEmail("test1234@gmail.com");
        memberMapper.delete(memberId);
    }
}
