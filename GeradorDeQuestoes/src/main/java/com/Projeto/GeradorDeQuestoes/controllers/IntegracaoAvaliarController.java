package com.Projeto.GeradorDeQuestoes.controllers;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Projeto.GeradorDeQuestoes.dto.QuestaoFormatoAvaliarDTO;
import com.Projeto.GeradorDeQuestoes.services.IntegracaoAvaliarService;


@RestController
@RequestMapping("/api/integracao/avaliar")
@CrossOrigin(origins = "http://localhost:4200")
public class IntegracaoAvaliarController {

    private IntegracaoAvaliarService  integracaoAvaliarService;

    public IntegracaoAvaliarController(IntegracaoAvaliarService integracaoAvaliarService) {
        this.integracaoAvaliarService = integracaoAvaliarService;
    }

    @PostMapping("/exportar")
    public ResponseEntity<String> exportarQuestoesParaAvaliar(@RequestBody List<QuestaoFormatoAvaliarDTO> questoes) {

        String resultadoFormatoAvaliar = this.integracaoAvaliarService.converterParaFormatoAvaliar(questoes);
    
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prova.txt")
            .contentType(MediaType.TEXT_PLAIN)
            .body(resultadoFormatoAvaliar);
  
    }


    
}
