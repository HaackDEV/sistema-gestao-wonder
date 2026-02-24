package com.haackdev.commercial_management.repository;

import com.haackdev.commercial_management.entity.Desenvolvimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesenvolvimentoRepository extends JpaRepository<Desenvolvimento, Long> {
}
