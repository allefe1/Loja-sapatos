package com.lojatenis.repository;

import com.lojatenis.domain.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("SELECT c FROM Categoria c WHERE c.deleted = false")
    Page<Categoria> findAllActive(Pageable pageable);

    @Query("SELECT c FROM Categoria c WHERE c.deleted = false AND c.id = :id")
    Optional<Categoria> findActiveById(@Param("id") Long id);

    @Query("SELECT c FROM Categoria c WHERE c.deleted = false AND c.id IN :ids")
    Set<Categoria> findActiveByIds(@Param("ids") Set<Long> ids);

    @Query("SELECT c FROM Categoria c WHERE c.deleted = false AND " +
            "LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Categoria> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    @Modifying
    @Query("UPDATE Categoria c SET c.deleted = true WHERE c.id = :id")
    void softDelete(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Categoria c " +
            "WHERE c.deleted = false AND c.id = :id")
    boolean existsActiveById(@Param("id") Long id);
}
