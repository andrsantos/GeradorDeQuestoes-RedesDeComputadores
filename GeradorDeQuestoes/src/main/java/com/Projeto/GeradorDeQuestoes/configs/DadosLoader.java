// package com.Projeto.GeradorDeQuestoes.configs;

// import org.springframework.ai.document.Document;
// import org.springframework.ai.vectorstore.VectorStore;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;
// import java.util.List;
// import java.util.Map;

// @Component
// public class DadosLoader implements CommandLineRunner {

//     private final VectorStore vectorStore;

//     public DadosLoader(VectorStore vectorStore) {
//         this.vectorStore = vectorStore;
//     }

//     @Override
//     public void run(String... args) throws Exception {
//         System.out.println("Verificando se há dados no Vector Store...");

//         List<Document> documents = List.of(
//             new Document("O Modelo OSI (Open Systems Interconnection) é um modelo conceitual que " +
//                          "padroniza as funções de um sistema de telecomunicação ou computação em " +
//                          "sete camadas de abstração. A Camada 1 é a Física, responsável pelos " +
//                          "sinais elétricos, bits.", 
//                          Map.of("topico", "OSI")), // Metadata
                         
//             new Document("A Camada 7 do Modelo OSI é a Camada de Aplicação. Ela fornece " +
//                          "interfaces para os aplicativos de software se comunicarem com a rede. " +
//                          "Protocolos comuns incluem HTTP, FTP, SMTP e DNS.", 
//                          Map.of("topico", "OSI")),

//             new Document("TCP (Transmission Control Protocol) é um protocolo da camada de " +
//                          "transporte (Camada 4 do OSI) que garante a entrega confiável e " +
//                          "ordenada de pacotes. Ele estabelece uma conexão (handshake) " +
//                          "antes de enviar dados.", 
//                          Map.of("topico", "TCP/IP")),

//             new Document("IP (Internet Protocol) é o principal protocolo da camada de rede " +
//                          "(Camada 3 do OSI). Sua função é o endereçamento e roteamento de " +
//                          "pacotes. O IPv4 usa endereços de 32 bits, enquanto o IPv6 usa 128 bits.", 
//                          Map.of("topico", "TCP/IP"))
//         );

//         System.out.println("Adicionando " + documents.size() + " documentos de exemplo ao PGVector...");
//         vectorStore.add(documents);
//         System.out.println("Documentos adicionados com sucesso!");
//     }
// }