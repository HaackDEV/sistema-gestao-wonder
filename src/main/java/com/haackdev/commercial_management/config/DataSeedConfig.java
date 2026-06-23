package com.haackdev.commercial_management.config;

import com.haackdev.commercial_management.entity.*;
import com.haackdev.commercial_management.entity.enums.StatusDesenvolvimento;
import com.haackdev.commercial_management.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@Configuration
@org.springframework.context.annotation.Profile("!test")
public class DataSeedConfig implements CommandLineRunner {

    private final FornecedorRepository fornecedorRepository;

    private final ProdutoRepository produtoRepository;

    private final ClienteRepository clienteRepository;

    private final DesenvolvimentoRepository desenvolvimentoRepository;

    private final PedidoRepository pedidoRepository;

    private final ItemPedidoRepository itemPedidoRepository;

    public DataSeedConfig(FornecedorRepository fornecedorRepository, ProdutoRepository produtoRepository, ClienteRepository clienteRepository, DesenvolvimentoRepository desenvolvimentoRepository, PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.desenvolvimentoRepository = desenvolvimentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // --- FORNECEDORES ---
        Fornecedor f1 = new Fornecedor(null, "Alpargatas S/A");
        Fornecedor f2 = new Fornecedor(null, "Nike Brasil");
        fornecedorRepository.saveAll(Arrays.asList(f1, f2));

        // --- CLIENTES ---
        Cliente c1 = new Cliente(null, "Loja de Calcados Silva LTDA", "Silva Sports", "12345678000199", "Rua das Flores, 123", "São Paulo", "SP", "11999999999", "contato@silvasports.com", "João Silva", "Maria Silva", "30/60/90");
        Cliente c2 = new Cliente(null, "Varejo Fashion S/A", "Fashion Wear", "98765432000188", "Av. Paulista, 1500", "São Paulo", "SP", "11888888888", "compras@fashionwear.com", "Ana Rocha", "Pedro Rocha", "A vista");
        clienteRepository.saveAll(Arrays.asList(c1, c2));

        // --- PRODUTOS ---
        Produto p1 = new Produto(null, f1, "HA-001", "Havaianas Tradicional Azul", "Chinelo", "Azul", "Borracha", new BigDecimal("15.00"), new BigDecimal("29.90"));
        Produto p2 = new Produto(null, f1, "HA-002", "Havaianas Slim Rosa", "Chinelo", "Rosa", "Borracha", new BigDecimal("18.00"), new BigDecimal("34.90"));
        Produto p3 = new Produto(null, f2, "NK-500", "Tênis Nike Air Max", "Tênis", "Preto", "Couro/Sintético", new BigDecimal("250.00"), new BigDecimal("499.90"));
        produtoRepository.saveAll(Arrays.asList(p1, p2, p3));

        // --- PEDIDOS ---
        Pedido ped1 = new Pedido(null, c1, LocalDate.now(), new BigDecimal("1000.00"), "Boleto", "3x", null);
        Pedido ped2 = new Pedido(null, c2, LocalDate.now().minusDays(2), new BigDecimal("500.00"), "Cartão", "1x", null);
        pedidoRepository.saveAll(Arrays.asList(ped1, ped2));

        // --- ITENS DE PEDIDO ---
        ItemPedido ip1 = new ItemPedido(null, ped1, p1, 10, p1.getValorVenda());
        ItemPedido ip2 = new ItemPedido(null, ped1, p3, 2, p3.getValorVenda());
        ItemPedido ip3 = new ItemPedido(null, ped2, p2, 5, p2.getValorVenda());
        itemPedidoRepository.saveAll(Arrays.asList(ip1, ip2, ip3));

        // --- DESENVOLVIMENTOS ---
        Desenvolvimento d1 = new Desenvolvimento(null, c1, p1, "Amostra", LocalDate.now().minusMonths(1), StatusDesenvolvimento.APROVADO, null, true, new BigDecimal("29.90"), LocalDate.now());
        Desenvolvimento d2 = new Desenvolvimento(null, c2, p3, "Pilotagem", LocalDate.now().minusWeeks(2), StatusDesenvolvimento.EM_ANALISE, null, false, null, null);
        desenvolvimentoRepository.saveAll(Arrays.asList(d1, d2));

        System.out.println("=========================================================");
        System.out.println("SUCESSO: Teste de Carga Inicial executado!");
        System.out.println("Dados guardados na base de dados com sucesso.");
        System.out.println("Fornecedores, Clientes, Produtos, Pedidos e Desenvolvimentos carregados.");
        System.out.println("=========================================================");
    }
}
