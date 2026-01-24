package Murilo.Wisch.WischGym.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
        @GetMapping("/test")
        public String test(){
            return "Security functional 🚀";
        }
}
