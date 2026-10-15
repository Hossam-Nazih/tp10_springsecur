package com.example.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Salle reservable, situee dans un batiment et dotee d'equipements.
 * <p>
 * Relations :
 * <ul>
 *   <li>ManyToOne vers Batiment (cote proprietaire, colonne batiment_id)</li>
 *   <li>ManyToMany vers Equipement (cote proprietaire, table salle_equipement)</li>
 *   <li>OneToMany vers Reservation (cote inverse, simple navigation)</li>
 * </ul>
 */
@Entity
@Table(name = "salle")
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salle")
    private Long idSalle;

    // nom affiche sur la porte (ex. "Salle A") ; unique pour eviter toute ambiguite lors d'une reservation
    @Column(name = "libelle", nullable = false, unique = true)
    private String libelle;

    // nombre de places assises disponibles
    @Column(name = "nb_places", nullable = false)
    private int nbPlaces;

    // etage ou se trouve la salle (0 = rez-de-chaussee)
    @Column(name = "etage", nullable = false)
    private int etage;

    // plusieurs salles par batiment ; nullable : une salle peut etre creee avant d'etre affectee.
    // PERSIST/MERGE : enregistrer une salle enregistre aussi un batiment encore nouveau
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "batiment_id")
    private Batiment batiment;

    // Salle possede la relation : c'est elle qui alimente la table de jointure salle_equipement.
    // Volontairement SANS cascade REMOVE : un equipement peut etre partage par d'autres salles,
    // le supprimer avec la salle casserait leurs donnees.
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "salle_equipement",
            joinColumns = @JoinColumn(name = "salle_id"),          // FK vers cette entite
            inverseJoinColumns = @JoinColumn(name = "equipement_id") // FK vers Equipement
    )
    private Set<Equipement> equipementsInstalles = new HashSet<>();

    // cote inverse de Reservation.salleConcernee, sans cascade : les reservations
    // appartiennent a l'utilisateur, la salle ne fait que les consulter
    @OneToMany(mappedBy = "salleConcernee")
    private Set<Reservation> creneauxReserves = new HashSet<>();

    public Salle() {}

    public Salle(String libelle, int nbPlaces, int etage) {
        this.libelle = libelle;
        this.nbPlaces = nbPlaces;
        this.etage = etage;
    }

    /**
     * Installe un equipement dans la salle. On met a jour les deux collections
     * pour que le modele objet reste coherent sans recharger depuis la base.
     */
    public void ajouterEquipement(Equipement equipement) {
        equipementsInstalles.add(equipement);
        equipement.getSallesEquipees().add(this);
    }

    // retire uniquement le lien (ligne de salle_equipement), l'equipement lui-meme est conserve
    public void retirerEquipement(Equipement equipement) {
        equipementsInstalles.remove(equipement);
        equipement.getSallesEquipees().remove(this);
    }

    public Long getIdSalle() {
        return idSalle;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public int getEtage() {
        return etage;
    }

    public void setEtage(int etage) {
        this.etage = etage;
    }

    public Batiment getBatiment() {
        return batiment;
    }

    // utilise par Batiment.ajouterSalle() ; preferer cette methode pour garder les deux cotes synchronises
    public void setBatiment(Batiment batiment) {
        this.batiment = batiment;
    }

    public Set<Equipement> getEquipementsInstalles() {
        return equipementsInstalles;
    }

    public Set<Reservation> getCreneauxReserves() {
        return creneauxReserves;
    }

    @Override
    public String toString() {
        return "Salle [id=" + idSalle + ", libelle=" + libelle + ", places=" + nbPlaces + ", etage=" + etage
                + ", batiment=" + (batiment != null ? batiment.getCodeBatiment() : "aucun")
                + ", equipements=" + equipementsInstalles.size() + "]";
    }
}
