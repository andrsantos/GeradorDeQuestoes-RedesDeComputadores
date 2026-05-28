package com.Projeto.GeradorDeQuestoes.repositories;

import com.Projeto.GeradorDeQuestoes.entities.VectorStoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface VectorStoreRepository extends JpaRepository<VectorStoreEntity, UUID> {

    @Query(value = "SELECT DISTINCT metadata->>'topico' FROM vector_store WHERE metadata->>'topico' IS NOT NULL", 
           nativeQuery = true)
    List<String> findDistinctTopicos();

    @Query(value = "SELECT DISTINCT conceito " +
                   "FROM vector_store, " +
                   "jsonb_array_elements_text(metadata->'conceitos') AS conceito " +
                   "WHERE metadata->>'topico' = :topico", 
           nativeQuery = true)
    List<String> findDistinctConceitosByTopico(@Param("topico") String topico);

    
}