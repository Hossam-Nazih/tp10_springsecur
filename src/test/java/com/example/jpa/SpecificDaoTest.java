package com.example.jpa;

import com.example.jpa.dao.SalleDao;
import com.example.jpa.dao.UtilisateurDao;
import com.example.jpa.entities.Salle;
import com.example.jpa.entities.Utilisateur;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpecificDaoTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static SalleDao salleDao;
    private static UtilisateurDao utilisateurDao;

    @BeforeClass
    public static void setUp() {
        emf = Persistence.createEntityManagerFactory("gestionPU");
        em = emf.createEntityManager();
        salleDao = new SalleDao(em);
        utilisateurDao = new UtilisateurDao(em);
    }

    @AfterClass
    public static void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void findByCapaciteMinAndSalleNom() {
        Salle grande = new Salle("Grande Salle", 50);
        Salle petite = new Salle("Petite Salle", 5);
        salleDao.create(grande);
        salleDao.create(petite);

        Utilisateur bob = new Utilisateur("Bob", "bob@example.com", grande);
        utilisateurDao.create(bob);

        List<Salle> grandes = salleDao.findByCapaciteMin(20);
        assertTrue(grandes.stream().anyMatch(s -> s.getNomSalle().equals("Grande Salle")));
        assertTrue(grandes.stream().noneMatch(s -> s.getNomSalle().equals("Petite Salle")));

        List<Utilisateur> occupants = utilisateurDao.findBySalleNom("Grande Salle");
        assertEquals(1, occupants.size());
        assertEquals("Bob", occupants.get(0).getNomPrenom());
    }
}
