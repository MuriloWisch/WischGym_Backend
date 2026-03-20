package Murilo.Wisch.WischGym.controller;

import Murilo.Wisch.WischGym.dto.auth.UserCreateDTO;
import Murilo.Wisch.WischGym.dto.auth.UserResponseDTO;
import Murilo.Wisch.WischGym.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> criar(@RequestBody @Valid UserCreateDTO dto) {
        return ResponseEntity.ok(userService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listarTodos() {
        return ResponseEntity.ok(userService.listarTodos());
    }

    @GetMapping("/professores")
    public ResponseEntity<List<UserResponseDTO>> listarProfessores() {
        return ResponseEntity.ok(userService.listarProfessores());
    }
}
