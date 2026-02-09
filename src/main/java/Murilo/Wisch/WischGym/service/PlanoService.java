package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.entities.Plano;
import Murilo.Wisch.WischGym.repository.PlanoRepository;

import java.util.List;

public class PlanoService {

    private final PlanoRepository planoRepository;

    public PlanoService(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    public Plano criar(Plano plano){
        return planoRepository.save(plano);
    }
    public List<Plano> listar(){
        return planoRepository.findAll();
    }

    public Plano buscarPorid(Long id) {
        return planoRepository.findById(id).orElseThrow(() -> new RuntimeException("Plano não encontrado"));
    }

    public Plano atualizar(Long id, Plano planoAtualizado){
        Plano plano = buscarPorid(id);

        plano.setNome(planoAtualizado.getNome());
        plano.setValor(planoAtualizado.getValor());
        plano.setDescricao(planoAtualizado.getDescricao());

        return planoRepository.save(plano);
    }
}
