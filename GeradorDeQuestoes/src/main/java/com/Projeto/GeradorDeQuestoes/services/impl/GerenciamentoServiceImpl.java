package com.Projeto.GeradorDeQuestoes.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.Projeto.GeradorDeQuestoes.dto.CenarioConfigDTO;
import com.Projeto.GeradorDeQuestoes.dto.TopicoConfigDTO;
import com.Projeto.GeradorDeQuestoes.dto.VectorStoreDTO;
import com.Projeto.GeradorDeQuestoes.entities.CenarioConfigEntity;
import com.Projeto.GeradorDeQuestoes.entities.DocumentosReferenciaEntity;
import com.Projeto.GeradorDeQuestoes.entities.TopicoConfigEntity;
import com.Projeto.GeradorDeQuestoes.entities.VectorStoreEntity;
import com.Projeto.GeradorDeQuestoes.repositories.CenarioConfigRepository;
import com.Projeto.GeradorDeQuestoes.repositories.DocumentosReferenciaRepository;
import com.Projeto.GeradorDeQuestoes.repositories.TopicoConfigRepository;
import com.Projeto.GeradorDeQuestoes.repositories.VectorStoreRepository;
import com.Projeto.GeradorDeQuestoes.dto.DocumentoExibicaoDTO;
import com.Projeto.GeradorDeQuestoes.services.GerenciamentoService;

@Service
public class GerenciamentoServiceImpl implements GerenciamentoService {

    private final TopicoConfigRepository topicoConfigRepository;
    private final CenarioConfigRepository cenarioConfigRepository;
    private final VectorStoreRepository vectorStoreRepository;
    private final DocumentosReferenciaRepository documentosReferenciaRepository;


    public GerenciamentoServiceImpl(TopicoConfigRepository topicoConfigRepository, CenarioConfigRepository cenarioConfigRepository,
        VectorStoreRepository vectorStoreRepository, DocumentosReferenciaRepository documentosReferenciaRepository
    ) {
        this.topicoConfigRepository = topicoConfigRepository;
        this.cenarioConfigRepository = cenarioConfigRepository;
        this.vectorStoreRepository = vectorStoreRepository;
        this.documentosReferenciaRepository = documentosReferenciaRepository;
    }
    
    /** OPERAÇÕES CRUD DE PROMPTS **/
    @Override
    public List<TopicoConfigEntity> listarTopicos() {
        return  topicoConfigRepository.findAll();
    }

    @Override
    public TopicoConfigDTO criarTopico(TopicoConfigDTO topicoConfigDTO) {
        TopicoConfigEntity topicoConfigEntity = new TopicoConfigEntity();
        topicoConfigEntity.setTopico(topicoConfigDTO.getTopico());
        topicoConfigEntity.setNivel(topicoConfigDTO.getNivel());
        topicoConfigEntity.setInstrucoesEspecificas(topicoConfigDTO.getInstrucoesEspecificas());
        TopicoConfigEntity savedEntity = topicoConfigRepository.save(topicoConfigEntity);
        TopicoConfigDTO savedDTO = new TopicoConfigDTO();
        savedDTO.setId(savedEntity.getId());
        savedDTO.setTopico(savedEntity.getTopico());
        savedDTO.setNivel(savedEntity.getNivel());
        savedDTO.setInstrucoesEspecificas(savedEntity.getInstrucoesEspecificas());
        return savedDTO;
    }

    @Override
    public void deletarTopico(String id) {
        topicoConfigRepository.deleteById(id);
    }

    @Override
    public TopicoConfigDTO atualizarTopico(String id, TopicoConfigDTO topicoConfigDTO) {
        TopicoConfigEntity topicoConfigEntity = topicoConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado com ID: " + id));

        topicoConfigEntity.setTopico(topicoConfigDTO.getTopico());
        topicoConfigEntity.setNivel(topicoConfigDTO.getNivel());
        topicoConfigEntity.setInstrucoesEspecificas(topicoConfigDTO.getInstrucoesEspecificas());

        TopicoConfigEntity atualizado = topicoConfigRepository.save(topicoConfigEntity);

        TopicoConfigDTO atualizadoDTO = new TopicoConfigDTO();
        atualizadoDTO.setId(atualizado.getId());
        atualizadoDTO.setTopico(atualizado.getTopico());
        atualizadoDTO.setNivel(atualizado.getNivel());
        atualizadoDTO.setInstrucoesEspecificas(atualizado.getInstrucoesEspecificas());

        return atualizadoDTO;
    }

