package com.example.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Batiment du campus qui regroupe plusieurs salles.
 * <p>
 * Relation : un batiment contient 0..n salles, une salle appartient a un seul batiment
 * (OneToMany ici, ManyToOne cote Salle).
 */
@Entity
@Table(name = "batiment")
public class Batiment {

    // cle primaire auto-incrementee par H2 (strategie IDENTITY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_batiment")
    private Long idBatiment;

    // code court affiche sur la signaletique (ex. "Bloc A") : deux batiments ne peuvent pas avoir le meme
    @Column(name = "code_batiment", nullable = false, unique = true, length = 30)
    private String codeBatiment;

    // adresse postale, facultative
    @Column(name = "adresse")
    private String adresse;

    // nombre de niveaux, rez-de-chaussee compris : les etages valides vont de 0 a nombreEtages - 1
    @Column(name = "nombre_etages", nullable = false)
    private int nombreEtages;

    // cote inverse de Salle.batiment : la cle etrangere "batiment_id" est dans la table salle.
    // Pas de cascade REMOVE : on refuse de faire disparaitre des salles (et leur historique
    // de reservations) simplement parce qu'un batiment est supprime.
    @OneToMany(mappedBy = "batiment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Salle> sallesDuBatiment = new HashSet<>();

    // constructeur sans argument exige par JPA (Hibernate instancie l'entite par reflexion)
    public Batiment() {}

    public Batiment(String codeBatiment, String adresse, int nombreEtages) {
        this.codeBatiment = codeBatiment;
        this.adresse = adresse;
        this.nombreEtages = nombreEtages;
    }

    /**
     * Rattache une salle a ce batiment en mettant a jour les deux cotes de l'association :
     * la collection en memoire ET la cle etrangere portee par la salle.
     * Refuse une salle placee a un etage que le batiment ne possede pas.
     */
    public void ajouterSalle(Salle salle) {
        if (salle.getEtage() < 0 || salle.getEtage() >= nombreEtages) {
            throw new IllegalArgumentException("Le batiment " + codeBatiment + " n'a pas d'etage " + salle.getEtage()
                    + " (etages 0 a " + (nombreEtages - 1) + ")");
        }
        sallesDuBatiment.add(salle);
        salle.setBatiment(this);
    }

    public void retirerSalle(Salle salle) {
        sallesDuBatiment.remove(salle);
        salle.setBatiment(null);
    }

    public Long getIdBatiment() {
        return idBatiment;
    }

    public String getCodeBatiment() {
        return codeBatiment;
    }

    public void setCodeBatiment(String codeBatiment) {
        this.codeBatiment = codeBatiment;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getNombreEtages() {
        return nombreEtages;
    }

    public void setNombreEtages(int nombreEtages) {
        this.nombreEtages = nombreEtages;
    }

    public Set<Salle> getSallesDuBatiment() {
        return sallesDuBatiment;
    }

    // on affiche seulement le nombre de salles pour eviter une boucle infinie Batiment <-> Salle
    @Override
    public String toString() {
        return "Batiment [id=" + idBatiment + ", code=" + codeBatiment + ", adresse=" + adresse
                + ", etages=" + nombreEtages + ", salles=" + sallesDuBatiment.size() + "]";
    }
}
