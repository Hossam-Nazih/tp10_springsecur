package com.example.jpa.entities;

/**
 * Etats possibles d'une reservation :
 * EN_ATTENTE a la creation, CONFIRMEE une fois le creneau verifie libre,
 * ANNULEE si le demandeur ou le gestionnaire y renonce.
 */
public enum StatutReservation {
    EN_ATTENTE,
    CONFIRMEE,
    ANNULEE
}
