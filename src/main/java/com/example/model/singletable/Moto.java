package com.example.model.singletable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/** Ligne de la table Vehicule avec vehicule_type = 'MOTO'. */
@Entity
@DiscriminatorValue("MOTO")
public class Moto extends Vehicule {

    private boolean avecSidecar;

    protected Moto() {}

    public Moto(String constructeur, int anneeMiseEnService, boolean avecSidecar) {
        super(constructeur, anneeMiseEnService);
        this.avecSidecar = avecSidecar;
    }

    public boolean isAvecSidecar() { return avecSidecar; }
    public void setAvecSidecar(boolean avecSidecar) { this.avecSidecar = avecSidecar; }

    @Override
    public String toString() {
        return "Moto " + getConstructeur() + " (" + getAnneeMiseEnService() + (avecSidecar ? ", avec side-car)" : ", sans side-car)");
    }
}
