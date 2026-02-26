package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.Cliente;
import com.haackdev.commercial_management.repository.ClienteRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Busca todos os clientes cadastrados
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    // Busca um cliente pelo ID
    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // Insere um novo cliente
    public Cliente insert(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Deleta um cliente pelo ID
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            clienteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível deletar um cliente que possui pedidos ou desenvolvimentos vinculados.");
        }
    }

    // Atualiza um cliente existente
    public Cliente update(Long id, Cliente cliente) {
        try {
            Cliente entity = clienteRepository.getReferenceById(id);
            updateData(entity, cliente);
            return clienteRepository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // Metodo auxiliar para atualizar os dados do cliente
    private void updateData(Cliente entity, Cliente novoCliente) {
        entity.setRazaoSocial(novoCliente.getRazaoSocial());
        entity.setNomeFantasia(novoCliente.getNomeFantasia());
        entity.setCnpj(novoCliente.getCnpj());
        entity.setEnderecoCompleto(novoCliente.getEnderecoCompleto());
        entity.setCidade(novoCliente.getCidade());
        entity.setEstado(novoCliente.getEstado());
        entity.setTelefoneGeral(novoCliente.getTelefoneGeral());
        entity.setEmailGeral(novoCliente.getEmailGeral());
        entity.setContatoDesenvolvimento(novoCliente.getContatoDesenvolvimento());
        entity.setContatoCompras(novoCliente.getContatoCompras());
        entity.setCondicoesPagamento(novoCliente.getCondicoesPagamento());
    }
}
