package com.Projeto.GeradorDeQuestoes.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Projeto.GeradorDeQuestoes.entities.PdfBinarioEntity;

public interface PdfBinarioRepository extends JpaRepository<PdfBinarioEntity, UUID> {
    Optional<PdfBinarioEntity> findByNomeOriginal(String filename);
}
