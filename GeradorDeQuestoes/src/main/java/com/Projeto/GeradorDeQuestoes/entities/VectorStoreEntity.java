package com.Projeto.GeradorDeQuestoes.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "vector_store") 
public class VectorStoreEntity {
    @Id
    private UUID id;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; 

    @Column(name = "content", columnDefinition = "text")
    private String content;
    
    public String getMetadata() { return metadata; }



    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    
}