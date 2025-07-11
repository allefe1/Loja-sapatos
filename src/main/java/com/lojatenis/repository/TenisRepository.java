package com.lojatenis.repository;

import com.lojatenis.domain.Tenis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface TenisRepository extends JpaRepository<Tenis, Long> {

    @Query("SELECT t FROM Tenis t WHERE t.deleted = false")
    Page<Tenis> findAllActive(Pageable pageable);

    @Query("SELECT t FROM Tenis t WHERE t.deleted = false AND t.id = :id")
    Optional<Tenis> findActiveById(@Param("id") Long id);

    @Query("SELECT t FROM Tenis t WHERE t.deleted = false AND " +
            "LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Tenis> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    @Query("SELECT t FROM Tenis t WHERE t.deleted = false AND " +
            "LOWER(t.marca) LIKE LOWER(CONCAT('%', :marca, '%'))")
    Page<Tenis> findByMarcaContainingIgnoreCase(@Param("marca") String marca, Pageable pageable);

    @Query("SELECT t FROM Tenis t WHERE t.deleted = false AND t.preco BETWEEN :precoMin AND :precoMax")
    Page<Tenis> findByPrecoBetween(@Param("precoMin") BigDecimal precoMin,
                                   @Param("precoMax") BigDecimal precoMax,
                                   Pageable pageable);

    @Modifying
    @Query("UPDATE Tenis t SET t.deleted = true WHERE t.id = :id")
    void softDelete(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Tenis t " +
            "WHERE t.deleted = false AND t.id = :id")
    boolean existsActiveById(@Param("id") Long id);
}
