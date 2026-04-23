package com.unievent.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to handle all fallback errors and override the default Spring Boot Whitelabel Error Page.
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // Return the path to the custom error page (serves static/error.html)
        return "forward:/error.html";
    }

}
