package com.Projeto.GeradorDeQuestoes.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Projeto.GeradorDeQuestoes.entities.DocumentoPromptEntity;

public interface DocumentoPromptRepository extends JpaRepository<DocumentoPromptEntity, UUID> {
    
}
