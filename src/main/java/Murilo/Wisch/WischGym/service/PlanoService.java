package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.entities.Plano;
import Murilo.Wisch.WischGym.dto.plano.PlanoCreateDTO;
import Murilo.Wisch.WischGym.dto.plano.PlanoResponseDTO;
import Murilo.Wisch.WischGym.dto.plano.PlanoUpdateDTO;
import Murilo.Wisch.WischGym.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanoService {

    private final PlanoRepository planoRepository;


    private PlanoResponseDTO toResponseDTO(Plano plano) {
        return new PlanoResponseDTO(
                plano.getId(),
                plano.getNome(),
                plano.getDescricao(),
                plano.getValor(),
                plano.getDuracaoMeses(),
                plano.isAtivo()
        );
    }

    public PlanoResponseDTO criar(PlanoCreateDTO dto){
        Plano plano = new Plano();
        plano.setNome(dto.nome());
        plano.setDescricao(dto.descricao());
        plano.setValor(dto.valor());
        plano.setDuracaoMeses(dto.duracaoMeses());
        plano.setAtivo(true);

        Plano salvo = planoRepository.save(plano);

        return toResponseDTO(salvo);
    }

    public List<PlanoResponseDTO> listar() {
        return planoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public PlanoResponseDTO buscarPorid(Long id) {
        Plano plano =  planoRepository.findById(id).orElseThrow(() -> new RuntimeException("Plano não encontrado"));
        return toResponseDTO(plano);
    }

    private Plano buscarEntityPorId(Long id) {
        return planoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));
    }


    public PlanoResponseDTO atualizar(Long id, PlanoUpdateDTO dto){
        Plano plano = buscarEntityPorId(id);

        plano.setNome(dto.getNome());
        plano.setDescricao(dto.getDescricao());
        plano.setValor(dto.getValor());
        plano.setDuracaoMeses(dto.getDuracaoMeses());

        if(dto.getAtivo() != null){
            plano.setAtivo(dto.getAtivo());
        }

        Plano salvo = planoRepository.save(plano);
        return toResponseDTO(salvo);
    }

    public void deletar(Long id){
        planoRepository.deleteById(id);
    }
}
