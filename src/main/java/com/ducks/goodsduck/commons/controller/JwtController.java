package com.ducks.goodsduck.commons.controller;

import com.ducks.goodsduck.commons.model.dto.JwtDto;
import com.ducks.goodsduck.commons.model.dto.UserDto;
import com.ducks.goodsduck.commons.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class JwtController {

    private final JwtService jwtService;

    @PostMapping("/gen/token")
    public Map<String, Object> genToken(@RequestBody JwtDto jwtDto) {

        System.out.println("===================================");
        System.out.println("/api/v1/gen/token");
        System.out.println("===================================");

        String token = jwtService.createJwt(jwtDto, (20 * 1000 * 60));

        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("result", token);
        return map;
    }

    @GetMapping("/get/subject")
    public Map<String, Object> getSubject(@RequestParam("token") String token) {

        System.out.println("===================================");
        System.out.println("/api/v1/get/subject");
        System.out.println("===================================");

        Long subject = jwtService.getUserIdFromJwt(token);

        System.out.println("subject = " +  subject);

        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("result", subject);
        return map;
    }

    // 비정상적인 사용자 접근 불가
    @GetMapping("/login")
    public UserDto isValidAccess(@RequestParam String token, HttpServletRequest request) {

        System.out.println("===================================");
        System.out.println("/api/v1/login(isValidAccess)");
        System.out.println("===================================");

        System.out.println(token.getClass());
        System.out.println("param token " + token);

//        String jwt = request.getHeader("jwt");
        String jwt = token;

        return jwtService.isValidAccess(jwt);
    }
}
