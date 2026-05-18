package com.Projeto.GeradorDeQuestoes.services.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.Projeto.GeradorDeQuestoes.entities.PdfBinarioEntity;
import com.Projeto.GeradorDeQuestoes.repositories.PdfBinarioRepository;
import com.Projeto.GeradorDeQuestoes.services.PdfBinarioService;


@Service
public class PdfBinarioEntityServiceImpl implements PdfBinarioService {

    private final PdfBinarioRepository pdfBinarioRepository;

    public PdfBinarioEntityServiceImpl(PdfBinarioRepository pdfBinarioRepository) {
        this.pdfBinarioRepository = pdfBinarioRepository;
    }


    @Override
    public PdfBinarioEntity salvarOuRecuperar(byte[] bytes, String filename) {

        Optional<PdfBinarioEntity> existente = pdfBinarioRepository.findByNomeOriginal(filename);
        
        if (existente.isPresent()) {
            return existente.get(); 
        }

        PdfBinarioEntity novoPdf = new PdfBinarioEntity();
        novoPdf.setNomeOriginal(filename);
        novoPdf.setArquivoBinario(bytes);
        novoPdf.setDataUpload(LocalDateTime.now());
        
        return pdfBinarioRepository.save(novoPdf);
        
        
    }

    @Override
    public PdfBinarioEntity buscarPorId(java.util.UUID id) {
        return pdfBinarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Arquivo físico não encontrado com o ID: " + id));
    }
    
}
