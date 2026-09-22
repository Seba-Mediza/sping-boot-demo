package com.aydsii.calculadora.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import com.aydsii.calculadora.model.Categoria;
import com.aydsii.calculadora.model.Cliente;
import com.aydsii.calculadora.model.EstadoPedido;
import com.aydsii.calculadora.model.Pedido;
import com.aydsii.calculadora.model.Producto;

/**
 * @DataJpaTest levanta solo la capa de persistencia (no el contexto completo) y cada
 * test corre dentro de una transacción que se revierte al terminar. Replace.NONE evita
 * que Spring sustituya el datasource por uno embebido y usa el MySQL real configurado
 * en application.properties, que es lo que de verdad valida la consulta @Query.
 * Todos los nombres usan un sufijo random para no chocar con datos ya cargados
 * (por ejemplo, los del PedidosDataSeeder) en esa misma base compartida.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    private Cliente cliente;
    private Categoria categoriaA;
    private Categoria categoriaB;
    private Producto productoA;
    private Producto productoB;

    @BeforeEach
    void setUp() {
        String sufijo = UUID.randomUUID().toString();
        cliente = clienteRepository.save(new Cliente("Test", "Cliente", "test." + sufijo + "@mail.com", null));
        categoriaA = categoriaRepository.save(new Categoria(null, "CategoriaA-" + sufijo));
        categoriaB = categoriaRepository.save(new Categoria(null, "CategoriaB-" + sufijo));
        productoA = productoRepository.save(new Producto(null, "ProductoA", 100.0, categoriaA));
        productoB = productoRepository.save(new Producto(null, "ProductoB", 200.0, categoriaB));

        Pedido entregado = new Pedido(cliente, LocalDate.of(2026, 1, 10), EstadoPedido.ENTREGADO);
        entregado.agregarDetalle(productoA, 2, 100.0);
        pedidoRepository.save(entregado);

        Pedido pendiente = new Pedido(cliente, LocalDate.of(2026, 2, 20), EstadoPedido.PENDIENTE);
        pendiente.agregarDetalle(productoB, 1, 200.0);
        pedidoRepository.save(pendiente);
    }

    @Test
    void buscarSinFiltrosDevuelveTodosLosPedidosDelCliente() {
        List<Pedido> resultado = pedidoRepository.buscar(cliente.getId(), null, null, null, null);

        assertThat(resultado).hasSize(2);
    }

    @Test
    void buscarPorEstadoFiltra() {
        List<Pedido> resultado = pedidoRepository.buscar(cliente.getId(), null, null, null, EstadoPedido.ENTREGADO);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoPedido.ENTREGADO);
    }

    @Test
    void buscarPorCategoriaDevuelveSoloPedidosQueIncluyenEsaCategoria() {
        List<Pedido> resultado = pedidoRepository.buscar(cliente.getId(), categoriaB.getNombre(), null, null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
    }

    @Test
    void buscarCombinandoClienteCategoriaYEstadoAplicaTodosLosFiltrosConAnd() {
        List<Pedido> coincide = pedidoRepository.buscar(cliente.getId(), categoriaA.getNombre(), null, null,
                EstadoPedido.ENTREGADO);
        assertThat(coincide).hasSize(1);

        List<Pedido> noCoincide = pedidoRepository.buscar(cliente.getId(), categoriaA.getNombre(), null, null,
                EstadoPedido.PENDIENTE);
        assertThat(noCoincide).isEmpty();
    }

    @Test
    void buscarPorRangoDeFechasFiltra() {
        List<Pedido> resultado = pedidoRepository.buscar(cliente.getId(), null,
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28), null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
    }

    @Test
    void buscarTraeElClienteYLosProductosDeCadaDetalleSinConsultasExtra() {
        List<Pedido> resultado = pedidoRepository.buscar(cliente.getId(), null, null, null, EstadoPedido.ENTREGADO);

        Pedido pedido = resultado.get(0);
        assertThat(pedido.getCliente().getNombre()).isEqualTo("Test");
        assertThat(pedido.getDetalles()).hasSize(1);
        assertThat(pedido.getDetalles().get(0).getProducto().getNombre()).isEqualTo("ProductoA");
        assertThat(pedido.getDetalles().get(0).getProducto().getCategoria().getNombre())
                .isEqualTo(categoriaA.getNombre());
    }

    @Test
    void buscarConCategoriaInexistenteDevuelveListaVacia() {
        List<Pedido> resultado = pedidoRepository.buscar(cliente.getId(), "categoria-que-no-existe", null, null, null);

        assertThat(resultado).isEmpty();
    }
}
