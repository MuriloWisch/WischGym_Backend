package Murilo.Wisch.WischGym;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class WischGymApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(WischGymApplication.class, args);
	}

	@PostConstruct
	public void gerarSenha() {
		String hash = passwordEncoder.encode("123456");
		System.out.println("HASH GERADO AGORA => " + hash);
	}
}
