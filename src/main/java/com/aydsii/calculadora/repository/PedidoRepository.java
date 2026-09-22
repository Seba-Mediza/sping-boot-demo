package com.aydsii.calculadora.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aydsii.calculadora.model.EstadoPedido;
import com.aydsii.calculadora.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Relaciona pedidos, clientes, detalle_pedidos, productos y categorias en una sola consulta.
     * Los JOIN FETCH traen todo el grafo necesario para armar la respuesta sin N+1 queries.
     * El filtro por categoria usa un EXISTS aparte para no perder el resto de los productos del
     * pedido: solo decide si el pedido entra o no, no recorta qué detalles se traen.
     * Cada filtro es opcional: si el parámetro llega null, esa condición no se aplica (AND).
     */
    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.cliente c
            LEFT JOIN FETCH p.detalles d
            LEFT JOIN FETCH d.producto pr
            LEFT JOIN FETCH pr.categoria cat
            WHERE (:clienteId IS NULL OR c.id = :clienteId)
              AND (:fechaDesde IS NULL OR p.fecha >= :fechaDesde)
              AND (:fechaHasta IS NULL OR p.fecha <= :fechaHasta)
              AND (:estado IS NULL OR p.estado = :estado)
              AND (:categoria IS NULL OR EXISTS (
                    SELECT 1 FROM DetallePedido dc
                    JOIN dc.producto prc
                    JOIN prc.categoria catc
                    WHERE dc.pedido = p AND LOWER(catc.nombre) = LOWER(:categoria)
              ))
            ORDER BY p.fecha DESC, p.id DESC
            """)
    List<Pedido> buscar(@Param("clienteId") Long clienteId,
            @Param("categoria") String categoria,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta,
            @Param("estado") EstadoPedido estado);
}
