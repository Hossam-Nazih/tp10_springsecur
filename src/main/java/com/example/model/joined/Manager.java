package com.example.model.joined;

import javax.persistence.Entity;

/** Table Manager : uniquement l'id (cle etrangere) et nbEquipesGerees. */
@Entity
public class Manager extends Employe {

    private int nbEquipesGerees;

    protected Manager() {}

    public Manager(String nomComplet, double salaireMensuel, int nbEquipesGerees) {
        super(nomComplet, salaireMensuel);
        this.nbEquipesGerees = nbEquipesGerees;
    }

    public int getNbEquipesGerees() { return nbEquipesGerees; }
    public void setNbEquipesGerees(int nbEquipesGerees) { this.nbEquipesGerees = nbEquipesGerees; }

    @Override
    public String toString() {
        return "Manager " + getNomComplet() + " (" + nbEquipesGerees + " equipes)";
    }
}
