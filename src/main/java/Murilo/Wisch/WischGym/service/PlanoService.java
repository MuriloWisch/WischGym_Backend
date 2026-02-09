package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.repository.PlanoRepository;

public class PlanoService {

    private final PlanoRepository planoRepository;

    public PlanoService(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }
}
