package com.example.jpa.test;

import com.example.jpa.entities.Reservation;
import com.example.jpa.entities.Salle;
import com.example.jpa.entities.StatutReservation;
import com.example.jpa.entities.Utilisateur;
import com.example.jpa.service.ServiceGestion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class ReservationTest {

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
    public void reservationConfirmeeEtRetrouvee() {
        Salle salle = new Salle("Salle Reservation", 12);
        service.ajouterSalle(salle);
        Utilisateur utilisateur = new Utilisateur("Dina", "dina@example.com", salle);
        service.ajouterUtilisateur(utilisateur);

        LocalDateTime debut = LocalDateTime.of(2026, 10, 20, 9, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 10, 20, 10, 0);
        Reservation reservation = service.reserver(debut, fin, "Reunion equipe", 5, salle, utilisateur);

        assertNotNull(reservation.getNumReservation());
        assertEquals(StatutReservation.CONFIRMEE, reservation.getStatut());

        List<Reservation> reservations = service.reservationsParSalle("Salle Reservation");
        assertEquals(1, reservations.size());
        assertEquals("Reunion equipe", reservations.get(0).getSujet());
    }

    @Test
    public void chevauchementRefuse() {
        Salle salle = new Salle("Salle Chevauchement", 8);
        service.ajouterSalle(salle);
        Utilisateur utilisateur = new Utilisateur("Eli", "eli@example.com", salle);
        service.ajouterUtilisateur(utilisateur);

        LocalDateTime debut = LocalDateTime.of(2026, 10, 21, 14, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 10, 21, 16, 0);
        service.reserver(debut, fin, "Atelier", 4, salle, utilisateur);

        LocalDateTime debutConflit = LocalDateTime.of(2026, 10, 21, 15, 0);
        LocalDateTime finConflit = LocalDateTime.of(2026, 10, 21, 17, 0);

        try {
            service.reserver(debutConflit, finConflit, "Autre reunion", 3, salle, utilisateur);
            fail("Une IllegalStateException etait attendue pour un creneau en conflit");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("deja reservee"));
        }
    }

    @Test
    public void annulationChangeLetat() {
        Salle salle = new Salle("Salle Annulation", 6);
        service.ajouterSalle(salle);
        Utilisateur utilisateur = new Utilisateur("Farid", "farid@example.com", salle);
        service.ajouterUtilisateur(utilisateur);

        LocalDateTime debut = LocalDateTime.of(2026, 10, 22, 11, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 10, 22, 12, 0);
        Reservation reservation = service.reserver(debut, fin, "Entretien", 2, salle, utilisateur);

        service.annulerReservation(reservation);

        Reservation misAJour = service.reservationsParSalle("Salle Annulation").get(0);
        assertEquals(StatutReservation.ANNULEE, misAJour.getStatut());
    }
}
