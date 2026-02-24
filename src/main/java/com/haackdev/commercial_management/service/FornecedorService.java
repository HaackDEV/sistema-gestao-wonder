package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.Fornecedor;
import com.haackdev.commercial_management.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    // Busca todos os fornecedores cadastrados
    public List<Fornecedor> findAll() {
        return fornecedorRepository.findAll();
    }

    // Busca um fornecedor pelo ID
    public Fornecedor findById(Long id) {
        return fornecedorRepository.findById(id).orElse(null);
    }

    // Insere um novo fornecedor
    public Fornecedor insert(Fornecedor fornecedor) {
        return fornecedorRepository.save(fornecedor);
    }

    // Deleta um fornecedor pelo ID
    public void delete(Long id) {
        fornecedorRepository.deleteById(id);
    }

    // Atualiza um fornecedor existente
    public Fornecedor update(Long id, Fornecedor fornecedor) {
        Fornecedor entity = fornecedorRepository.getReferenceById(id);
        updateData(entity, fornecedor);
        return fornecedorRepository.save(entity);
    }

    // Metodo auxiliar para atualizar os dados do fornecedor
    private void updateData(Fornecedor entity, Fornecedor novoFornecedor) {
        entity.setNomeFantasia(novoFornecedor.getNomeFantasia());
    }
}
