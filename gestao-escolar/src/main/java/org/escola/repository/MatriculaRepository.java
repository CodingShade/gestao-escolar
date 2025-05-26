package org.escola.repository;

import org.escola.model.Matricula;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class MatriculaRepository {
    private final EntityManager em;

    public MatriculaRepository(EntityManager em) {
        this.em = em;
    }

    public void salvar(Matricula matricula) {
        em.getTransaction().begin();
        em.persist(matricula);
        em.getTransaction().commit();
    }

    public List<Matricula> buscarTodasComDetalhes() {
        return em.createQuery(
                        "SELECT m FROM Matricula m " +
                                "JOIN FETCH m.aluno " +
                                "JOIN FETCH m.curso", Matricula.class)
                .getResultList();
    }

    public Long contarMatriculasPorCurso(Long cursoId) {
        return em.createQuery(
                        "SELECT COUNT(m) FROM Matricula m WHERE m.curso.id = :cursoId", Long.class)
                .setParameter("cursoId", cursoId)
                .getSingleResult();
    }

    public Double calcularMediaIdadePorCurso(Long cursoId) {
        return em.createQuery(
                        "SELECT AVG(YEAR(CURRENT_DATE) - YEAR(a.dataNascimento)) " +
                                "FROM Matricula m JOIN m.aluno a WHERE m.curso.id = :cursoId", Double.class)
                .setParameter("cursoId", cursoId)
                .getSingleResult();
    }

    public Long contarMatriculasRecentesPorCurso(Long cursoId, LocalDate dataLimite) {
        return em.createQuery(
                        "SELECT COUNT(m) FROM Matricula m " +
                                "WHERE m.curso.id = :cursoId AND m.dataMatricula >= :dataLimite", Long.class)
                .setParameter("cursoId", cursoId)
                .setParameter("dataLimite", dataLimite)
                .getSingleResult();
    }

}