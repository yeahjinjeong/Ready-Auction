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

    /** 지영 - 마이페이지 경매 등록 내역 조회 시 필요 **/
    List<Product> findByMemberIdAndAuctionStatusIn(Long memberId, List<AuctionStatus> start);
    List<Product> findByIdIn(List<Long> productIds);
    List<Product> findByMemberIdAndAuctionStatusAndIdNotIn(Long memberId, AuctionStatus auctionStatus, List<Long> productIdsWithBids);

    // 예진 작업 시작
    @Query("""
    select p.images
    from Product p
    where p.id = :productId
    """)
    Optional<List<String>> findImagesById(Long productId);

    // 특정 이미지가 포함된 제품을 찾아 그 이미지를 반환하는 메서드
    @Query("SELECT img FROM Product p JOIN p.images img WHERE img = :imageUrl")
    Optional<String> findImageByProductImage(@Param("imageUrl") String imageUrl);
    // 예진 작업 끝
}

