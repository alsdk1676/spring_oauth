package com.app.oauth.controller;

import com.app.oauth.domain.OauthMemberVO;
import com.app.oauth.service.MemberService;
import com.app.oauth.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member/*")
public class AuthController {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

//    회원가입
    @PostMapping("register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody OauthMemberVO oauthMemberVO) {
        Map<String, Object> response = new HashMap<>();

//        회원가입 시키고, 다시 로그인을 하게 만든다.
//        early return
        Long memberId = memberService.getMemberIdByMemberEmail(oauthMemberVO.getMemberEmail());
//        null과 같다고 처리하면 회원가입 시킬 수 없음!
        if(memberId != null) {
            OauthMemberVO foundUser = memberService.getMemberById(memberId).orElse(null);

            if(foundUser != null && foundUser.getMemberEmail().equals(oauthMemberVO.getMemberEmail())) {
                response.put("message", "이미 사용중인 아이디입니다.");
                response.put("provider", foundUser.getMemberProvider());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

//        소셜 로그인인지 검사 (아니라면 자사 로그인으로 null 값 넣어줌 => 동적쿼리로 default 설정해놨기 때문에 따로 검사 안해도됨)
//        회원가입
        memberService.register(oauthMemberVO);
        response.put("message", "회원가입이 완료되었습니다. 반갑습니다🤗");
        return ResponseEntity.ok(response);
    }


//    로그인
    @PostMapping("login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody OauthMemberVO oauthMemberVO) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();

        Long memberId = memberService.getMemberIdByMemberEmail(oauthMemberVO.getMemberEmail());

//        방어 코드1 : 이메일 검사 (null 처리 먼저)
        if(memberId != null) {
            response.put("message", "등록되지 않은 이메일입니다.😢");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 상태 오류를 보내고 body에 메세지 보내기 => return 타입 : ResponseEntity
//            response 객체인데 body에 map을 보냄 => 리턴타입 : ResponseEntity<Map<String, Object>>
        }

//        유저를 찾는다.
        OauthMemberVO foundUser = memberService.getMemberById(memberId).orElse(null);

//        방어코드2 : 비밀번호 검사
        if(foundUser.getMemberPassword().equals(oauthMemberVO.getMemberPassword())) {
            response.put("message", "비밀번호가 틀렸습니다.😅");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

//        아이디와 비밀번호가 일치하는 유저
//        토큰 발급 후 응닫ㅂ
        claims.put("email", oauthMemberVO.getMemberEmail());
        claims.put("name", oauthMemberVO.getMemberName());
        String jwtToken = jwtTokenUtil.generateToken(claims);
        response.put("jwtToken", jwtToken);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


//    로그인 이후 이용해야 하는 페이지

}
