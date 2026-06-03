package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.dto.request.FornecedorRequest;
import com.haackdev.commercial_management.dto.response.FornecedorResponse;
import com.haackdev.commercial_management.entity.Fornecedor;
import com.haackdev.commercial_management.mapper.FornecedorMapper;
import com.haackdev.commercial_management.repository.FornecedorRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FornecedorService {

    // Injeção de dependência via construtor
    private final FornecedorRepository fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;

    public FornecedorService(FornecedorRepository fornecedorRepository, FornecedorMapper fornecedorMapper) {
        this.fornecedorRepository = fornecedorRepository;
        this.fornecedorMapper = fornecedorMapper;
    }

    // Busca todos os fornecedores cadastrados
    public List<FornecedorResponse> findAll() {
        return fornecedorRepository.findAll().stream().map(fornecedorMapper::fornecedorToFornecedorResponse).toList(); // Retorna uma lista de fornecedores convertido para DTO
    }

    // Busca um fornecedor pelo ID
    public FornecedorResponse findById(Long id) {
        return fornecedorRepository.findById(id).map(fornecedorMapper::fornecedorToFornecedorResponse) // Retorna um fornecedor convertido para DTO
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // Insere um novo fornecedor
    public FornecedorResponse insert(FornecedorRequest request) {
        Fornecedor fornecedor = fornecedorMapper.requestToFornecedor(request); // Converte um FornecedorRequest para Fornecedor
        fornecedor.setId(null); // Define o ID como null para que o banco de dados gere um novo ID
        fornecedor = fornecedorRepository.save(fornecedor); // Salva o fornecedor no banco de dados
        return fornecedorMapper.fornecedorToFornecedorResponse(fornecedor); // Converte um Fornecedor para FornecedorResponse
    }

    // Deleta um fornecedor pelo ID
    public void delete(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            fornecedorRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível deletar um fornecedor pois ele possui vínculos no banco de dados.");
        }
    }

    // Atualiza um fornecedor existente
    public FornecedorResponse update(Long id, FornecedorRequest fornecedor) {
        try {
            Fornecedor entity = fornecedorRepository.getReferenceById(id);
            updateData(entity, fornecedor); // Atualiza os dados do fornecedor
            entity = fornecedorRepository.save(entity); // Salva o fornecedor no banco de dados
            return fornecedorMapper.fornecedorToFornecedorResponse(entity); // Converte um fornecedor para FornecedorResponse
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // Metodo auxiliar para atualizar os dados do fornecedor
    private void updateData(Fornecedor entity, FornecedorRequest novoFornecedor) {
        entity.setNomeFantasia(novoFornecedor.nomeFantasia());
    }
}
