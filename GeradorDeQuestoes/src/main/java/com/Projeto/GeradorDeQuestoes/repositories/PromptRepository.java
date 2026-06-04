package com.Projeto.GeradorDeQuestoes.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Projeto.GeradorDeQuestoes.entities.PromptEntity;

public interface PromptRepository extends JpaRepository<PromptEntity, String> {

    @Modifying
    @Query("UPDATE PromptEntity p SET p.ativo = false WHERE p.topico.id = :topicoId AND p.nivel = :nivel")
    void desativarPromptsAntigos(@Param("topicoId") String topicoId, @Param("nivel") String nivel);

    List<PromptEntity> findByTopicoNome(String nomeTopico);

    List<PromptEntity> findByTopicoIdAndNivel(UUID topicoId, String nivel);

    Optional<PromptEntity> findByTopicoNomeAndNivelAndAtivoTrue(String topicoNome, String nivel);
    
}
