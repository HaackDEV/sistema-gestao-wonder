package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.Cliente;
import com.haackdev.commercial_management.entity.Desenvolvimento;
import com.haackdev.commercial_management.repository.DesenvolvimentoRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesenvolvimentoService {

    @Autowired
    private DesenvolvimentoRepository desenvolvimentoRepository;

    // Busca todos os desenvolvimentos cadastrados
    public List<Desenvolvimento> findAll() {
        return desenvolvimentoRepository.findAll();
    }

    // Busca um desenvolvimento pelo ID
    public Desenvolvimento findById(Long id) {
        return desenvolvimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // Insere um novo desenvolvimento
    public Desenvolvimento insert(Desenvolvimento desenvolvimento) {
        return desenvolvimentoRepository.save(desenvolvimento);
    }

    // Deleta um desenvolvimento pelo ID
    public void delete(Long id) {
        if (!desenvolvimentoRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            desenvolvimentoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível deletar este desenvolvimento pois ele possui vínculos no banco de dados.");
        }
    }

    // Atualiza um desenvolvimento existente
    public Desenvolvimento update(Long id, Desenvolvimento desenvolvimento) {
        try {
            Desenvolvimento entity = desenvolvimentoRepository.getReferenceById(id);
            updateData(entity, desenvolvimento);
            return desenvolvimentoRepository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // Metodo auxiliar para atualizar os dados do desenvolvimento
    private void updateData(Desenvolvimento entity, Desenvolvimento novoDesenvolvimento) {
        entity.setTipo(novoDesenvolvimento.getTipo());
        entity.setDataSolicitacao(novoDesenvolvimento.getDataSolicitacao());
        entity.setStatus(novoDesenvolvimento.getStatus());
        entity.setMotivoReprovacao(novoDesenvolvimento.getMotivoReprovacao());
        entity.setVirouPedido(novoDesenvolvimento.getVirouPedido());
        entity.setValorConvertido(novoDesenvolvimento.getValorConvertido());
        entity.setDataConversao(novoDesenvolvimento.getDataConversao());
    }
}
