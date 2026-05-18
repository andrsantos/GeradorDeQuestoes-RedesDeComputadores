package com.Projeto.GeradorDeQuestoes.services;

import com.Projeto.GeradorDeQuestoes.dto.GeracaoAutomaticaRequest;
import com.Projeto.GeradorDeQuestoes.dto.GerarQuestaoRequest;
import com.Projeto.GeradorDeQuestoes.dto.ListaQuestoes;
import com.Projeto.GeradorDeQuestoes.dto.Prova;
import com.Projeto.GeradorDeQuestoes.dto.Questao;
import com.Projeto.GeradorDeQuestoes.dto.TopicoQuantidade;
import com.Projeto.GeradorDeQuestoes.entities.ProvaEntity;
import com.Projeto.GeradorDeQuestoes.entities.QuestaoProvaEntity;
import com.Projeto.GeradorDeQuestoes.repositories.ProvaRepository;
import com.Projeto.GeradorDeQuestoes.repositories.TopicoConfigRepository;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeradorProvaService {

    private static final Map<UUID, Prova> provasEmMemoria = new ConcurrentHashMap<>();
    private final GeradorQuestaoService questaoService;
    private final ProvaRepository provaRepository;
    private final PdfService pdfService;

    public GeradorProvaService(GeradorQuestaoService questaoService, 
    ProvaRepository provaRepository, 
    PdfService pdfService,
    TopicoConfigRepository configRepository
    ) {
        this.questaoService = questaoService;
        this.provaRepository = provaRepository;
        this.pdfService = pdfService;
    }

  
    public Prova criarNovaProva() {
        Prova novaProva = new Prova();
        provasEmMemoria.put(novaProva.getId(), novaProva);
        return novaProva;
    }


    public Prova getProva(UUID id) {
        return provasEmMemoria.get(id);
    }

  
    public Prova adicionarQuestoes(UUID idProva, GerarQuestaoRequest request) {
        Prova prova = getProva(idProva);
        if (prova == null) {
            throw new RuntimeException("Prova não encontrada!"); 
        }

        ListaQuestoes novasQuestoes = questaoService.gerarQuestoes(request);

        novasQuestoes.questoes().forEach(prova::adicionarQuestao);

        return prova;
    }

    public Prova adicionarQuestoesDoBanco(UUID idProva, List<Questao> questoesBanco) {
        Prova prova = getProva(idProva);
        if (prova == null) {
            throw new RuntimeException("Prova não encontrada!"); 
        }

        questoesBanco.forEach(prova::adicionarQuestao);

        return prova;
    }


  
    public Prova descartarQuestao(UUID idProva, int indiceQuestao) {
        Prova prova = getProva(idProva);
        if (prova == null) {
            throw new RuntimeException("Prova não encontrada!");
        }
        prova.removerQuestao(indiceQuestao);
        return prova;
    }

    public byte[] finalizarEGerarPdf(UUID idProva) throws IOException {
        Prova provaEmMemoria = getProva(idProva);
        if (provaEmMemoria == null) {
            throw new RuntimeException("Prova não encontrada!");
        }

        ProvaEntity provaEntity = new ProvaEntity();
        provaEntity.setId(provaEmMemoria.getId());
        provaEntity.setDataCriacao(OffsetDateTime.now());
        provaEntity.setTitulo("Prova de Redes - " + provaEmMemoria.getId().toString().substring(0, 8));

        for (Questao questaoDto : provaEmMemoria.getQuestoes()) {
            QuestaoProvaEntity questaoEntity = new QuestaoProvaEntity();
            questaoEntity.setEnunciado(questaoDto.getEnunciado());
            questaoEntity.setAlternativas(questaoDto.getAlternativas());
            questaoEntity.setRespostaCorreta(questaoDto.getRespostaCorreta());
            
            provaEntity.addQuestao(questaoEntity); 
        }

        provaRepository.save(provaEntity);

        byte[] pdfBytes = pdfService.gerarPdfProva(provaEmMemoria);
        System.out.println("SERVICE: PDF gerado para prova " + idProva);

        provasEmMemoria.remove(idProva);
        System.out.println("SERVICE: Prova " + idProva + " removida da memória.");
        
        return pdfBytes;
    }

    public Prova adicionarQuestoesAutomatico(UUID idProva, GeracaoAutomaticaRequest request) {
        
        Prova prova = getProva(idProva);
        if (prova == null) {
            throw new RuntimeException("Prova não encontrada!");
        }

        Set<String> topicosProcessados = new HashSet<>();

        for (TopicoQuantidade tp : request.getTopicos()) {
            
            if (topicosProcessados.contains(tp.getTopico())) {
                System.out.println("SERVICE: Tópico duplicado ignorado: " + tp.getTopico());
                continue;
            }
            
            topicosProcessados.add(tp.getTopico());

            GerarQuestaoRequest ragRequest = new GerarQuestaoRequest(tp.getTopico(), tp.getQuantidade(), 
            tp.getQuantidadeDificeis(), tp.getQuantidadeMedias(), tp.getQuantidadeFaceis());
            
            ListaQuestoes novasQuestoes = questaoService.gerarQuestoes(ragRequest);


            novasQuestoes.questoes().forEach(questao -> {

            String letraCorretaOriginal = questao.getRespostaCorreta().toLowerCase();
            String textoCorreto = questao.getAlternativas().get(letraCorretaOriginal);

            List<String> textos = new ArrayList<>(questao.getAlternativas().values());
            Collections.shuffle(textos);

            Map<String, String> novasAlternativas = new LinkedHashMap<>();
            String[] letras = {"a", "b", "c", "d", "e"};
            String novaLetraCorreta = "";

            for (int i = 0; i < textos.size(); i++) {
                String letraAtual = letras[i];
                String textoAtual = textos.get(i);
                
                novasAlternativas.put(letraAtual, textoAtual);

                if (textoAtual.equals(textoCorreto)) {
                    novaLetraCorreta = letraAtual;
                }
            }

            questao.setAlternativas(novasAlternativas);
            questao.setRespostaCorreta(novaLetraCorreta);

            System.out.println("--- Questão Randomizada ---");
            System.out.println("Nova correta: " + questao.getRespostaCorreta());
            System.out.println("Novas alternativas: " + questao.getAlternativas());
 
            });
            
            novasQuestoes.questoes().forEach(prova::adicionarQuestao);
            
            System.out.println("SERVICE: Adicionadas " + novasQuestoes.questoes().size() 
                             + " questões do tópico '" + tp.getTopico() + "' à prova " + idProva);
        }
        
        return prova;
    }

 
    public Prova adicionarQuestoesManuais(UUID idProva, List<Questao> questoes) {
        Prova prova = getProva(idProva);
        if (prova == null) {
            throw new RuntimeException("Prova não encontrada!");
        }
        
        questoes.forEach(prova::adicionarQuestao);
        
        System.out.println("SERVICE: Adicionadas " + questoes.size() + " questões manuais à prova " + idProva);
        return prova;
    }

}