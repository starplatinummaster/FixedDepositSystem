package com.zeta.DigitalFixedDepositSystem.repository;

import com.zeta.DigitalFixedDepositSystem.model.FDBreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FDBreakRepository extends JpaRepository<FDBreak, UUID> { }
