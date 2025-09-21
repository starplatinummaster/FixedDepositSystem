package com.zeta.DigitalFixedDepositSystem.repository;

import com.zeta.DigitalFixedDepositSystem.model.FixedDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zeta.DigitalFixedDepositSystem.model.enums.FixedDepositStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDeposit, UUID> {
    Optional<FixedDeposit> findByIdAndUserEmail(UUID id, String userEmail);
    List<FixedDeposit> findByUserIdAndStatus(UUID userId, FixedDepositStatus status);
    List<FixedDeposit> findByUserId(UUID userId);
}
