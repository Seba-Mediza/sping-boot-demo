package com.aydsii.calculadora;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aydsii.calculadora.model.Categoria;
import com.aydsii.calculadora.model.Cliente;
import com.aydsii.calculadora.model.EstadoPedido;
import com.aydsii.calculadora.model.Pedido;
import com.aydsii.calculadora.model.Producto;
import com.aydsii.calculadora.repository.CategoriaRepository;
import com.aydsii.calculadora.repository.ClienteRepository;
import com.aydsii.calculadora.repository.PedidoRepository;
import com.aydsii.calculadora.repository.ProductoRepository;

/**
 * Carga datos de ejemplo (categorías, productos, clientes y pedidos) la primera vez
 * que se levanta el proyecto, para poder probar /api/pedidos/buscar sin cargar nada
 * a mano. Si ya hay pedidos, no hace nada (evita duplicar datos en cada reinicio).
 */
@Component
public class PedidosDataSeeder implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public PedidosDataSeeder(CategoriaRepository categoriaRepository, ProductoRepository productoRepository,
            ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void run(String... args) {
        if (pedidoRepository.count() > 0) {
            return;
        }

        Categoria perifericos = categoriaRepository.save(new Categoria(null, "Perifericos"));
        Categoria computacion = categoriaRepository.save(new Categoria(null, "Computacion"));
        Categoria audio = categoriaRepository.save(new Categoria(null, "Audio"));

        Producto mouse = productoRepository.save(new Producto(null, "Mouse inalambrico", 4500.0, perifericos));
        Producto teclado = productoRepository.save(new Producto(null, "Teclado mecanico", 12000.0, perifericos));
        Producto monitor = productoRepository.save(new Producto(null, "Monitor 24 pulgadas", 85000.0, computacion));
        Producto notebook = productoRepository.save(new Producto(null, "Notebook Core i5", 450000.0, computacion));
        Producto auriculares = productoRepository.save(new Producto(null, "Auriculares Bluetooth", 18000.0, audio));

        Cliente ana = clienteRepository.save(new Cliente("Ana", "Garcia", "ana.garcia@demo.com", "1122334455"));
        Cliente carlos = clienteRepository.save(new Cliente("Carlos", "Lopez", "carlos.lopez@demo.com", "1133445566"));
        Cliente beatriz = clienteRepository.save(new Cliente("Beatriz", "Fernandez", "beatriz.fernandez@demo.com", null));

        Pedido pedido1 = new Pedido(ana, LocalDate.of(2026, 8, 15), EstadoPedido.ENTREGADO);
        pedido1.agregarDetalle(mouse, 2, mouse.getPrecio());
        pedidoRepository.save(pedido1);

        Pedido pedido2 = new Pedido(ana, LocalDate.of(2026, 9, 1), EstadoPedido.PENDIENTE);
        pedido2.agregarDetalle(teclado, 1, teclado.getPrecio());
        pedido2.agregarDetalle(auriculares, 1, auriculares.getPrecio());
        pedidoRepository.save(pedido2);

        Pedido pedido3 = new Pedido(carlos, LocalDate.of(2026, 7, 20), EstadoPedido.ENVIADO);
        pedido3.agregarDetalle(notebook, 1, notebook.getPrecio());
        pedidoRepository.save(pedido3);

        Pedido pedido4 = new Pedido(carlos, LocalDate.of(2026, 9, 10), EstadoPedido.CANCELADO);
        pedido4.agregarDetalle(monitor, 1, monitor.getPrecio());
        pedidoRepository.save(pedido4);

        Pedido pedido5 = new Pedido(beatriz, LocalDate.of(2026, 8, 25), EstadoPedido.ENTREGADO);
        pedido5.agregarDetalle(mouse, 1, mouse.getPrecio());
        pedido5.agregarDetalle(monitor, 1, monitor.getPrecio());
        pedidoRepository.save(pedido5);
    }
}
