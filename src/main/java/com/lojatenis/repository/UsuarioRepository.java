package com.lojatenis.repository;

import com.lojatenis.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.deleted = false")
    Page<Usuario> findAllActive(Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.deleted = false AND u.id = :id")
    Optional<Usuario> findActiveById(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u WHERE u.deleted = false AND u.email = :email")
    Optional<Usuario> findActiveByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u " +
            "WHERE u.deleted = false AND u.email = :email")
    boolean existsActiveByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.deleted = true WHERE u.id = :id")
    void softDelete(@Param("id") Long id);
}
