package com.readyauction.app.auction.repository;

import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Category;
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

    /** 지영 - 마이페이지 경매 참여 내역 조회 시 필요 **/
    // 낙찰
    @Query("""
    SELECT p FROM Product p
    WHERE p.winner.memberId = :memberId
    """)
    List<Product> findWinningBids(@Param("memberId") Long memberId);

    /** 지영 - 마이페이지 경매 등록 내역 조회 시 필요 **/

    // 판매 중 내역 (start 또는 progress 상태인 경매)
    @Query("""
    SELECT p FROM Product p
    WHERE p.memberId = :memberId
    AND (p.auctionStatus = 'START' OR p.auctionStatus = 'PROGRESS')
    """)
    List<Product> findActiveProductsByMemberId(@Param("memberId") Long memberId);

    // 거래 완료 내역
    // 1. 결제 완료 (confirmed 상태인 winner)
    @Query("""
    SELECT p FROM Product p
    WHERE p.memberId = :memberId
    AND p.winner.status = 'CONFIRMED'
    """)
    List<Product> findConfirmedProductsByMemberId(@Param("memberId") Long memberId);

    // 2. 구매확정 (accepted 상태인 winner)
    @Query("""
    SELECT p FROM Product p
    WHERE p.memberId = :memberId
    AND p.winner.status = 'ACCEPTED'
    """)
    List<Product> findAcceptedProductsByMemberId(@Param("memberId") Long memberId);

    // 유찰 내역 (경매 종료 및 입찰자가 없는 상품)
    @Query("""
    SELECT p FROM Product p 
    WHERE p.memberId = :memberId 
    AND p.auctionStatus = 'END' 
    AND NOT EXISTS (SELECT 1 FROM Bid b WHERE b.product.id = p.id)
    """)
    List<Product> findFailedProductsByMemberId(@Param("memberId") Long memberId);


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

    List<Product> findByAuctionStatus(AuctionStatus auctionStatus); // 필터링을 위한 메서드

    // Filter by category and status
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.auctionStatus <> :status")
    Page<Product> findByCategoryAndAuctionStatus(@Param("category") Category category, @Param("status") AuctionStatus status, Pageable pageable);

    // Search by name, category, and status
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.category = :category AND p.auctionStatus <> :status")
    Page<Product> searchByNameCategoryAndStatus(@Param("name") String name, @Param("category") Category category, @Param("status") AuctionStatus status, Pageable pageable);

}
