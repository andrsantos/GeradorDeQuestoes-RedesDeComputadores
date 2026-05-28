package com.Projeto.GeradorDeQuestoes.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_documentos_prompts")
public class DocumentoPromptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String idPrompt;

    private UUID idDocumento;


    public DocumentoPromptEntity() {
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIdPrompt() {
        return this.idPrompt;
    }

    public void setIdPrompt(String idPrompt) {
        this.idPrompt = idPrompt;
    }
    
    public UUID getIdDocumento() {
        return this.idDocumento;
    }

    public void setIdDocumento(UUID idDocumento) {
        this.idDocumento = idDocumento;
    }


}
