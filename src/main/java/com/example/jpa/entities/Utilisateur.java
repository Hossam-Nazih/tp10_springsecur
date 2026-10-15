package com.example.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * Personne enregistree dans l'application : elle est rattachee a une salle
 * "de base" et peut deposer des reservations.
 */
@Entity
@Table(name = "tp2_utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_utilisateur")
    private Long numUtilisateur;

    // nom et prenom tels qu'ils apparaissent dans les listes
    @Column(name = "nom_prenom", nullable = false)
    private String nomPrenom;

    // adresse de contact ; UNIQUE car elle sert a retrouver un utilisateur sans connaitre son numero
    @Column(name = "courriel", nullable = false, unique = true)
    private String courriel;

    // relation ManyToOne : une salle peut etre partagee par plusieurs utilisateurs,
    // mais chaque utilisateur n'est affecte qu'a une seule salle (facultative : colonne nullable)
    @ManyToOne
    @JoinColumn(name = "salle_affectee_num")
    private Salle salleAffectee;

    public Utilisateur() {}

    public Utilisateur(String nomPrenom, String courriel, Salle salleAffectee) {
        this.nomPrenom = nomPrenom;
        this.courriel = courriel;
        this.salleAffectee = salleAffectee;
    }

    public Long getNumUtilisateur() {
        return numUtilisateur;
    }

    public String getNomPrenom() {
        return nomPrenom;
    }

    public void setNomPrenom(String nomPrenom) {
        this.nomPrenom = nomPrenom;
    }

    public String getCourriel() {
        return courriel;
    }

    public void setCourriel(String courriel) {
        this.courriel = courriel;
    }

    public Salle getSalleAffectee() {
        return salleAffectee;
    }

    public void setSalleAffectee(Salle salleAffectee) {
        this.salleAffectee = salleAffectee;
    }

    // on affiche seulement le nom de la salle pour garder une sortie courte et lisible
    @Override
    public String toString() {
        return "Utilisateur [num=" + numUtilisateur + ", nom=" + nomPrenom + ", courriel=" + courriel
                + ", salle=" + (salleAffectee != null ? salleAffectee.getNomSalle() : "aucune") + "]";
    }
}
