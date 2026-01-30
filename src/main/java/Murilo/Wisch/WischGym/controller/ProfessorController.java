package Murilo.Wisch.WischGym.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professor")
public class ProfessorController {

    @GetMapping("/test")
    public String test(){
        return "Professor ok";
    }
}
