package com.Projeto.GeradorDeQuestoes.controllers;
import com.Projeto.GeradorDeQuestoes.dto.Prova;
import com.Projeto.GeradorDeQuestoes.dto.ProvaInfoDTO;
import com.Projeto.GeradorDeQuestoes.dto.Questao;
import com.Projeto.GeradorDeQuestoes.entities.ProvaEntity;
import com.Projeto.GeradorDeQuestoes.entities.QuestaoProvaEntity;
import com.Projeto.GeradorDeQuestoes.repositories.ProvaRepository;
import com.Projeto.GeradorDeQuestoes.repositories.QuestaoProvaRepository;
import com.Projeto.GeradorDeQuestoes.services.PdfService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/provas-salvas")
@CrossOrigin(origins = "http://localhost:4200") 
public class ProvaSalvaController {

    private final ProvaRepository provaRepository;
    private final PdfService pdfService;
    private final QuestaoProvaRepository questaoProvaRepository;


    public ProvaSalvaController(ProvaRepository provaRepository, 
        PdfService pdfService,
        QuestaoProvaRepository questaoProvaRepository) {
        this.provaRepository = provaRepository;
        this.pdfService = pdfService;
        this.questaoProvaRepository = questaoProvaRepository;
    }

    @PostMapping
    public ResponseEntity<ProvaEntity> salvarProva(@RequestBody Prova provaDto) {
        
        ProvaEntity novaProva = new ProvaEntity();
        
        String titulo = "Prova Gerada - " + java.time.LocalDate.now().toString();
        novaProva.setTitulo(titulo);

        if (provaDto.getQuestoes() != null && !provaDto.getQuestoes().isEmpty()) {
            
            for (Questao questaoDto : provaDto.getQuestoes()) {
                QuestaoProvaEntity questaoEntity = new QuestaoProvaEntity();
                
                questaoEntity.setEnunciado(questaoDto.getEnunciado());
                questaoEntity.setAlternativas(questaoDto.getAlternativas());
                questaoEntity.setRespostaCorreta(questaoDto.getRespostaCorreta());
                questaoEntity.setComentarioTecnico(questaoDto.getComentarioTecnico());
                
                novaProva.addQuestao(questaoEntity); 
            }
        }

        ProvaEntity provaSalva = provaRepository.save(novaProva);

        System.out.println("CONTROLLER: Prova salva com sucesso! ID: " + provaSalva.getId());

        return ResponseEntity.ok(provaSalva);
    }

    @GetMapping
    public ResponseEntity<List<ProvaInfoDTO>> getListaProvas() {
        List<ProvaInfoDTO> provas = provaRepository.findAllWithInfo();
        return ResponseEntity.ok(provas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvaEntity> getDetalheProva(@PathVariable UUID id) {
        return provaRepository.findById(id)
                .map(ResponseEntity::ok) 
                .orElse(ResponseEntity.notFound().build()); 
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProva(@PathVariable UUID id) {
        if (!provaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        provaRepository.deleteById(id);
        System.out.println("CONTROLLER: Prova " + id + " excluída.");
        return ResponseEntity.noContent().build(); 
    }

    @PutMapping("/questoes/{idQuestao}")
    public ResponseEntity<QuestaoProvaEntity> atualizarQuestao(
            @PathVariable UUID idQuestao,
            @RequestBody Questao questaoDto) {
        
        return questaoProvaRepository.findById(idQuestao)
            .map(entity -> {
                entity.setEnunciado(questaoDto.getEnunciado());
                entity.setAlternativas(questaoDto.getAlternativas());
                entity.setRespostaCorreta(questaoDto.getRespostaCorreta());
                QuestaoProvaEntity saved = questaoProvaRepository.save(entity);
                return ResponseEntity.ok(saved);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download-pdf")
    public ResponseEntity<byte[]> baixarProvaPdf(@PathVariable UUID id) {
        
        ProvaEntity provaEntity = provaRepository.findById(id).orElse(null);
        if (provaEntity == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Prova provaDto = convertEntityToDto(provaEntity);
            
            byte[] pdfBytes = pdfService.gerarPdfProva(provaDto);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "prova_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

  
    private Prova convertEntityToDto(ProvaEntity entity) {
        Prova provaDto = new Prova(); 
        
        List<Questao> questoesDto = entity.getQuestoes().stream()
                .map(qe -> new Questao( 
                        qe.getId(),
                        qe.getEnunciado(),
                        qe.getAlternativas(),
                        qe.getRespostaCorreta(),
                        qe.getComentarioTecnico()
                ))
                .collect(Collectors.toList());

        questoesDto.forEach(provaDto::adicionarQuestao);
        
        return provaDto;
    }
}