package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.dto.request.ClienteRequest;
import com.haackdev.commercial_management.dto.response.ClienteResponse;
import com.haackdev.commercial_management.entity.Cliente;
import com.haackdev.commercial_management.mapper.ClienteMapper;
import com.haackdev.commercial_management.repository.ClienteRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

/**
 * Testes Unitários da Camada de Serviço para a entidade Cliente.
 * Usa Mockito para isolar a lógica de negócio das dependências de banco de dados.
 */
@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private ClienteMapper mapper;

    @InjectMocks
    private ClienteService service;

    private Long idExistente;
    private Long idInexistente;
    private Cliente cliente;
    private ClienteResponse clienteResponse;
    private ClienteRequest clienteRequest;

    /**
     * Preparação inicial, ocorre ANTES de cada método de teste ser executado.
     * Serve para configurar os cenários comuns de Mock.
     */
    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 2L;

        cliente = new Cliente();
        cliente.setId(idExistente);
        cliente.setRazaoSocial("Razão Social");
        cliente.setNomeFantasia("Cliente Teste");
        cliente.setCnpj("12345678000190");
        cliente.setEmailGeral("email@teste.com");
        cliente.setCidade("Cidade");
        cliente.setEstado("SC");

        clienteResponse = new ClienteResponse(
                idExistente,
                cliente.getRazaoSocial(),
                cliente.getNomeFantasia(),
                cliente.getCnpj(),
                cliente.getEmailGeral(),
                cliente.getCidade(),
                cliente.getEstado()
        );

        clienteRequest = new ClienteRequest(
                cliente.getRazaoSocial(),
                cliente.getNomeFantasia(),
                cliente.getCnpj(),
                "Endereço",
                cliente.getCidade(),
                cliente.getEstado(),
                "4799999999",
                cliente.getEmailGeral(),
                "Contato Dev",
                "Contato Compras",
                "30 dias"
        );
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONSULTA
    // -------------------------------------------------------------

    @Test
    public void findAllDeveRetornarLista() {
        // ARRANGE
        Mockito.when(repository.findAll()).thenReturn(List.of(cliente));
        Mockito.when(mapper.clienteToClienteResponse(cliente)).thenReturn(clienteResponse);

        // ACT
        List<ClienteResponse> result = service.findAll();

        // ASSERT
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(idExistente, result.get(0).id());
    }

    @Test
    public void findByIdDeveRetornarClienteQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(cliente));
        Mockito.when(mapper.clienteToClienteResponse(cliente)).thenReturn(clienteResponse);

        // ACT
        ClienteResponse result = service.findById(idExistente);

        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(idInexistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE INSERÇÃO
    // -------------------------------------------------------------

    @Test
    public void insertDeveSalvarEGerarIdNull() {
        // ARRANGE
        Mockito.when(mapper.requestToCliente(any())).thenReturn(new Cliente());
        Mockito.when(repository.save(any())).thenReturn(cliente);
        Mockito.when(mapper.clienteToClienteResponse(cliente)).thenReturn(clienteResponse);

        // ACT
        ClienteResponse result = service.insert(clienteRequest);

        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
        Mockito.verify(repository, Mockito.times(1)).save(any());
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE EXCLUSÃO
    // -------------------------------------------------------------

    @Test
    public void deleteDeveNaoFazerNadaQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.existsById(idExistente)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(idExistente);

        // ACT & ASSERT
        Assertions.assertDoesNotThrow(() -> service.delete(idExistente));

        Mockito.verify(repository, Mockito.times(1)).deleteById(idExistente);
    }

    @Test
    public void deleteDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.existsById(idInexistente)).thenReturn(false);

        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(idInexistente));
    }

    @Test
    public void deleteDeveLancarDatabaseExceptionQuandoHouverViolacaoDeIntegridade() {
        // ARRANGE
        Mockito.when(repository.existsById(idExistente)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idExistente);

        // ACT & ASSERT
        Assertions.assertThrows(DatabaseException.class, () -> service.delete(idExistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE ATUALIZAÇÃO
    // -------------------------------------------------------------

    @Test
    public void updateDeveRetornarClienteQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.getReferenceById(idExistente)).thenReturn(cliente);
        Mockito.when(repository.save(any())).thenReturn(cliente);
        Mockito.when(mapper.clienteToClienteResponse(cliente)).thenReturn(clienteResponse);

        // ACT
        ClienteResponse result = service.update(idExistente, clienteRequest);

        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
        Mockito.verify(repository).save(cliente);
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);

        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, clienteRequest));
    }
}
