package com.aydsii.calculadora.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aydsii.calculadora.model.DetallePedido;
import com.aydsii.calculadora.model.EstadoPedido;
import com.aydsii.calculadora.model.Pedido;
import com.aydsii.calculadora.model.PedidoResumen;
import com.aydsii.calculadora.model.ProductoResumen;
import com.aydsii.calculadora.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<PedidoResumen> buscar(Long clienteId, String categoria, LocalDate fechaDesde, LocalDate fechaHasta,
            EstadoPedido estado) {
        return pedidoRepository.buscar(clienteId, categoria, fechaDesde, fechaHasta, estado).stream()
                .map(this::aResumen)
                .toList();
    }

    private PedidoResumen aResumen(Pedido pedido) {
        List<ProductoResumen> productos = pedido.getDetalles().stream()
                .map(this::aProductoResumen)
                .toList();

        double totalPedido = productos.stream().mapToDouble(ProductoResumen::subtotal).sum();
        String cliente = pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido();

        return new PedidoResumen(pedido.getId(), cliente, pedido.getFecha(), pedido.getEstado(),
                redondear(totalPedido), productos);
    }

    private ProductoResumen aProductoResumen(DetallePedido detalle) {
        double subtotal = detalle.getCantidad() * detalle.getPrecioUnitario();
        return new ProductoResumen(detalle.getProducto().getNombre(),
                detalle.getProducto().getCategoria().getNombre(), detalle.getCantidad(), redondear(subtotal));
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
