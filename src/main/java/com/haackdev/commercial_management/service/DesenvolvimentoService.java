package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.dto.request.DesenvolvimentoRequest;
import com.haackdev.commercial_management.dto.response.DesenvolvimentoResponse;
import com.haackdev.commercial_management.entity.*;
import com.haackdev.commercial_management.entity.enums.StatusDesenvolvimento;
import com.haackdev.commercial_management.mapper.DesenvolvimentoMapper;
import com.haackdev.commercial_management.repository.ClienteRepository;
import com.haackdev.commercial_management.repository.DesenvolvimentoRepository;
import com.haackdev.commercial_management.repository.ProdutoRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DesenvolvimentoService {

    private final DesenvolvimentoRepository desenvolvimentoRepository;
    private final DesenvolvimentoMapper desenvolvimentoMapper;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoService pedidoService;

    public DesenvolvimentoService(DesenvolvimentoRepository desenvolvimentoRepository, DesenvolvimentoMapper desenvolvimentoMapper, ClienteRepository clienteRepository, ProdutoRepository produtoRepository, PedidoService pedidoService) {
        this.desenvolvimentoRepository = desenvolvimentoRepository;
        this.desenvolvimentoMapper = desenvolvimentoMapper;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoService = pedidoService;
    }

    // Busca todos os desenvolvimentos cadastrados
    public List<DesenvolvimentoResponse> findAll() {
        return desenvolvimentoRepository.findAll().stream().map(desenvolvimentoMapper::DesenvolvimentoToDesenvolvimentoResponse).toList();
    }

    // Busca um desenvolvimento pelo ID
    public DesenvolvimentoResponse findById(Long id) {
        return desenvolvimentoRepository.findById(id).map(desenvolvimentoMapper::DesenvolvimentoToDesenvolvimentoResponse) // Retorna um desenvolvimento convertido para DTO
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // Insere um novo desenvolvimento
    public DesenvolvimentoResponse insert(DesenvolvimentoRequest request) {
        Desenvolvimento desenvolvimento = desenvolvimentoMapper.requestToDesenvolvimento(request); // Converte um DesenvolvimentoRequest para Desenvolvimento
        desenvolvimento.setId(null); // Define o ID como null para que o banco de dados gere um novo ID

        if (!clienteRepository.existsById(request.clienteId())) {
            throw new ResourceNotFoundException(request.clienteId());
        }

        if (!produtoRepository.existsById(request.produtoId())) {
            throw new ResourceNotFoundException(request.produtoId());
        }

        Cliente cliente = clienteRepository.getReferenceById(request.clienteId());
        Produto produto = produtoRepository.getReferenceById(request.produtoId());

        desenvolvimento.setCliente(cliente);
        desenvolvimento.setProduto(produto);

        desenvolvimento = desenvolvimentoRepository.save(desenvolvimento); // Salva o Desenvolvimento no banco de dados
        return desenvolvimentoMapper.DesenvolvimentoToDesenvolvimentoResponse(desenvolvimento); // Converte um Desenvolvimento para DesenvolvimentoResponse
    }

    // Deleta um desenvolvimento pelo ID
    public void delete(Long id) {
        if (!desenvolvimentoRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            desenvolvimentoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    "Não é possível deletar este desenvolvimento pois ele possui vínculos no banco de dados.");
        }
    }

    // Atualiza um desenvolvimento existente
    public DesenvolvimentoResponse update(Long id, DesenvolvimentoRequest desenvolvimentoRequest) {
        try {
            Desenvolvimento entity = desenvolvimentoRepository.getReferenceById(id);
            updateData(entity, desenvolvimentoRequest);

            if (!clienteRepository.existsById(desenvolvimentoRequest.clienteId())) {
                throw new ResourceNotFoundException(desenvolvimentoRequest.clienteId());
            }

            if (!produtoRepository.existsById(desenvolvimentoRequest.produtoId())) {
                throw new ResourceNotFoundException(desenvolvimentoRequest.produtoId());
            }

            Cliente cliente = clienteRepository.getReferenceById(desenvolvimentoRequest.clienteId());
            Produto produto = produtoRepository.getReferenceById(desenvolvimentoRequest.produtoId());

            entity.setCliente(cliente);
            entity.setProduto(produto);

            entity = desenvolvimentoRepository.save(entity);
            return desenvolvimentoMapper.DesenvolvimentoToDesenvolvimentoResponse(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // Metodo auxiliar para atualizar os dados do desenvolvimento
    private void updateData(Desenvolvimento entity, DesenvolvimentoRequest novoDesenvolvimento) {
        entity.setTipo(novoDesenvolvimento.tipo());
        entity.setDataSolicitacao(novoDesenvolvimento.dataSolicitacao());
        entity.setStatus(novoDesenvolvimento.status());
        entity.setMotivoReprovacao(novoDesenvolvimento.motivoReprovacao());
        entity.setVirouPedido(novoDesenvolvimento.virouPedido());
        entity.setValorConvertido(novoDesenvolvimento.valorConvertido());
        entity.setDataConversao(novoDesenvolvimento.dataConversao());
    }

    @Transactional
    public Pedido converterEmPedido(Long id) {
        Desenvolvimento desenvolvimento = desenvolvimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (desenvolvimento.getVirouPedido() == true) {
            throw new DatabaseException("Este desenvolvimento já foi convertido em pedido.");
        }

        if (desenvolvimento.getStatus() != StatusDesenvolvimento.APROVADO) {
            throw new DatabaseException("Apenas desenvolvimentos aprovados podem ser convertidos em pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(desenvolvimento.getCliente());
        pedido.setDataPedido(LocalDate.now());

        ItemPedido item = new ItemPedido();
        item.setProduto(desenvolvimento.getProduto());
        item.setQuantidade(1);
        
        if (desenvolvimento.getValorConvertido() == null) {
            throw new DatabaseException("O desenvolvimento não possui um valor de conversão definido. Atualize o desenvolvimento antes de faturá-lo.");
        }
        
        item.setValorUnitario(desenvolvimento.getValorConvertido());
        pedido.addItem(item);

        pedido = pedidoService.insert(pedido);

        desenvolvimento.setVirouPedido(true);
        desenvolvimento.setDataConversao(LocalDate.now());
        desenvolvimentoRepository.save(desenvolvimento);

        return pedido;
    }
}
