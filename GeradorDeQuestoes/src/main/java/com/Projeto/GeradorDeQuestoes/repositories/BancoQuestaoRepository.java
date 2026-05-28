package com.Projeto.GeradorDeQuestoes.repositories;

import com.Projeto.GeradorDeQuestoes.entities.BancoQuestaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BancoQuestaoRepository extends JpaRepository<BancoQuestaoEntity, UUID> {
 List<BancoQuestaoEntity> findByTopico(String topico);
 List<BancoQuestaoEntity> findByNivel(String nivel);
 List<BancoQuestaoEntity> findByConceito(String conceito);
}