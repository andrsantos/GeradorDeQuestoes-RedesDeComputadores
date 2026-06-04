package com.Projeto.GeradorDeQuestoes.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Projeto.GeradorDeQuestoes.entities.TopicoEntity;

public interface TopicoRepository extends JpaRepository<TopicoEntity, UUID> {
    Optional<TopicoEntity> findByNome(String nome);
}
