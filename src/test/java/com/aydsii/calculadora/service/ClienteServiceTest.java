package com.aydsii.calculadora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aydsii.calculadora.exception.EmailYaRegistradoException;
import com.aydsii.calculadora.model.Cliente;
import com.aydsii.calculadora.model.ClienteDTO;
import com.aydsii.calculadora.repository.ClienteRepository;

class ClienteServiceTest {

    private ClienteRepository clienteRepository;
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteRepository = mock(ClienteRepository.class);
        clienteService = new ClienteService(clienteRepository);
    }

    @Test
    void crearConsultaElEmailYLuegoPersisteElCliente() {
        given(clienteRepository.existsByEmailIgnoreCase("juan@mail.com")).willReturn(false);
        given(clienteRepository.save(any(Cliente.class))).willAnswer(invocacion -> {
            Cliente cliente = invocacion.getArgument(0);
            cliente.setId(1L);
            return cliente;
        });

        Cliente creado = clienteService.crear(new ClienteDTO("Juan", "Pérez", "juan@mail.com", "1122334455"));

        assertThat(creado.getId()).isEqualTo(1L);
        assertThat(creado.getNombre()).isEqualTo("Juan");
        assertThat(creado.getEmail()).isEqualTo("juan@mail.com");
    }

    @Test
    void crearConEmailYaRegistradoLanzaExcepcionYNoPersiste() {
        given(clienteRepository.existsByEmailIgnoreCase("juan@mail.com")).willReturn(true);

        assertThatThrownBy(() -> clienteService.crear(new ClienteDTO("Juan", "Pérez", "juan@mail.com", null)))
                .isInstanceOf(EmailYaRegistradoException.class)
                .hasMessage("El email ya está registrado");

        verify(clienteRepository, never()).save(any());
    }
}
