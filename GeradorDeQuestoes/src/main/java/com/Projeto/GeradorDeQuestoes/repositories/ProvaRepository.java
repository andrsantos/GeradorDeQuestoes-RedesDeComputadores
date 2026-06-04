package com.Projeto.GeradorDeQuestoes.repositories;

import com.Projeto.GeradorDeQuestoes.dto.ProvaInfoDTO;
import com.Projeto.GeradorDeQuestoes.entities.ProvaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProvaRepository extends JpaRepository<ProvaEntity, UUID> {

    @Query("SELECT new com.Projeto.GeradorDeQuestoes.dto.ProvaInfoDTO(p.id, p.titulo, p.dataCriacao, SIZE(p.questoes)) " +
           "FROM ProvaEntity p ORDER BY p.dataCriacao DESC")
    List<ProvaInfoDTO> findAllWithInfo();
}