    /** OPERAÇÕES CRUD DE CENÁRIOS **/
    @Override
    public List<CenarioConfigEntity> listarCenarios() {
        return cenarioConfigRepository.findAll();
    }

    @Override
    public CenarioConfigDTO criarCenario(CenarioConfigDTO cenarioConfigDTO) {
        CenarioConfigEntity cenarioConfigEntity = new CenarioConfigEntity();
        cenarioConfigEntity.setTopico(cenarioConfigDTO.getTopico());
        cenarioConfigEntity.setNivel(cenarioConfigDTO.getNivel());
        cenarioConfigEntity.setDescricao(cenarioConfigDTO.getDescricao());
        CenarioConfigEntity savedEntity = cenarioConfigRepository.save(cenarioConfigEntity);
        CenarioConfigDTO savedDTO = new CenarioConfigDTO();
        savedDTO.setId(savedEntity.getId());
        savedDTO.setTopico(savedEntity.getTopico());
        savedDTO.setNivel(savedEntity.getNivel());
        savedDTO.setDescricao(savedEntity.getDescricao());
        return savedDTO;
    }

    @Override
    public void deletarCenario(Long id) {
        cenarioConfigRepository.deleteById(id);
    }

    @Override
    public CenarioConfigDTO atualizarCenario(Long id, CenarioConfigDTO cenarioConfigDTO) {
        CenarioConfigEntity cenarioConfigEntity = cenarioConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cenário não encontrado com ID: " + id));

        cenarioConfigEntity.setTopico(cenarioConfigDTO.getTopico());
        cenarioConfigEntity.setNivel(cenarioConfigDTO.getNivel());
        cenarioConfigEntity.setDescricao(cenarioConfigDTO.getDescricao());

        CenarioConfigEntity atualizado = cenarioConfigRepository.save(cenarioConfigEntity);

        CenarioConfigDTO atualizadoDTO = new CenarioConfigDTO();
        atualizadoDTO.setId(atualizado.getId());
        atualizadoDTO.setTopico(atualizado.getTopico());
        atualizadoDTO.setNivel(atualizado.getNivel());
        atualizadoDTO.setDescricao(atualizado.getDescricao());

        return atualizadoDTO;
    }
    

    /** OPERAÇÕES CRUD DE DOCUMENTOS **/

    @Override
    public List<VectorStoreDTO> listarDocumentos() {

        List<VectorStoreEntity> documentos = this.vectorStoreRepository.findAll();
        List<VectorStoreDTO> documentosDto = new ArrayList<>();

        documentos.forEach(documento -> {
            VectorStoreDTO documentoDto = new VectorStoreDTO();
            documentoDto.setConteudo(documento.getContent());
            documentoDto.setMetadata(documento.getMetadata());
            documentosDto.add(documentoDto);
        });

        return documentosDto;
    
    }

    @Override
    public List<DocumentoExibicaoDTO> listarDocumentosParaGerenciamento() {
        return documentosReferenciaRepository.findAll().stream().map(ref -> {
            DocumentoExibicaoDTO dto = new DocumentoExibicaoDTO();
            dto.setIdBinario(ref.getPdfBinario().getId()); 
            dto.setTopico(ref.getTopico());
            dto.setNivel(ref.getNivel().toString());
            dto.setMaterialReferencia(ref.getPdfBinario().getNomeOriginal());
            return dto;
        }).toList();
    }

    @Override
    public List<DocumentoExibicaoDTO> listarDocumentosCadastrados() {

        List<DocumentosReferenciaEntity> referencias = documentosReferenciaRepository.findAll();
        return referencias.stream().map(ref -> new DocumentoExibicaoDTO(
            ref.getId(),
            ref.getPdfBinario().getId(),
            ref.getTopico(),
            ref.getFonte(), 
            ref.getNivel().toString(),
            ref.getPdfBinario().getNomeOriginal()
        )).toList();
    }

    
}
