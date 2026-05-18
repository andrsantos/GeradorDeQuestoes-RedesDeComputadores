package com.Projeto.GeradorDeQuestoes.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Projeto.GeradorDeQuestoes.entities.PdfQuestaoEntity;

@Repository
public interface PdfQuestaoRepository extends JpaRepository<PdfQuestaoEntity, UUID> {
    
}
