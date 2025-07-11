package com.lojatenis.repository;

import com.lojatenis.domain.Pedido;
import com.lojatenis.domain.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p WHERE p.deleted = false")
    Page<Pedido> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.deleted = false AND p.id = :id")
    Optional<Pedido> findActiveById(@Param("id") Long id);

    @Query("SELECT p FROM Pedido p WHERE p.deleted = false AND p.usuario.id = :usuarioId")
    Page<Pedido> findActiveByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.deleted = false AND p.status = :status")
    Page<Pedido> findActiveByStatus(@Param("status") StatusPedido status, Pageable pageable);

    @Modifying
    @Query("UPDATE Pedido p SET p.deleted = true WHERE p.id = :id")
    void softDelete(@Param("id") Long id);
}
