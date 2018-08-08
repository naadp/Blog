package com.yao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/errorPage")
    public String errorPage(){
        return "redirect:/error/errorPage.jsp";
    }

}
