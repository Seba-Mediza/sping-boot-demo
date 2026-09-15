package com.aydsii.calculadora.service;

import org.springframework.stereotype.Service;

import com.aydsii.calculadora.exception.EmailYaRegistradoException;
import com.aydsii.calculadora.model.Cliente;
import com.aydsii.calculadora.model.ClienteDTO;
import com.aydsii.calculadora.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Consulta la base de datos para comprobar que el email no esté ya registrado
     * (esto no es una regla de Bean Validation, se hace antes del INSERT) y recién
     * ahí persiste el cliente.
     */
    public Cliente crear(ClienteDTO datos) {
        if (clienteRepository.existsByEmailIgnoreCase(datos.getEmail())) {
            throw new EmailYaRegistradoException();
        }
        Cliente cliente = new Cliente(datos.getNombre(), datos.getApellido(), datos.getEmail(), datos.getTelefono());
        return clienteRepository.save(cliente);
    }
}
