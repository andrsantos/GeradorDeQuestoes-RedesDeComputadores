package com.Projeto.GeradorDeQuestoes.services.impl;

import org.springframework.stereotype.Service;

import com.Projeto.GeradorDeQuestoes.dto.DocumentosReferenciaDTO;
import com.Projeto.GeradorDeQuestoes.entities.DocumentosReferenciaEntity;
import com.Projeto.GeradorDeQuestoes.entities.PdfBinarioEntity;
import com.Projeto.GeradorDeQuestoes.entities.TopicoEntity;
import com.Projeto.GeradorDeQuestoes.repositories.DocumentosReferenciaRepository;
import com.Projeto.GeradorDeQuestoes.repositories.TopicoRepository;
import com.Projeto.GeradorDeQuestoes.services.DocumentosReferenciaService;

@Service
public class DocumentosReferenciaServiceImpl implements DocumentosReferenciaService {

    private DocumentosReferenciaRepository documentosReferenciaRepository;

    private TopicoRepository topicoRepository;


    DocumentosReferenciaServiceImpl(DocumentosReferenciaRepository documentosReferenciaRepository, TopicoRepository topicoRepository){
        this.documentosReferenciaRepository = documentosReferenciaRepository;
        this.topicoRepository = topicoRepository;
    }


    @Override
    public DocumentosReferenciaEntity SalvarDocumentoReferencia(DocumentosReferenciaDTO documentosReferenciaDTO) {

        DocumentosReferenciaEntity documentosReferenciaEntity = new DocumentosReferenciaEntity();
        String nomeDoTopico = documentosReferenciaDTO.getTopico();

        TopicoEntity topicoEntity = topicoRepository.findByNome(nomeDoTopico)
                .orElseGet(() -> {
                    TopicoEntity novoTopico = new TopicoEntity(nomeDoTopico);
                    return topicoRepository.save(novoTopico);
            });
        
        documentosReferenciaEntity.setTopico(topicoEntity);
        return documentosReferenciaRepository.save(documentosReferenciaEntity);
    }


    public DocumentosReferenciaEntity vincularContexto(PdfBinarioEntity pdf, String topico, String fonte) {
 
       TopicoEntity topicoEntity = topicoRepository.findByNome(topico)
                .orElseGet(() -> {
                    TopicoEntity novoTopico = new TopicoEntity(topico);
                    return topicoRepository.save(novoTopico);
        });
    
    DocumentosReferenciaEntity referencia = new DocumentosReferenciaEntity();
    referencia.setPdfBinario(pdf); 
    referencia.setTopico(topicoEntity);
    referencia.setFonte(fonte);
    
    return documentosReferenciaRepository.save(referencia);
}

    
    
}
