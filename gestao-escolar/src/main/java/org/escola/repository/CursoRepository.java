package org.escola.repository;

import org.escola.model.Curso;
import javax.persistence.EntityManager;
import java.util.List;

public class CursoRepository {
    private final EntityManager em;

    public CursoRepository(EntityManager em) {
        this.em = em;
    }

    public void salvar(Curso curso) {
        em.getTransaction().begin();
        em.persist(curso);
        em.getTransaction().commit();
    }

    public List<Curso> buscarTodos() {
        return em.createQuery("SELECT c FROM Curso c", Curso.class).getResultList();
    }

    public Curso buscarPorId(Long id) {
        return em.find(Curso.class, id);
    }

    public List<Curso> buscarPorNome(String nome) {
        return em.createQuery("SELECT c FROM Curso c WHERE LOWER(c.nome) LIKE LOWER(:nome)", Curso.class)
                .setParameter("nome", "%" + nome + "%")
                .getResultList();
    }
}