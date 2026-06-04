package com.Projeto.GeradorDeQuestoes.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Projeto.GeradorDeQuestoes.dto.CenarioConfigDTO;
import com.Projeto.GeradorDeQuestoes.dto.FiltroGerenciamentoDTO;
import com.Projeto.GeradorDeQuestoes.dto.TopicoAgrupadoDTO;
import com.Projeto.GeradorDeQuestoes.dto.TopicoConfigDTO;
import com.Projeto.GeradorDeQuestoes.services.GerenciamentoService;


@RestController
@RequestMapping("/api/gerenciamento")
@CrossOrigin(origins = "http://localhost:4200")
public class GerenciamentoController {

    private final GerenciamentoService gerenciamentoService;


    public GerenciamentoController(GerenciamentoService gerenciamentoService) {
        this.gerenciamentoService = gerenciamentoService;
    }

    @PostMapping("/listar")
    public List<?> listarGerenciamento(@RequestBody FiltroGerenciamentoDTO filtro) {

        System.out.println("Filtro recebido: " + filtro.getFiltro().name());

        switch (filtro.getFiltro()) {
            case PROMPTS:
                return gerenciamentoService.listarTopicos();
            case CENARIO:
                return gerenciamentoService.listarCenarios();
            default:
                throw new IllegalArgumentException("Filtro inválido: " + filtro.getFiltro());
        }

    }
    

    // ****** OPERAÇÕES CRUD PARA CENÁRIOS ****** //
    @PostMapping("/criar/cenario")
    public CenarioConfigDTO criarCenario(@RequestBody CenarioConfigDTO cenarioConfigDTO) {
       return gerenciamentoService.criarCenario(cenarioConfigDTO);
    }

    @DeleteMapping("/deletar/cenario/{id}")
    public void deletarCenario(@PathVariable Long id) {
        System.out.println("ID recebido para deleção: " + id);
        gerenciamentoService.deletarCenario(id);
    }

    @PutMapping("/atualizar/cenario/{id}")
    public CenarioConfigDTO atualizarCenario(@PathVariable Long id, @RequestBody CenarioConfigDTO cenarioConfigDTO) {
        return gerenciamentoService.atualizarCenario(id, cenarioConfigDTO);
    }

    // ****** OPERAÇÕES CRUD PARA PROMPTS ****** //
    @PostMapping("/criar/prompt")
    public TopicoConfigDTO criarPrompts(@RequestBody TopicoConfigDTO topicoConfigDTO) {
       return gerenciamentoService.criarTopico(topicoConfigDTO);
    }

    @DeleteMapping("/deletar/prompt/{id}")
    public void deletarPrompt(@PathVariable String id) {
        System.out.println("ID recebido para deleção: " + id);
        gerenciamentoService.deletarTopico(id);
    }

    @PutMapping("/atualizar/prompt/{id}")
    public TopicoConfigDTO atualizarPrompt(@PathVariable String id, @RequestBody TopicoConfigDTO topicoConfigDTO) {
        System.out.println("ID recebido para atualização: " + id);
        return gerenciamentoService.atualizarTopico(id, topicoConfigDTO);
    }

    // ****** OPERAÇÕES CRUD PARA DOCUMENTOS ****** //
    @GetMapping("/listar/documentos")
    public ResponseEntity<?> listarDocumentos(){

        try{
            return ResponseEntity.ok(this.gerenciamentoService.listarDocumentos());
            } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage()); 
        }
    }

    @GetMapping("/listar/documentos/filtrados")
    public ResponseEntity<List<TopicoAgrupadoDTO>> listarDocumentosFiltrados() {
        try {

            List<TopicoAgrupadoDTO> lista = gerenciamentoService
            .listarDocumentosAgrupados();

            
            return ResponseEntity.ok(lista);

        } catch (Exception e) {

            return ResponseEntity.internalServerError().build();

        }
}

    @DeleteMapping("/deletar/documento/{id}")
    public ResponseEntity<?> deletarDocumento(@PathVariable String id) {
        try {
            System.out.println("ID recebido para deleção de documento: " + id);
            gerenciamentoService.deletarDocumento(id);
            return ResponseEntity.ok().body("Documento deletado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao deletar documento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao deletar o documento: " + e.getMessage());
        }
    }



}
