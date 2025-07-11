package com.lojatenis.repository;

import com.lojatenis.domain.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    @Query("SELECT i FROM ItemPedido i WHERE i.deleted = false AND i.pedido.id = :pedidoId")
    List<ItemPedido> findActiveByPedidoId(@Param("pedidoId") Long pedidoId);

    @Query("SELECT i FROM ItemPedido i WHERE i.deleted = false AND i.tenis.id = :tenisId")
    List<ItemPedido> findActiveByTenisId(@Param("tenisId") Long tenisId);
}
