# TP3_HIBERNATE-

Gestion de reservations de salles avec **JPA / Hibernate 5.4** et une base **H2 en memoire** :
relations entre entites, cascades et suppression des orphelins.

## Modele (`com.example.model`)

| Entite | Role | Relations |
|---|---|---|
| `Utilisateur` | personne qui reserve | `OneToMany` -> `Reservation` (cascade ALL + orphanRemoval) |
| `Reservation` | creneau `[heureDebut, heureFin[` | `ManyToOne` -> `Utilisateur` (titulaire), `ManyToOne` -> `Salle` |
| `Salle` | salle reservable | `ManyToOne` -> `Batiment`, `ManyToMany` -> `Equipement` (table `salle_equipement`) |
| `Equipement` | materiel partage entre salles | cote inverse du `ManyToMany` |
| `Batiment` | regroupe les salles | `OneToMany` -> `Salle` |

Choix de cascade :
- supprimer un utilisateur supprime ses reservations ;
- retirer une reservation de `listeReservations` la supprime en base (`orphanRemoval`) ;
- pas de `REMOVE` vers `Salle`, `Equipement`, `Batiment` : ces donnees sont partagees.

## Exercices supplementaires (`com.example.service.ReservationService`)
- validation des dates (`@PrePersist` / `@PreUpdate` dans `Reservation`) ;
- disponibilite d'une salle sur un creneau ;
- requetes JPQL : reservations d'un utilisateur, salles equipees d'un equipement, salles d'un batiment ;
- gestion des erreurs avec rollback de la transaction.

## Lancer la demo

```
mvn clean compile exec:java
```

ou executer `com.example.App` depuis l'IDE. Les requetes SQL generees s'affichent dans la console
(`hibernate.show_sql` / `hibernate.format_sql`).

Le code du TP2 (DAO generique + service, package `com.example.jpa`) est conserve avec ses tests : `mvn test`.
