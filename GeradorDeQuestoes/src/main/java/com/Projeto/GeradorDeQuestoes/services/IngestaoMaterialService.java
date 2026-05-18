package com.Projeto.GeradorDeQuestoes.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.Projeto.GeradorDeQuestoes.dto.QuestaoDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;


@Service
public class IngestaoMaterialService {

    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;
    private final ChatClient anthropicChatClient;

    public IngestaoMaterialService(VectorStore vectorStore, ChatClient anthropicChatClient) {
        this.vectorStore = vectorStore;
        this.anthropicChatClient = anthropicChatClient;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }



    public void importarCapituloLivroDificil(Resource pdfResource, String topico, String fonte) {
        processarRAG(pdfResource, topico, fonte, "universitario_avancado");
    }

    public void importarCapituloLivroMedio(Resource pdfResource, String topico, String fonte) {
        processarRAG(pdfResource, topico, fonte, "universitario_intermediario");
    }

    public void importarCapituloLivroFacil(Resource pdfResource, String topico, String fonte) {
        processarRAG(pdfResource, topico, fonte, "universitario_iniciante");
    }

    private void processarRAG(Resource pdfResource, String topico, String fonte, String nivel) {

        TikaDocumentReader pdfReader = new TikaDocumentReader(pdfResource);
        
        List<Document> documentosBrutos = pdfReader.get();
        
        System.out.println("Documentos lidos pelo Tika: " + documentosBrutos.size());
        if (!documentosBrutos.isEmpty()) {
            String conteudo = documentosBrutos.get(0).getText();
            System.out.println("Tamanho do texto extraído: " + (conteudo != null ? conteudo.length() : "NULO"));
            System.out.println("Início do texto: " + (conteudo != null && conteudo.length() > 100 
                ? conteudo.substring(0, 100) : conteudo));
        }
        
        TokenTextSplitter splitter = new TokenTextSplitter(1500, 400, 10, 5000, true);
        
        List<Document> chunks = splitter.apply(documentosBrutos);
        
        chunks.forEach(chunk -> {
            chunk.getMetadata().put("topico", topico);
            chunk.getMetadata().put("fonte", fonte);
            chunk.getMetadata().put("nivel_material", nivel);
        });
        
        this.vectorStore.accept(chunks);
        System.out.println("Sucesso: " + chunks.size() + " fragmentos [" + nivel + "] importados.");
        
    }


    public List<QuestaoDTO> processarPdfParaQuestoes(File pdfFile) {
        List<String> jsonsBrutos = extrairTextoDePdf(pdfFile);
        System.out.println("JSONs brutos extraídos: " + jsonsBrutos.toString());
        List<QuestaoDTO> questoesExtraidas = filtrarQuestoesValidas(jsonsBrutos);
        List<QuestaoDTO> questoesRevisadas = chamarAgenteRevisor(questoesExtraidas);
        return questoesRevisadas;
    }

    public List<QuestaoDTO> enriquecerQuestoes(List<QuestaoDTO> questoes) {
        return questoes;
    }

