package com.aydsii.calculadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aydsii.calculadora.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
