package com.traveldiary.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.traveldiary.be.entity.Faq;

public interface FaqRepository extends JpaRepository<Faq, Integer> {
}