    public List<QuestaoDTO> chamarAgenteRevisor(List<QuestaoDTO> questoes) {
        if (questoes == null || questoes.isEmpty()) return questoes;

        List<QuestaoDTO> todasRevisadas = new ArrayList<>();
        int tamanhoLote = 3;

        for (int i = 0; i < questoes.size(); i += tamanhoLote) {
            int fim = Math.min(i + tamanhoLote, questoes.size());
            List<QuestaoDTO> loteAtual = questoes.subList(i, fim);
            
            System.out.println("Agente Revisor: Processando lote " + (i/tamanhoLote + 1));
            
            try {
                String jsonLote = objectMapper.writeValueAsString(loteAtual);
                
                String instrucao = """
                    Você é um Agente Pedagógico Sênior especializado em Redes de Computadores.
                    Sua missão é ENRIQUECER e REFORMAR o lote de questões JSON abaixo.

                    REGRAS MANDATÓRIAS DE ESTRUTURA:
                    1. GABARITO E NÍVEL: Preencha se estiver vazio. No campo 'nivel', use EXCLUSIVAMENTE: UNIVERSITARIO_INICIANTE, UNIVERSITARIO_INTERMEDIARIO ou UNIVERSITARIO_AVANCADO.
                    2. QUESTÃO INSPIRADA: O campo 'questaoInspirada' NÃO pode ser uma String. Ele deve ser um OBJETO JSON completo com a mesma estrutura (id, enunciado, alternativas, gabarito).
                       - Use a lógica da questão original, mas mude o cenário, valores e marcas citadas.
                       - No campo 'id' da questaoInspirada, use o prefixo 'INS-' seguido do ID original.
                    
                    EXEMPLO DO FORMATO PARA CADA QUESTÃO:
                    {
                      "id": "Q123",
                      "enunciado": "...",
                      "alternativas": {"A": "...", ...},
                      "gabarito": "A",
                      "questaoInspirada": {
                         "id": "INS-Q123",
                         "enunciado": "Nova pergunta baseada na original...",
                         "alternativas": {"A": "nova op A", ...},
                         "respostaCorreta": "B"
                      }
                    }

                    Preenche também os seguintes campos da questão inspirada: explicacao, conceito, competencia, comentarioTecnico, topico e nivel.
                    Para o campo tópico, preenche SEMPRE com 'Redes de Computadores e a Internet'.
                    

                    REGRAS DE FORMATAÇÃO:
                    - Retorne APENAS o array JSON [].
                    - NUNCA envie texto livre ou explicações fora do JSON.
                    - Toda string deve estar entre aspas duplas.
                    """;

                String respostaIA = this.anthropicChatClient.prompt(instrucao + "\n\n LOTE:\n" + jsonLote)
                        .options(ChatOptions.builder().temperature(0.0).maxTokens(4000).build())
                        .call().content();

                String cleanJson = respostaIA.replaceAll("(?s)```json\\s*|```", "").trim();
                cleanJson = garantirFechamentoJson(cleanJson);

                List<QuestaoDTO> loteRevisado = objectMapper.readValue(cleanJson, 
                        new com.fasterxml.jackson.core.type.TypeReference<List<QuestaoDTO>>() {});
                
                todasRevisadas.addAll(loteRevisado);
                
            } catch (Exception e) {
                System.err.println("Erro crítico no lote " + i + ": " + e.getMessage());
                todasRevisadas.addAll(loteAtual);
            }
        }
        return todasRevisadas;
    }

    private String garantirFechamentoJson(String json) {
        long abertos = json.chars().filter(ch -> ch == '{').count();
        long fechados = json.chars().filter(ch -> ch == '}').count();
        StringBuilder sb = new StringBuilder(json);
        while (fechados < abertos) {
            sb.append("}");
            fechados++;
        }
        if (!json.trim().endsWith("]")) sb.append("]");
        return sb.toString();
    }

    private String extrairTextoPagina(PDDocument document, int numeroPagina) throws Exception {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(numeroPagina);
        stripper.setEndPage(numeroPagina);
        String textoNativo = stripper.getText(document);

        if (isPaginaComTexto(textoNativo)) {
            System.out.println("Página " + numeroPagina + ": texto nativo extraído.");
            return textoNativo;
        }

        System.out.println("Página " + numeroPagina + ": sem texto nativo, usando OCR...");
        PDFRenderer renderer = new PDFRenderer(document);
        BufferedImage imagem = renderer.renderImageWithDPI(numeroPagina - 1, 300);

        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("por");

        String textoOcr = tesseract.doOCR(imagem);
        imagem.flush();
        return textoOcr;
    }

    private boolean isPaginaComTexto(String texto) {
        if (texto == null || texto.isBlank()) return false;

        long caracteresAlfabeticos = texto.chars()
                .filter(Character::isLetter)
                .count();

        return caracteresAlfabeticos > 50;
    }


