package com.hoomicorp.repository;

import com.hoomicorp.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntertainmentRepository extends JpaRepository<Category, Long> {
}
