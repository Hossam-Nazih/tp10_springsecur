package com.example.model.tableperclass;

import javax.persistence.Entity;

/** Table Electronique : id, libelle, prix (herites) + dureeGarantieMois. */
@Entity
public class Electronique extends Produit {

    private int dureeGarantieMois;

    protected Electronique() {}

    public Electronique(String libelle, double prix, int dureeGarantieMois) {
        super(libelle, prix);
        this.dureeGarantieMois = dureeGarantieMois;
    }

    public int getDureeGarantieMois() { return dureeGarantieMois; }
    public void setDureeGarantieMois(int dureeGarantieMois) { this.dureeGarantieMois = dureeGarantieMois; }

    @Override
    public String toString() {
        return "Electronique " + getLibelle() + " (" + getPrix() + " DH, garantie " + dureeGarantieMois + " mois)";
    }
}
