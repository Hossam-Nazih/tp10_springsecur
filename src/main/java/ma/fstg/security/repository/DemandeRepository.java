package ma.fstg.security.repository;

import ma.fstg.security.entity.Demande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeRepository extends JpaRepository<Demande, Integer> {

    // Vue de l'utilisateur : uniquement ses propres demandes
    List<Demande> findByDemandeurOrderByDateCreationDesc(String demandeur);

    // Vue de l'administrateur : toutes les demandes
    List<Demande> findAllByOrderByDateCreationDesc();
}