    public List<String> extrairTextoDePdf(File pdfFile) {
        List<String> resultadosJson = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            int totalPaginas = document.getNumberOfPages();

            String textoGabarito = extrairTextoPagina(document, totalPaginas);
            System.out.println("=== GABARITO EXTRAÍDO ===\n" + textoGabarito);
            
            for (int i = 1; i < totalPaginas; i++) {
                String textoPagina = extrairTextoPagina(document, i);
                if (!textoPagina.isBlank()) {
                    System.out.println("Processando página " + i + "/" + (totalPaginas - 1));
                    resultadosJson.add(IAQuestaoParser(textoPagina, textoGabarito));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Falha no pipeline de extração: " + e.getMessage(), e);
        }
        return resultadosJson;
    }


   

    public String IAQuestaoParser(String textoPagina, String textoGabarito) {

        String instrucao = """
            OBJETIVO: Você é um extrator de dados JSON estrito. Converta o texto abaixo em um array JSON.

            ESTRUTURA OBRIGATÓRIA (RESPEITE OS NOMES DOS CAMPOS):
            [
            {
                "id": "código original da questão (ex: Q3976030)",
                "enunciado": "texto completo e limpo do enunciado",
                "alternativas": {
                "A": "texto da alternativa A",
                "B": "texto da alternativa B",
                "C": "texto da alternativa C",
                "D": "texto da alternativa D",
                "E": "texto da alternativa E"
                },
                "gabarito": "letra correta (ex: A)"
            }
            ]

            REGRAS CRÍTICAS:
            1. ASPAS: Use EXCLUSIVAMENTE aspas duplas (") em TODO o JSON — chaves E valores.
            NUNCA use aspas simples, barras invertidas ou escapes desnecessários.
            Se o texto original tiver aspas, substitua por aspas simples (') internamente.
            Se o texto original tiver barras invertidas antes de aspas, remova-as.

            2. VALORES SEMPRE STRING EM UMA ÚNICA LINHA: Todo valor deve ser string entre aspas duplas.
            ERRADO: "A": I        CORRETO: "A": "I"
            ERRADO: "A": I e II   CORRETO: "A": "I e II"

            3. ID DA QUESTÃO: O ID no formato Q seguido de números (ex: Q3976030) pode aparecer
            antes OU depois do enunciado. Procure em todo o bloco da questão.
            PRESERVE o ID COMPLETO — nunca trunce ou abrevie (ex: Q3973813, não Q3813).
            Se não encontrar nenhum ID, use: "id": ""

            4. GABARITO: O CONTEXTO DE APOIO contém gabaritos no formato "N: LETRA"
            onde N é o número sequencial da questão na prova.
            Se não encontrar, use: "gabarito": ""
            Questões com gabarito vazio são ACEITAS — não as omita por isso.

            5. INTEGRIDADE ABSOLUTA: Cada questão tem seu próprio enunciado e suas próprias
            alternativas. NUNCA misture partes de questões diferentes.
            O enunciado termina onde começam as alternativas (A, B, C...).
            As alternativas de uma questão terminam onde começa o enunciado da próxima.

            6. ALTERNATIVAS AUSENTES: Se a questão tiver menos de 4 alternativas com texto,
            OMITA a questão inteira.

            7. QUESTÕES COM IMAGEM: OMITA questões que referenciem figura, imagem ou diagrama
            externo necessário para responder (ex: "Com base na figura", "Observe o diagrama",
            "Com base nessa mensagem" quando a mensagem não estiver no texto).

            8. LIMPEZA: Remova numeração de página, cabeçalhos, rodapés e metadados da banca
            (ex: "Ano: 2026  Banca: CESPE  Órgão: ...").

            9. RESPOSTA VAZIA: Se não houver questões válidas, retorne exatamente: []

            10. SEM TEXTO EXTRA: Retorne APENAS o array JSON válido, sem explicações ou markdown.

            11. JSON VÁLIDO: Certifique-se de que todos os objetos e arrays estão fechados.
                Não deixe nenhum campo incompleto — se não conseguir extrair um campo completo,
                OMITA a questão inteira em vez de deixar o JSON mal formado.
            """;

        String promptCompleto = instrucao +
                "\n\nCONTEXTO DE APOIO (GABARITOS NO FORMATO 'N: LETRA'):\n" + textoGabarito +
                "\n\nTEXTO PARA EXTRAÇÃO:\n" + textoPagina;

        return this.anthropicChatClient.prompt(promptCompleto)
                .options(ChatOptions.builder()
                        .temperature(0.0)
                        .maxTokens(4096)
                        .build())
                .call().content();
    }

    public List<QuestaoDTO> filtrarQuestoesValidas(List<String> paginasJson) {
        Set<String> idsVistos = new HashSet<>();
        Set<String> enunciadosVistos = new HashSet<>();

        return paginasJson.stream()
            .flatMap(json -> {
                try {
                    String cleanJson = json.replaceAll("(?s)```json\\s*|```", "").trim();
                    cleanJson = cleanJson.replace("\\\"", "'");

                    JsonNode root;
                    try {
                        root = objectMapper.readTree(cleanJson);
                    } catch (Exception e) {
                        System.err.println("JSON malformado — tentando recuperação parcial: " + e.getMessage());
                        root = tentarRecuperarJson(cleanJson);
                        if (root == null) return Stream.empty();
                    }

                    List<QuestaoDTO> lista = new ArrayList<>();
                    if (root.isArray()) {
                        for (JsonNode node : root) {
                            try {
                                QuestaoDTO q = objectMapper.treeToValue(node, QuestaoDTO.class);
                                if (!isQuestaoCompleta(q)) continue;

                                String chaveEnunciado = q.getEnunciado().substring(
                                        0, Math.min(60, q.getEnunciado().length()));
                                String chaveId = q.getId() != null && !q.getId().isBlank()
                                        ? q.getId() : null;

                                boolean idDuplicado = chaveId != null && !idsVistos.add(chaveId);
                                boolean enunciadoDuplicado = !enunciadosVistos.add(chaveEnunciado);

                                if (!idDuplicado && !enunciadoDuplicado) {
                                    lista.add(q);
                                } else {
                                    System.err.println("Duplicata ignorada: " + chaveEnunciado.substring(0, 30) + "...");
                                }

                            } catch (Exception e) {
                                System.err.println("Questão ignorada: " + e.getMessage());
                            }
                        }
                    }
                    return lista.stream();

                } catch (Exception e) {
                    System.err.println("Falha ao processar página: " + e.getMessage());
                    return Stream.empty();
                }
            })
            .collect(Collectors.toList());
    }

    private boolean isQuestaoCompleta(QuestaoDTO q) {
        if (q.getEnunciado() == null || q.getEnunciado().isBlank()
                || q.getEnunciado().length() < 20) return false;

        if (q.getAlternativas() == null) return false;

        long alternativasValidas = q.getAlternativas().entrySet().stream()
                .filter(e -> {
                    String chave = e.getKey();
                    String valor = e.getValue();
                    return chave != null
                            && chave.matches("[A-Ea-e]")
                            && valor != null
                            && !valor.isBlank()
                            && !valor.contains("__invalid__");
                })
                .count();

        return alternativasValidas >= 4;
    }

    private JsonNode tentarRecuperarJson(String jsonMalformado) {
        try {
            List<String> objetosValidos = new ArrayList<>();

            int profundidade = 0;
            int inicio = -1;

            for (int i = 0; i < jsonMalformado.length(); i++) {
                char c = jsonMalformado.charAt(i);
                if (c == '{') {
                    if (profundidade == 0) inicio = i;
                    profundidade++;
                } else if (c == '}') {
                    profundidade--;
                    if (profundidade == 0 && inicio != -1) {
                        String objeto = jsonMalformado.substring(inicio, i + 1);
                        try {
                            JsonNode node = objectMapper.readTree(objeto);
                            JsonNode enunciado = node.get("enunciado");
                            if (enunciado != null && !enunciado.asText().isBlank()
                                    && enunciado.asText().length() > 20) {
                                objetosValidos.add(objeto);
                            }
                        } catch (Exception ignored) {
                        }
                        inicio = -1;
                    }
                }
            }

            if (objetosValidos.isEmpty()) return null;

            String arrayRecuperado = "[" + String.join(",", objetosValidos) + "]";
            return objectMapper.readTree(arrayRecuperado);

        } catch (Exception e) {
            System.err.println("Recuperação parcial falhou: " + e.getMessage());
            return null;
        }
    }


}