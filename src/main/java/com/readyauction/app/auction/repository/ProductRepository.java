package com.readyauction.app.auction.repository;

import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 여기에 추가적인 쿼리 메서드를 정의할 수 있습니다.

    Optional<Product> findByIdAndAuctionStatusNot(Long id, AuctionStatus auctionStatus);
    Optional<Product> findById(Long id);

    // 이름을 기준으로 제품을 검색하는 쿼리 메서드
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByEndTimeBetween(Timestamp start, Timestamp end);
    // 이름으로 검색하면서 auctionStatus가 END가 아닌 상품만 가져오기
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.auctionStatus <> :status")
    Page<Product> searchByNameAndStatus(@Param("name") String name, @Param("status") AuctionStatus status, Pageable pageable);

    // 모든 상품 중에서 auctionStatus가 END가 아닌 상품만 가져오기
    @Query("SELECT p FROM Product p WHERE p.auctionStatus <> :status")
    Page<Product> findActiveProducts(@Param("status") AuctionStatus status, Pageable pageable);
}
