package com.aydsii.calculadora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.aydsii.calculadora.model.Categoria;
import com.aydsii.calculadora.model.Cliente;
import com.aydsii.calculadora.model.EstadoPedido;
import com.aydsii.calculadora.model.Pedido;
import com.aydsii.calculadora.model.PedidoResumen;
import com.aydsii.calculadora.model.Producto;
import com.aydsii.calculadora.repository.PedidoRepository;

class PedidoServiceTest {

    private final PedidoRepository pedidoRepository = mock(PedidoRepository.class);
    private final PedidoService pedidoService = new PedidoService(pedidoRepository);

    @Test
    void buscarMapeaClienteProductosYCalculaElTotal() {
        Cliente cliente = new Cliente("Ana", "Garcia", "ana@mail.com", null);
        Categoria categoria = new Categoria(1L, "Perifericos");
        Producto producto = new Producto(1L, "Mouse inalambrico", 4500.0, categoria);

        Pedido pedido = new Pedido(cliente, LocalDate.of(2026, 8, 15), EstadoPedido.ENTREGADO);
        pedido.setId(12L);
        pedido.agregarDetalle(producto, 2, 4500.0);

        given(pedidoRepository.buscar(5L, "Perifericos", null, null, EstadoPedido.ENTREGADO))
                .willReturn(List.of(pedido));

        List<PedidoResumen> resultado = pedidoService.buscar(5L, "Perifericos", null, null, EstadoPedido.ENTREGADO);

        assertThat(resultado).hasSize(1);
        PedidoResumen resumen = resultado.get(0);
        assertThat(resumen.pedidoId()).isEqualTo(12L);
        assertThat(resumen.cliente()).isEqualTo("Ana Garcia");
        assertThat(resumen.estado()).isEqualTo(EstadoPedido.ENTREGADO);
        assertThat(resumen.totalPedido()).isEqualTo(9000.0);
        assertThat(resumen.productos()).hasSize(1);
        assertThat(resumen.productos().get(0).nombre()).isEqualTo("Mouse inalambrico");
        assertThat(resumen.productos().get(0).categoria()).isEqualTo("Perifericos");
        assertThat(resumen.productos().get(0).subtotal()).isEqualTo(9000.0);
    }

    @Test
    void buscarSumaElSubtotalDeVariosProductosParaElTotalDelPedido() {
        Cliente cliente = new Cliente("Carlos", "Lopez", "carlos@mail.com", null);
        Categoria perifericos = new Categoria(1L, "Perifericos");
        Categoria audio = new Categoria(2L, "Audio");
        Producto teclado = new Producto(1L, "Teclado mecanico", 12000.0, perifericos);
        Producto auriculares = new Producto(2L, "Auriculares Bluetooth", 18000.0, audio);

        Pedido pedido = new Pedido(cliente, LocalDate.of(2026, 9, 1), EstadoPedido.PENDIENTE);
        pedido.setId(2L);
        pedido.agregarDetalle(teclado, 1, 12000.0);
        pedido.agregarDetalle(auriculares, 1, 18000.0);

        given(pedidoRepository.buscar(any(), any(), any(), any(), any())).willReturn(List.of(pedido));

        PedidoResumen resumen = pedidoService.buscar(null, null, null, null, null).get(0);

        assertThat(resumen.totalPedido()).isEqualTo(30000.0);
        assertThat(resumen.productos()).hasSize(2);
    }

    @Test
    void buscarSinResultadosDevuelveListaVacia() {
        given(pedidoRepository.buscar(any(), any(), any(), any(), any())).willReturn(List.of());

        List<PedidoResumen> resultado = pedidoService.buscar(null, null, null, null, null);

        assertThat(resultado).isEmpty();
    }
}
