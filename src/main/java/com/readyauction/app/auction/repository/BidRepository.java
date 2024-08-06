package com.readyauction.app.auction.repository;

import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    // 여기에 추가적인 쿼리 메서드를 정의할 수 있습니다.

    Optional<Bid> findByMemberIdAndProduct(Long memberId, Product product);
}
