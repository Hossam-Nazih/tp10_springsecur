package com.example.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Local physique (salle de cours, de reunion...) que l'on peut affecter
 * a des utilisateurs et reserver sur des creneaux.
 */
@Entity
@Table(name = "tp2_salle")
public class Salle {

    // numero technique genere par la colonne auto-increment de H2 (strategie IDENTITY) :
    // aucune sequence a gerer, la base attribue la valeur au moment de l'INSERT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_salle")
    private Long numSalle;

    // nom visible sur la porte ; la contrainte UNIQUE garantit qu'on ne confond jamais deux salles
    @Column(name = "nom_salle", nullable = false, unique = true)
    private String nomSalle;

    // nombre maximal de personnes que la salle peut accueillir
    @Column(name = "nombre_places", nullable = false)
    private int nombrePlaces;

    // constructeur vide impose par la specification JPA (instanciation par reflexion)
    public Salle() {}

    public Salle(String nomSalle, int nombrePlaces) {
        this.nomSalle = nomSalle;
        this.nombrePlaces = nombrePlaces;
    }

    // pas de setter sur l'identifiant : c'est la base qui le fixe
    public Long getNumSalle() {
        return numSalle;
    }

    public String getNomSalle() {
        return nomSalle;
    }

    public void setNomSalle(String nomSalle) {
        this.nomSalle = nomSalle;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    @Override
    public String toString() {
        return "Salle [num=" + numSalle + ", nom=" + nomSalle + ", places=" + nombrePlaces + "]";
    }
}
