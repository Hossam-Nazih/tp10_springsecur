package com.example.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Membre (etudiant, enseignant...) autorise a reserver des salles.
 * <p>
 * Il est "proprietaire" de ses reservations : elles naissent et meurent avec lui
 * (cascade ALL + orphanRemoval).
 */
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long idUtilisateur;

    // sert d'identifiant fonctionnel : la contrainte UNIQUE empeche deux comptes avec le meme mail
    @Column(name = "adresse_mail", nullable = false, unique = true)
    private String adresseMail;

    // prenom + nom tels qu'affiches dans le planning
    @Column(name = "nom_complet", nullable = false)
    private String nomComplet;

    // cote inverse de Reservation.titulaire (la cle etrangere est dans la table reservation).
    // cascade ALL        : persist / merge / remove de l'utilisateur sont repercutes sur ses reservations
    // orphanRemoval=true : une reservation retiree de cette collection est supprimee en base (DELETE)
    @OneToMany(mappedBy = "titulaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> listeReservations = new HashSet<>();

    public Utilisateur() {}

    public Utilisateur(String nomComplet, String adresseMail) {
        this.nomComplet = nomComplet;
        this.adresseMail = adresseMail;
    }

    /**
     * Ajoute une reservation en synchronisant les deux cotes : sans le setTitulaire(),
     * la colonne titulaire_id resterait vide et l'INSERT echouerait (nullable = false).
     */
    public void ajouterReservation(Reservation reservation) {
        listeReservations.add(reservation);
        reservation.setTitulaire(this);
    }

    // l'inverse : grace a orphanRemoval, la reservation retiree sera supprimee au prochain commit
    public void retirerReservation(Reservation reservation) {
        listeReservations.remove(reservation);
        reservation.setTitulaire(null);
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public String getAdresseMail() {
        return adresseMail;
    }

    public void setAdresseMail(String adresseMail) {
        this.adresseMail = adresseMail;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public Set<Reservation> getListeReservations() {
        return listeReservations;
    }

    @Override
    public String toString() {
        return "Utilisateur [id=" + idUtilisateur + ", nom=" + nomComplet + ", mail=" + adresseMail
                + ", reservations=" + listeReservations.size() + "]";
    }
}
