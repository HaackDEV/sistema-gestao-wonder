package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.dto.request.ClienteRequest;
import com.haackdev.commercial_management.dto.response.ClienteResponse;
import com.haackdev.commercial_management.entity.Cliente;
import com.haackdev.commercial_management.mapper.ClienteMapper;
import com.haackdev.commercial_management.repository.ClienteRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    // Injeção de dependência via construtor
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }
    
    // Busca todos os clientes cadastrados
    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll().stream().map(clienteMapper::clienteToClienteResponse).collect(Collectors.toList()); // Retorna uma lista de clientes convertidos para DTO
    }

    // Busca um cliente pelo ID
    public ClienteResponse findById(Long id) {
        return clienteRepository.findById(id).map(clienteMapper::clienteToClienteResponse) // Retorna um cliente convertido para DTO
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // Insere um novo cliente
    public ClienteResponse insert(ClienteRequest request) {
        Cliente cliente = clienteMapper.requestToCliente(request); // Converte um ClienteRequest para Cliente
        cliente.setId(null); // Define o ID como null para que o banco de dados gere um novo ID
        cliente = clienteRepository.save(cliente); // Salva o cliente no banco de dados
        return clienteMapper.clienteToClienteResponse(cliente); // Converte um Cliente para ClienteResponse
    }

    // Deleta um cliente pelo ID
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            clienteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível deletar um cliente pois ele possui vínculos no banco de dados.");
        }
    }

    // Atualiza um cliente existente
    public ClienteResponse update(Long id, ClienteRequest request) {
        try {
            Cliente entity = clienteRepository.getReferenceById(id); 
            updateData(entity, request); // Atualiza os dados do cliente
            entity = clienteRepository.save(entity); // Salva o cliente no banco de dados
            return clienteMapper.clienteToClienteResponse(entity); // Converte um Cliente para ClienteResponse
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id); 
        }
    }

    // Metodo auxiliar para atualizar os dados do cliente
    private void updateData(Cliente entity, ClienteRequest request) {
        entity.setRazaoSocial(request.razaoSocial());
        entity.setNomeFantasia(request.nomeFantasia());
        entity.setCnpj(request.cnpj());
        entity.setEnderecoCompleto(request.enderecoCompleto());
        entity.setCidade(request.cidade());
        entity.setEstado(request.estado());
        entity.setTelefoneGeral(request.telefoneGeral());
        entity.setEmailGeral(request.emailGeral());
        entity.setContatoDesenvolvimento(request.contatoDesenvolvimento());
        entity.setContatoCompras(request.contatoCompras());
        entity.setCondicoesPagamento(request.condicoesPagamento());
    }
}
