package com.Projeto.GeradorDeQuestoes.services;

import com.Projeto.GeradorDeQuestoes.dto.DocumentosReferenciaDTO;
import com.Projeto.GeradorDeQuestoes.entities.DocumentosReferenciaEntity;
import com.Projeto.GeradorDeQuestoes.entities.PdfBinarioEntity;

public interface DocumentosReferenciaService {
    
    DocumentosReferenciaEntity SalvarDocumentoReferencia(DocumentosReferenciaDTO documentosReferenciaDTO);
    DocumentosReferenciaEntity vincularContexto(PdfBinarioEntity pdf, String topico, String fonte);
}
