package com.example.model.singletable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Nouvelle sous-classe : ajouter un type de vehicule ne cree aucune table,
 * seulement une colonne chargeMaxTonnes dans la table Vehicule.
 */
@Entity
@DiscriminatorValue("CAMION")
public class Camion extends Vehicule {

    private double chargeMaxTonnes;

    protected Camion() {}

    public Camion(String constructeur, int anneeMiseEnService, double chargeMaxTonnes) {
        super(constructeur, anneeMiseEnService);
        this.chargeMaxTonnes = chargeMaxTonnes;
    }

    public double getChargeMaxTonnes() { return chargeMaxTonnes; }
    public void setChargeMaxTonnes(double chargeMaxTonnes) { this.chargeMaxTonnes = chargeMaxTonnes; }

    @Override
    public String toString() {
        return "Camion " + getConstructeur() + " (" + getAnneeMiseEnService() + ", " + chargeMaxTonnes + " t)";
    }
}
