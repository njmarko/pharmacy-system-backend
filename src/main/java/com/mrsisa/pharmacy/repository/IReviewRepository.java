package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {
}
