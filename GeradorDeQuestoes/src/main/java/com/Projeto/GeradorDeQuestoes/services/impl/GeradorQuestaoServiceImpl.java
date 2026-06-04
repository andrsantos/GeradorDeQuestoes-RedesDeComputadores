package com.Projeto.GeradorDeQuestoes.services.impl;

import com.Projeto.GeradorDeQuestoes.dto.AvaliacaoQuestao;
import com.Projeto.GeradorDeQuestoes.dto.GerarQuestaoRequest;
import com.Projeto.GeradorDeQuestoes.dto.ListaQuestoes;
import com.Projeto.GeradorDeQuestoes.dto.Questao;
import com.Projeto.GeradorDeQuestoes.dto.TopicoQuantidade;
import com.Projeto.GeradorDeQuestoes.entities.PromptEntity;
import com.Projeto.GeradorDeQuestoes.entities.TopicoConfigEntity;
import com.Projeto.GeradorDeQuestoes.enums.NivelTecnico;
import com.Projeto.GeradorDeQuestoes.repositories.CenarioConfigRepository;
import com.Projeto.GeradorDeQuestoes.repositories.PromptRepository;
import com.Projeto.GeradorDeQuestoes.repositories.TopicoConfigRepository;
import com.Projeto.GeradorDeQuestoes.services.GeradorQuestaoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class GeradorQuestaoServiceImpl implements GeradorQuestaoService {

    private final ChatClient openAiChatClient;
    private final ChatClient anthropicChatClient;
    private final VectorStore vectorStore;
    private final TopicoConfigRepository topicoConfigRepository;
    private final CenarioConfigRepository cenarioConfigRepository;
    private final PromptRepository promptRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    public GeradorQuestaoServiceImpl(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                                     VectorStore vectorStore,
                                     TopicoConfigRepository configRepository,
                                     CenarioConfigRepository cenarioConfigRepository,
                                     PromptRepository promptRepository,
                                     @Qualifier("anthropicChatClient") ChatClient anthropicChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.anthropicChatClient = anthropicChatClient;
        this.vectorStore = vectorStore;
        this.topicoConfigRepository = configRepository;
        this.cenarioConfigRepository = cenarioConfigRepository;
        this.promptRepository = promptRepository;
    }

    @Override
    public ListaQuestoes gerarQuestoes(GerarQuestaoRequest request) {
        List<Questao> todasAsQuestoes = new ArrayList<>();

        if (request.topicos() == null || request.topicos().isEmpty()) {
            System.err.println("Aviso: Requisição de geração de questões chegou vazia.");
            return new ListaQuestoes(todasAsQuestoes);
        }

        for (TopicoQuantidade bloco : request.topicos()) {
            System.out.println("Processando bloco de geração para o assunto: " + bloco.getTopico());

            boolean buscaGeral = bloco.getSubtopicos() == null || bloco.getSubtopicos().isEmpty();

            if (buscaGeral) {
                if (bloco.getQuantidadeFaceis() > 0) {
                    todasAsQuestoes.addAll(gerarQuestoesParaConceito(bloco.getTopico(), "", "FACIL", bloco.getQuantidadeFaceis()));
                }
                if (bloco.getQuantidadeMedias() > 0) {
                    todasAsQuestoes.addAll(gerarQuestoesParaConceito(bloco.getTopico(), "", "MEDIO", bloco.getQuantidadeMedias()));
                }
                if (bloco.getQuantidadeDificeis() > 0) {
                    todasAsQuestoes.addAll(gerarQuestoesParaConceito(bloco.getTopico(), "", "DIFICIL", bloco.getQuantidadeDificeis()));
                }
            } else {
                for (var conceitoDto : bloco.getSubtopicos()) {
                    String nomeConceito = conceitoDto.getConceito();
                    
                    if (conceitoDto.getQuantidadeFaceis() > 0) {
                        todasAsQuestoes.addAll(gerarQuestoesParaConceito(bloco.getTopico(), nomeConceito, "FACIL", conceitoDto.getQuantidadeFaceis()));
                    }
                    if (conceitoDto.getQuantidadeMedias() > 0) {
                        todasAsQuestoes.addAll(gerarQuestoesParaConceito(bloco.getTopico(), nomeConceito, "MEDIO", conceitoDto.getQuantidadeMedias()));
                    }
                    if (conceitoDto.getQuantidadeDificeis() > 0) {
                        todasAsQuestoes.addAll(gerarQuestoesParaConceito(bloco.getTopico(), nomeConceito, "DIFICIL", conceitoDto.getQuantidadeDificeis()));
                    }
                }
            }
        }

        System.out.println("Geração concluída. Total de questões geradas: " + todasAsQuestoes.size());
        return new ListaQuestoes(todasAsQuestoes);
    }

    private String ajustandoStringDeNivel(String nivel){
        String nivelAjustado = "";
        if(nivel.equals("FACIL")){
        nivelAjustado = "UNIVERSITARIO_INICIANTE";
        return nivelAjustado;
        }
        if(nivel.equals("MEDIO")){
        nivelAjustado = "UNIVERSITARIO_INTERMEDIARIO";
        return nivelAjustado;
        } else {
        nivelAjustado = "UNIVERSITARIO_AVANCADO";
        return nivelAjustado;
        }
    }

    private List<Questao> gerarQuestoesParaConceito(String nomeTopico, String conceito, String nivel, int quantidadeSolicitada) {
        List<Questao> blocoFinal = new ArrayList<>();
        
        String instrucoesDoAgente;
        String nivelAjustado =  ajustandoStringDeNivel(nivel);
        Optional<PromptEntity> promptCustomizado = promptRepository.findByTopicoNomeAndNivelAndAtivoTrue(nomeTopico, nivelAjustado);
        if (promptCustomizado.isPresent()) {
            instrucoesDoAgente = promptCustomizado.get().getInstrucao();
            System.out.println("🤖 Usando Prompt Personalizado para o tópico: " + nomeTopico);
        } else {
            TopicoConfigEntity configPadrao = topicoConfigRepository.findByTopicoAndNivel("Padrao", nivel)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Erro Crítico: Não há prompt personalizado para '" + nomeTopico + 
                    "' e o prompt 'Padrao' também não está cadastrado para o nível " + nivel + "."));
            
            instrucoesDoAgente = configPadrao.getInstrucoesEspecificas();
            System.out.println("⚠️ Prompt personalizado não encontrado. Usando instrução Padrão do sistema.");
        }

        String conceitoDeExibicao = (conceito == null || conceito.isBlank()) ? "Geral (" + nomeTopico + ")" : conceito;
        int tentativasMaximas = quantidadeSolicitada * 3;
        int tentativasAtuais = 0;

        while (blocoFinal.size() < quantidadeSolicitada && tentativasAtuais < tentativasMaximas) {
            tentativasAtuais++;
            String contextoDoConceito = recuperarContextoDoBanco(nomeTopico, conceito);

            if (contextoDoConceito.isBlank()) {
                continue; 
            }

            Questao questaoFinal = null;
            int subTentativas = 0;
            String questaoRaw = null;
            
            while (questaoFinal == null && subTentativas < 3) {
                try {
                    if (questaoRaw == null) {
                        System.out.println("Gerando questão para conceito: " + conceitoDeExibicao + " (Nível: " + nivel + ")");
                        questaoRaw = chamarAgenteEscritor(nomeTopico, nivel, contextoDoConceito,conceitoDeExibicao);
                    }

                    List<Questao> listaRascunho = parsearRespostaTags(questaoRaw);
                    if (listaRascunho.isEmpty()) {
                        questaoRaw = null; 
                        subTentativas++;
                        continue;
                    }

                    Questao rascunho = listaRascunho.get(0);
                    
                    String questaoContextualizadaRaw = chamarAgenteContextualizador(rascunho, conceitoDeExibicao, instrucoesDoAgente);
                    
                    List<Questao> listaFinal = parsearRespostaTags(questaoContextualizadaRaw);
                    if (!listaFinal.isEmpty()) {
                        questaoFinal = listaFinal.get(0);
                    }
                } catch (Exception e) {
                    System.err.println("Erro na geração [" + conceitoDeExibicao + "]: " + e.getMessage());
                }
                subTentativas++;
            }

            if (questaoFinal != null) {
                questaoFinal = chamarAgenteJulgador(questaoFinal);
                questaoFinal.setConceito(conceitoDeExibicao); 
                
                AvaliacaoQuestao avaliacao = chamarAgenteAvaliador(questaoFinal);
                questaoFinal.setCompetencia(avaliacao.getCompetencia());
                questaoFinal.setComentarioTecnico(avaliacao.getComentarioTecnico());
                questaoFinal.setTopico(nomeTopico);
                questaoFinal.setNivel(converterDeStringParaNivelTecnico(nivel));

                blocoFinal.add(questaoFinal);
                System.out.println("Progresso (" + conceitoDeExibicao + "): " + blocoFinal.size() + "/" + quantidadeSolicitada);
            }
        }
        return blocoFinal;
    }

    private NivelTecnico converterDeStringParaNivelTecnico(String nivel){
        if(nivel.equals("FACIL")){
            return NivelTecnico.UNIVERSITARIO_INICIANTE;
        }
        if(nivel.equals("MEDIO")){
            return NivelTecnico.UNIVERSITARIO_INTERMEDIARIO;
        } else {
            return NivelTecnico.UNIVERSITARIO_AVANCADO;
        }
    }

    private String recuperarContextoDoBanco(String topico, String conceitoEspecífico) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();

        SearchRequest sr;
        
        if (conceitoEspecífico == null || conceitoEspecífico.isBlank()) {
            sr = SearchRequest.builder()
                    .query("Questão sobre " + topico)
                    .filterExpression(b.eq("topico", topico).build())
                    .topK(5)
                    .build();
            System.out.println("Busca Vetorial: ABRANGENTE para tópico [" + topico + "]");
        } else {
            sr = SearchRequest.builder()
                    .query("Explicação sobre " + conceitoEspecífico)
                    .filterExpression(b.and(
                        b.eq("topico", topico),
                        b.in("conceitos", conceitoEspecífico)
                    ).build())
                    .topK(3) 
                    .build();
            System.out.println("Busca Vetorial para conceito [" + conceitoEspecífico + "] em [" + topico + "]");
        }

        List<Document> documentos = this.vectorStore.similaritySearch(sr);

        if (documentos.isEmpty()) {
            System.err.println("Aviso: Nenhum contexto encontrado no pgvector para: " + (conceitoEspecífico.isBlank() ? topico : conceitoEspecífico));
            return "";
        }

        return documentos.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));
    }
    
    @Override
    public List<String> extrairConceitosUnicos(String contexto, int qtd) {
        String prompt = "Liste exatamente %d conceitos técnicos distintos (ex: Protocolo, Atraso de Fila) baseados no material: %s. Separe os itens obrigatoriamente por VÍRGULA.".formatted(qtd, contexto);
        String r = this.openAiChatClient.prompt(prompt).options(ChatOptions.builder().temperature(0.7).build()).call().content();
        
        String[] partes = r.split(",|\\n|\\r|\\d+\\.");
        
        return Arrays.stream(partes)
                .map(String::trim)
                .filter(s -> s.length() > 3 && s.length() < 60) 
                .distinct()
                .collect(Collectors.toList());
    }

   private List<Questao> parsearRespostaTags(String rawText) {
    List<Questao> questoes = new ArrayList<>();

    Pattern patternBloco = Pattern.compile(
        "(?si)\\[ENUNCIADO\\](.*?)\\[/ENUNCIADO\\]" +
        "(.*?)" +
        "\\[RESPOSTA\\]\\s*(.*?)(?=\\[EXPLICACAO\\])" +
        "\\[EXPLICACAO\\]\\s*(.*?)\\[/EXPLICACAO\\]"
    );

    Matcher matcher = patternBloco.matcher(rawText);
    while (matcher.find()) {
        try {
            String enunciado  = matcher.group(1).trim();
            String blocoAlts  = matcher.group(2).trim();
            String respostaRaw = matcher.group(3).trim();
            String explicacao = matcher.group(4).trim();

            Map<String, String> alternativas = extrairAlternativas(blocoAlts);
            String resposta = mapResposta(respostaRaw);

            boolean valido = !enunciado.isEmpty()
                    && !resposta.isEmpty()
                    && alternativas.size() == 5
                    && !alternativas.containsValue("");

            if (valido) {
                questoes.add(new Questao(
                    UUID.randomUUID().toString(),
                    enunciado,
                    alternativas,
                    resposta,
                    explicacao
                ));
            } else {
                System.err.println("Questão descartada — campos incompletos. Alts: "
                    + alternativas.size() + " | Resposta: '" + resposta + "'");
            }

        } catch (Exception e) {
            System.err.println("Erro Parse: " + e.getMessage());
        }
    }
    return questoes;
    }

    private Map<String, String> extrairAlternativas(String bloco) {
        Map<String, String> alts = new LinkedHashMap<>();

        Pattern p = Pattern.compile(
            "(?m)^\\[([A-Ea-e])\\]\\s*(.*?)(?=^\\[[A-Ea-e]\\]|\\[RESPOSTA\\]|$)",
            Pattern.DOTALL
        );

        Matcher m = p.matcher(bloco);
        while (m.find()) {
            String letra = m.group(1).toLowerCase();
            String texto = m.group(2).trim();
            if (!texto.isEmpty()) {
                alts.put(letra, texto);
            }
        }
        return alts;
    }

    private String mapResposta(String raw) {
        if (raw == null || raw.isBlank()) return "";

        Matcher mExato = Pattern.compile("(?i)^\\s*([a-e])\\s*$").matcher(raw.trim());
        if (mExato.find()) return mExato.group(1).toLowerCase();

        Matcher mFallback = Pattern.compile("(?i)([a-e])").matcher(raw);
        if (mFallback.find()) return mFallback.group(1).toLowerCase();

        return "";
    }

    private String chamarAgenteEscritor(String topico, String nivel, String contexto, String conceito) {
        
        String templateBase = """
            Você é um especialista técnico avaliador.
            Gere UMA questão de múltipla escolha com base EXCLUSIVAMENTE no contexto fornecido abaixo.
            Não invente informações que não estejam no contexto.

            ### PARÂMETROS DO SISTEMA ###
            - Conceito central: {conceito}
            - Nível de dificuldade: {nivel}
            - Tópico principal: {topico}

            ### CONTEXTO FONTE (Use apenas este material) ###
            {contexto}

            ### FORMATO DE SAÍDA OBRIGATÓRIO (Não adicione nenhum texto extra) ###
            [ENUNCIADO]
            <texto do enunciado objetivo e direto>
            [/ENUNCIADO]
            [A] <alternativa A>
            [B] <alternativa B>
            [C] <alternativa C>
            [D] <alternativa D>
            [E] <alternativa E>
            [RESPOSTA] <apenas a letra: a, b, c, d ou e>
            [EXPLICACAO]
            <explicação detalhada>
            [/EXPLICACAO]
            """;

        PromptTemplate template = new PromptTemplate(templateBase);
        
        Map<String, Object> params = Map.of(
            "nivel", nivel, 
            "topico", topico, 
            "contexto", contexto, 
            "conceito", conceito
        );

        return this.openAiChatClient.prompt(template.render(params))
                .options(ChatOptions.builder().temperature(0.5).build()) 
                .call()
                .content();
    }

    private String chamarAgenteContextualizador(Questao questao, String conceito, String regrasDeEstiloDoBanco) {
        System.out.println("Contextualizando questão para conceito: " + conceito);

        String alternativasFormatadas = questao.getAlternativas().entrySet().stream()
                .map(e -> "[" + e.getKey().toUpperCase() + "] " + e.getValue())
                .collect(Collectors.joining("\n"));

        
        String regrasFinais = regrasDeEstiloDoBanco + """
                
                
                ### REGRAS DE SEGURANÇA (OBRIGATÓRIO) ###
                - As alternativas NÃO devem ser alteradas — apenas o enunciado muda.
                - O conceito testado e a resposta correta devem permanecer exatamente os mesmos.
                """;

        String prompt = """
            Você é um especialista em elaboração de questões de concurso público na área de redes de computadores.

            Sua tarefa é reescrever a questão abaixo em formato contextualizado.

            ### QUESTÃO ORIGINAL ###
            Enunciado: %s
            Alternativas:
            %s
            Resposta correta: %s
            Conceito testado: %s

            ### REGRAS ESTILÍSTICAS ###
            %s

            ### FORMATO DE SAÍDA (siga exatamente, sem texto adicional) ###
            [ENUNCIADO]
            <narrativa + pergunta>
            [/ENUNCIADO]
            [A] <alternativa A original>
            [B] <alternativa B original>
            [C] <alternativa C original>
            [D] <alternativa D original>
            [E] <alternativa E original>
            [RESPOSTA] <letra original>
            [EXPLICACAO]
            <explicação original>
            [/EXPLICACAO]
            """.formatted(
                questao.getEnunciado(),
                alternativasFormatadas,
                questao.getRespostaCorreta().toUpperCase(),
                conceito,
                regrasFinais 
        );

        return this.openAiChatClient.prompt(prompt)
                .options(ChatOptions.builder().temperature(0.4).build())
                .call()
                .content();
    }

    private Questao chamarAgenteJulgador(Questao questao) {

    String alternativasFormatadas = questao.getAlternativas().entrySet().stream()
            .map(e -> "[" + e.getKey().toUpperCase() + "] " + e.getValue())
            .collect(Collectors.joining("\n"));

    String prompt = """
        Você é um avaliador especialista em elaboração de questões de redes de computadores
        no nível de concursos públicos e provas universitárias.

        Sua tarefa é julgar a qualidade da questão abaixo.

        ### QUESTÃO ###
        [ENUNCIADO]
        %s
        [/ENUNCIADO]

        %s

        [RESPOSTA]
        %s
        [/RESPOSTA]

        [EXPLICACAO]
        %s
        [/EXPLICACAO]

        ### CRITÉRIOS DE AVALIAÇÃO ###
        Avalie:

        1. Clareza do enunciado
        2. Existência de apenas uma resposta correta
        3. Ausência de ambiguidade
        4. Coerência técnica
        5. Qualidade dos distratores
        6. Nível adequado de dificuldade

        ### REGRAS ###
        - Escreva um feedback detalhado, apontando pontos fortes e fracos da questão.
        - Dê uma nota de 0 a 10, onde 10 é excelente e 0 é inaceitável.
        - Monte uma nova versão da questão com enunciado e alternativas melhoradas de acordo com o seu feedback.
        - O formato de saída da nova questão deve ser o mesmo da questão original, usando as mesmas tags [ENUNCIADO], [A], [B], [C], [D], [E], [RESPOSTA] e [EXPLICACAO].
        
        """.formatted(
            questao.getEnunciado(),
            alternativasFormatadas,
            questao.getRespostaCorreta().toUpperCase(),
            questao.getExplicacao()
    );

    String resposta = this.anthropicChatClient.prompt(prompt)
            .options(ChatOptions.builder().temperature(0.1).build())
            .call()
            .content();

    List<Questao> questoesMelhoradas = parsearRespostaTags(resposta);
    if (!questoesMelhoradas.isEmpty()) {
        Questao questaoMelhorada = questoesMelhoradas.get(0);
        System.out.println("Julgador melhorou a questão com sucesso.");
        questaoMelhorada.setFeedbackJulgador("Julgador melhorou a questão com sucesso.");
        return questaoMelhorada;
    }

    System.err.println("Julgador não gerou questão parseável — mantendo questão original.");
    questao.setFeedbackJulgador(resposta);
    return questao;

    }

    private AvaliacaoQuestao chamarAgenteAvaliador(Questao questao){

      String prompt = """
        Você é um avaliador especialista em elaboração de questões de redes de computadores
        no nível de concursos públicos e provas universitárias.

        Sua tarefa é analisar a questão abaixo, e tecer um comentário técnico a respeito da questão. 

        ### VOCÊ DEVE IDENTIFICAR ### 
        - Competência que está sendo cobrada
        
        ### VOCÊ DEVE FAZER ### 
        - Uma análise técnica da questão, explicando qual a alternativa correta e justificando porque as outras estão erradas.

        ### QUESTÃO ###
        [ENUNCIADO]
        %s
        [/ENUNCIADO]

        %s

        [RESPOSTA]
        %s
        [/RESPOSTA]


        ### FORMATO DE SAÍDA ### 

        Responda APENAS no seguinte formato JSON:

        {
        "competencia": "Competência principal avaliada",
        "comentarioTecnico": "Comentário técnico detalhado da questão"
        }

        Não adicione texto fora do JSON.

        
        """.formatted(
            questao.getEnunciado(),
            questao.getAlternativas().entrySet().stream()
                .map(e -> "[" + e.getKey().toUpperCase() + "] " + e.getValue())
                .collect(Collectors.joining("\n")),
            questao.getRespostaCorreta().toUpperCase()
        );

        String resposta = this.anthropicChatClient.prompt(prompt)
        .options(ChatOptions.builder().temperature(0.1).build())
        .call()
        .content();

        resposta = resposta
        .replace("```json", "")
        .replace("```", "")
        .trim();

            try {
                return objectMapper.readValue(resposta, AvaliacaoQuestao.class);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao parsear avaliação da questão: " + resposta, e);
            }
    }
}