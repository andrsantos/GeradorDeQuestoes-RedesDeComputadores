package com.Projeto.GeradorDeQuestoes.services;

import java.util.List;
import com.Projeto.GeradorDeQuestoes.dto.CenarioConfigDTO;
import com.Projeto.GeradorDeQuestoes.dto.DocumentoExibicaoDTO;
import com.Projeto.GeradorDeQuestoes.dto.TopicoConfigDTO;
import com.Projeto.GeradorDeQuestoes.dto.VectorStoreDTO;
import com.Projeto.GeradorDeQuestoes.entities.CenarioConfigEntity;
import com.Projeto.GeradorDeQuestoes.entities.TopicoConfigEntity;


public interface GerenciamentoService {

   List<TopicoConfigEntity> listarTopicos();
   TopicoConfigDTO criarTopico(TopicoConfigDTO topicoConfigDTO);
   void deletarTopico(String id);
   TopicoConfigDTO atualizarTopico(String id, TopicoConfigDTO topicoConfigDTO);

   List<CenarioConfigEntity> listarCenarios();
   CenarioConfigDTO criarCenario(CenarioConfigDTO cenarioConfigDTO);
   void deletarCenario(Long id);
   CenarioConfigDTO atualizarCenario(Long id, CenarioConfigDTO cenarioConfigDTO);  

   List<VectorStoreDTO> listarDocumentos();
   List<DocumentoExibicaoDTO> listarDocumentosParaGerenciamento();
   List<DocumentoExibicaoDTO> listarDocumentosCadastrados();

   void deletarDocumento(String idBinario);



    
}
