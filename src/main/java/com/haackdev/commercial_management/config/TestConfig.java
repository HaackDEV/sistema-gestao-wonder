package com.haackdev.commercial_management.config;

import com.haackdev.commercial_management.entity.Fornecedor;
import com.haackdev.commercial_management.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class TestConfig implements CommandLineRunner {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Override
    public void run(String... args) throws Exception {

        Fornecedor f1 = new Fornecedor(null, "Alpargatas S/A");
        Fornecedor f2 = new Fornecedor(null, "Nike Brasil");
        Fornecedor f3 = new Fornecedor(null, "Adidas Oficial");
        Fornecedor f4 = new Fornecedor(null, "Puma Brasil");

        fornecedorRepository.saveAll(Arrays.asList(f1, f2, f3, f4));

        System.out.println("=========================================================");
        System.out.println("SUCESSO: Teste de Carga Inicial executado!");
        System.out.println("Fornecedores guardados na base de dados com sucesso.");
        System.out.println("=========================================================");
    }
}
