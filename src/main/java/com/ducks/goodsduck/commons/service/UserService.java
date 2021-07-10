package com.ducks.goodsduck.commons.service;

import com.ducks.goodsduck.commons.model.dto.*;
import com.ducks.goodsduck.commons.model.entity.SocialAccount;
import com.ducks.goodsduck.commons.model.entity.User;
import com.ducks.goodsduck.commons.model.enums.UserRole;
import com.ducks.goodsduck.commons.repository.SocialAccountRepository;
import com.ducks.goodsduck.commons.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final OauthKakaoService oauthKakaoService;
    private final OauthNaverService oauthNaverService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;

    // 네이버로 인증받기
    public UserDto oauth2AuthorizationNaver(String code, String state) {

        AuthorizationNaverDto authorizationNaverDto = oauthNaverService.callTokenApi(code, state);

        // 소셜로그인 정보
        String userInfoFromNaver = oauthNaverService.callGetUserByAccessToken(authorizationNaverDto.getAccess_token());

        // 소셜을 통해 가입한 ID가 있는지 체크
        JSONObject jsonUserInfo = new JSONObject(userInfoFromNaver);
        JSONObject jsonResponseInfo = new JSONObject(jsonUserInfo.get("response").toString());
        String userSocialAccountId = jsonResponseInfo.get("id").toString();

        // 회원 로그인, 비회원 로그인 체크
        return socialAccountRepository.findById(userSocialAccountId)
                .map(socialAccount -> {
                    User user = socialAccount.getUser();
                    UserDto userDto = new UserDto(user);
                    userDto.setSocialAccountId(userSocialAccountId);
                    userDto.setJwt(jwtService.createJwt(new JwtDto(user.getId()), 1 * 1000 * 60));
                    userDto.setRole(UserRole.USER);

                    return userDto;
                })
                .orElseGet(() -> {
                    UserDto userDto = new UserDto();
                    userDto.setSocialAccountId(userSocialAccountId);
                    userDto.setRole(UserRole.ANONYMOUS);

                    return userDto;
                });
    }

    // 카카오로 인증받기
    public UserDto oauth2AuthorizationKakao(String code) {

        AuthorizationKakaoDto authorizationKakaoDto = oauthKakaoService.callTokenApi(code);

        // 소셜로그인 정보
        String userInfoFromKakao = oauthKakaoService.callGetUserByAccessToken(authorizationKakaoDto.getAccess_token());

        // 비회원 체크
        JSONObject jsonUserInfo = new JSONObject(userInfoFromKakao);
        String userSocialAccountId = jsonUserInfo.get("id").toString();

        // 회원 로그인, 비회원 로그인 체크
        return socialAccountRepository.findById(userSocialAccountId)
                .map(socialAccount -> {
                    User user = socialAccount.getUser();
                    UserDto userDto = new UserDto(user);
                    userDto.setSocialAccountId(userSocialAccountId);
                    userDto.setJwt(jwtService.createJwt(new JwtDto(user.getId()), 1 * 1000 * 60));
                    userDto.setRole(UserRole.USER);

                    return userDto;
                })
                .orElseGet(() -> {
                    UserDto userDto = new UserDto();
                    userDto.setSocialAccountId(userSocialAccountId);
                    userDto.setRole(UserRole.ANONYMOUS);

                    return userDto;
                });
    }

    // 회원가입
    public UserDto signUp(UserSignUpRequestDto userSignUpRequestDto) {

        SocialAccount socialAccount = socialAccountRepository.save(
                new SocialAccount(
                        userSignUpRequestDto.getSocialAccountId(),
                        userSignUpRequestDto.getSocialAccountType()
                )
        );

        User user = userRepository.save(
                new User(userSignUpRequestDto.getNickName(),
                        userSignUpRequestDto.getEmail(),
                        userSignUpRequestDto.getPhoneNumber())
        );
        user.addSocialAccount(socialAccount);
        
        // jwt 발급(10분)
        String jwt = jwtService.createJwt(new JwtDto(user.getId()), 10 * 1000 * 60);

        UserDto userDto = new UserDto(user);
        userDto.setJwt(jwt);
        return userDto;
    }

//    // 유저 전체 리스트 조회
//    public List<UserDto> findAll(){
//        return userRepository.findAll()
//                .stream()
//                .map(user -> new UserDto())
//                .collect(Collectors.toList());
//    }
//
//    public UserDto find(Long user_id) {
//        return userRepository.findById(user_id)
//                .map(user -> new UserDto())
//                .orElseGet(() -> new UserDto()); // user를 못찾으면 빈 UserDto 반환 (임시)
//    }
}
