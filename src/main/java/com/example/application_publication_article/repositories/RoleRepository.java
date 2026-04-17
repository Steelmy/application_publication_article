package com.example.application_publication_article.repositories;

import com.example.application_publication_article.entities.Role;
import com.example.application_publication_article.entities.TypeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Utile pour trouver un rôle par son nom d'énumération
    Optional<Role> findByNomRole(TypeRole nomRole);
}