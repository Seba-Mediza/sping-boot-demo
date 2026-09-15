package com.aydsii.calculadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aydsii.calculadora.model.Cliente;

// JpaRepository: Spring Data genera la implementación (CRUD + la query derivada de abajo) en tiempo de ejecución.
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmailIgnoreCase(String email);
}
