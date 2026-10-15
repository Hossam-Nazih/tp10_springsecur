package com.example.jpa.test;

import com.example.jpa.entities.Salle;
import com.example.jpa.entities.Utilisateur;
import com.example.jpa.service.ServiceGestion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ServiceGestionTest {

    private ServiceGestion service;

    @Before
    public void setUp() {
        service = new ServiceGestion();
    }

    @After
    public void tearDown() {
        service.fermer();
    }

    @Test
    public void testCreationEtRechercheSalle() {
        Salle salle = new Salle("Salle Test", 20);
        service.ajouterSalle(salle);

        List<Salle> salles = service.listerSalles();
        assertFalse(salles.isEmpty());
        assertTrue(salles.stream().anyMatch(s -> s.getNomSalle().equals("Salle Test")));
    }

    @Test
    public void testCreationUtilisateurAvecSalle() {
        Salle salle = new Salle("Salle B", 15);
        service.ajouterSalle(salle);

        Utilisateur utilisateur = new Utilisateur("Alice", "alice@example.com", salle);
        service.ajouterUtilisateur(utilisateur);

        List<Utilisateur> utilisateurs = service.listerUtilisateurs();
        assertTrue(utilisateurs.stream().anyMatch(u ->
                u.getNomPrenom().equals("Alice") && u.getSalleAffectee().getNomSalle().equals("Salle B")));
    }

    @Test
    public void testRechercheUtilisateursParSalle() {
        Salle salle = new Salle("Salle C", 30);
        service.ajouterSalle(salle);

        Utilisateur u1 = new Utilisateur("Bob", "bob@example.com", salle);
        Utilisateur u2 = new Utilisateur("Carol", "carol@example.com", salle);
        service.ajouterUtilisateur(u1);
        service.ajouterUtilisateur(u2);

        List<Utilisateur> utilisateurs = service.utilisateursParSalle("Salle C");
        assertEquals(2, utilisateurs.size());
    }
}
