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
}
