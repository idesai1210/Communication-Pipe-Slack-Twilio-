package com.ishan.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by Ishan on 5/19/17.
 */
@RestController
public class HomeController {



    @RequestMapping("/greeting")
    public String greeting() {
        return "greeting.html";
    }
}
