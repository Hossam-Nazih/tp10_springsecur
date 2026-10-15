package com.example.model.singletable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/** Ligne de la table Vehicule avec vehicule_type = 'VOITURE'. */
@Entity
@DiscriminatorValue("VOITURE")
public class Voiture extends Vehicule {

    private int nbPortes;

    protected Voiture() {}

    public Voiture(String constructeur, int anneeMiseEnService, int nbPortes) {
        super(constructeur, anneeMiseEnService);
        this.nbPortes = nbPortes;
    }

    public int getNbPortes() { return nbPortes; }
    public void setNbPortes(int nbPortes) { this.nbPortes = nbPortes; }

    @Override
    public String toString() {
        return "Voiture " + getConstructeur() + " (" + getAnneeMiseEnService() + ", " + nbPortes + " portes)";
    }
}
