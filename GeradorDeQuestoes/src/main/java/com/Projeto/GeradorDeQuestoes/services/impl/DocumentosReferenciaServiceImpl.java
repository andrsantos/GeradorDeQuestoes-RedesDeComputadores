package com.Projeto.GeradorDeQuestoes.services.impl;

import org.springframework.stereotype.Service;

import com.Projeto.GeradorDeQuestoes.dto.DocumentosReferenciaDTO;
import com.Projeto.GeradorDeQuestoes.entities.DocumentosReferenciaEntity;
import com.Projeto.GeradorDeQuestoes.entities.PdfBinarioEntity;
import com.Projeto.GeradorDeQuestoes.repositories.DocumentosReferenciaRepository;
import com.Projeto.GeradorDeQuestoes.services.DocumentosReferenciaService;

@Service
public class DocumentosReferenciaServiceImpl implements DocumentosReferenciaService {

    private DocumentosReferenciaRepository documentosReferenciaRepository;


    DocumentosReferenciaServiceImpl(DocumentosReferenciaRepository documentosReferenciaRepository){
        this.documentosReferenciaRepository = documentosReferenciaRepository;
    }


    @Override
    public DocumentosReferenciaEntity SalvarDocumentoReferencia(DocumentosReferenciaDTO documentosReferenciaDTO) {
        DocumentosReferenciaEntity documentosReferenciaEntity = new DocumentosReferenciaEntity();
        documentosReferenciaEntity.setTopico(documentosReferenciaDTO.getTopico());
        return documentosReferenciaRepository.save(documentosReferenciaEntity);
    }


    public DocumentosReferenciaEntity vincularContexto(PdfBinarioEntity pdf, String topico, String fonte) {

    DocumentosReferenciaEntity referencia = new DocumentosReferenciaEntity();
    referencia.setPdfBinario(pdf); 
    referencia.setTopico(topico);
    referencia.setFonte(fonte);
    
    return documentosReferenciaRepository.save(referencia);
}

    
    
}
