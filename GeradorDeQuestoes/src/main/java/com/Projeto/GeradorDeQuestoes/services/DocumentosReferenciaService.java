package com.Projeto.GeradorDeQuestoes.services;

import com.Projeto.GeradorDeQuestoes.dto.DocumentosReferenciaDTO;
import com.Projeto.GeradorDeQuestoes.entities.DocumentosReferenciaEntity;
import com.Projeto.GeradorDeQuestoes.entities.PdfBinarioEntity;
import com.Projeto.GeradorDeQuestoes.enums.NivelTecnico;

public interface DocumentosReferenciaService {
    
    DocumentosReferenciaEntity SalvarDocumentoReferencia(DocumentosReferenciaDTO documentosReferenciaDTO);
    DocumentosReferenciaEntity vincularContexto(PdfBinarioEntity pdf, String topico, NivelTecnico nivel, String fonte);
}
