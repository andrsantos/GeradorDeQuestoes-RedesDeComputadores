package com.Projeto.GeradorDeQuestoes.services;

import java.util.UUID;

import com.Projeto.GeradorDeQuestoes.entities.PdfBinarioEntity;

public interface PdfBinarioService {

    PdfBinarioEntity salvarOuRecuperar(byte[] bytes, String filename);
    PdfBinarioEntity buscarPorId(UUID id);
    
}
