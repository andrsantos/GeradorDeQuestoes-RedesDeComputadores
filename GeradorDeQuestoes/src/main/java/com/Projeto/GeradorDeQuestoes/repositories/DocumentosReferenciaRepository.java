package com.Projeto.GeradorDeQuestoes.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Projeto.GeradorDeQuestoes.entities.DocumentosReferenciaEntity;

@Repository
public interface DocumentosReferenciaRepository extends JpaRepository<DocumentosReferenciaEntity, UUID>{
    
}
