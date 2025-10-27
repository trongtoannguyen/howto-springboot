package com.sample.payment.repository;

import com.sample.payment.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByWalletNumberAndCvv(Long walletNumber, Integer cvv);

    Optional<Wallet> findByWalletNumber(Long walletNumber);
}
