package org.escola;

import org.escola.service.EscolaService;
import javax.persistence.*;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("escola-pu");
        EntityManager em = emf.createEntityManager();

        EscolaService escolaService = new EscolaService(em);
        escolaService.iniciar();

        em.close();
        emf.close();
    }
}