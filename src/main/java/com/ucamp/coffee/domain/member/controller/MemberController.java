package com.ucamp.coffee.domain.member.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

    @GetMapping("/signup")
    public String signupPage(){
        return "member/signup";
    }
}