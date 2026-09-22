package com.aydsii.calculadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aydsii.calculadora.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
