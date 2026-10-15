package com.example.jpa;

import com.example.jpa.entities.Salle;
import com.example.jpa.entities.Utilisateur;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.assertNotNull;

public class PersistenceSmokeTest {

    @Test
    public void persistAndFindSalle() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gestionPU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        Salle salle = new Salle("Salle A", 10);
        Utilisateur utilisateur = new Utilisateur("Alice", "alice@example.com", salle);
        em.persist(salle);
        em.persist(utilisateur);
        em.getTransaction().commit();

        assertNotNull(salle.getNumSalle());
        assertNotNull(utilisateur.getNumUtilisateur());

        em.close();
        emf.close();
    }
}
