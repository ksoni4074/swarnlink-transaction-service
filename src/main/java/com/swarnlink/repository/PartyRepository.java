package com.swarnlink.repository;

import com.swarnlink.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {
    List<Party> findByUserId(Long userId);

    Optional<Party> findByIdAndUserId(Long aLong, Long userId);
}