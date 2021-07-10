package com.ducks.goodsduck.commons.controller;

import com.ducks.goodsduck.commons.model.dto.UserDto;
import com.ducks.goodsduck.commons.model.dto.UserSignUpRequestDto;
import com.ducks.goodsduck.commons.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @GetMapping("/oauth2/authorization/naver")
    public UserDto oauth2AuthorizationNaver(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) {

        return userService.oauth2AuthorizationNaver(code, state);
    }

    @GetMapping("/oauth2/authorization/kakao")
    public UserDto oauth2AuthorizationKakao(@RequestParam("code") String code) {

        System.out.println("===================================");
        System.out.println("/api/v1/oauth2/authorization/kakao");
        System.out.println("===================================");

        return userService.oauth2AuthorizationKakao(code);
    }

    @PostMapping("/signup")
    public UserDto userSignUp(@RequestBody UserSignUpRequestDto userSignUpRequestDto, HttpServletRequest request) {

        System.out.println("===================================");
        System.out.println("/api/v1/signup");
        System.out.println("===================================");

        return userService.signUp(userSignUpRequestDto);
    }

//    @GetMapping("/user")
//    public List<UserDto> getUserList() {
//        return userService.findAll();
//    }
//
//    @GetMapping("/user/{user_id}")
//    public UserDto getUser(@RequestParam Long user_id) {
//        return userService.find(user_id);
//    }

}
