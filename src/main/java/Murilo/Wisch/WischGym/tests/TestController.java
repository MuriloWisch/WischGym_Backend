package Murilo.Wisch.WischGym.tests;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

        @GetMapping("/test")
        public String test() {
            System.out.println("CONTROLLER FOI CHAMADO");
            return "Authenticado";
        }

    @PostMapping("/auth/register")
    public String register() {
        return "Register ok";
    }
}
