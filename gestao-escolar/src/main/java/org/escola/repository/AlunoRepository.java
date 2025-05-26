package org.escola.repository;

import org.escola.model.Aluno;
import javax.persistence.EntityManager;
import java.util.List;

public class AlunoRepository {
    private final EntityManager em;

    public AlunoRepository(EntityManager em) {
        this.em = em;
    }

    public void salvar(Aluno aluno) {
        em.getTransaction().begin();
        em.persist(aluno);
        em.getTransaction().commit();
    }

    public List<Aluno> buscarTodos() {
        return em.createQuery("SELECT a FROM Aluno a", Aluno.class).getResultList();
    }

    public Aluno buscarPorId(Long id) {
        return em.find(Aluno.class, id);
    }

    public Aluno buscarPorEmail(String email) {
        try {
            return em.createQuery("SELECT a FROM Aluno a WHERE a.email = :email", Aluno.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}