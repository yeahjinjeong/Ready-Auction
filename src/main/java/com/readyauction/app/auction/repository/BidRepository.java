package com.readyauction.app.auction.repository;

import com.readyauction.app.auction.dto.TopBidDto;
import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.BidStatus;
import com.readyauction.app.auction.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    // 여기에 추가적인 쿼리 메서드를 정의할 수 있습니다.

    Optional<Bid> findByMemberIdAndProduct(Long memberId, Product product);
    Optional<List<Bid>> findByProduct(Product product);

    @Query("SELECT new com.readyauction.app.auction.dto.TopBidDto(b.memberId, b.product, MAX(b.myPrice), MAX(b.bidTime)) " +
            "FROM Bid b WHERE b.product IN :products GROUP BY b.memberId, b.product")
    Optional<List<TopBidDto>> findTopBidsByProducts(@Param("products") List<Product> products);

    Optional<Bid> findTopByProductIdOrderByMyPriceDesc(Long productId);

    /** 지영 - 마이페이지 경매 참여 내역 조회 시 필요 **/

    // 입찰 중
    @Query("""
    SELECT b FROM Bid b
    WHERE b.memberId = :memberId
    AND b.bidStatus = 'CONFIRMED'
    AND b.product.auctionStatus <> 'END'
    """)
    List<Bid> findBiddingBids(@Param("memberId") Long memberId);

    // 낙찰
    @Query("""
    SELECT b FROM Bid b
    WHERE b.memberId = :memberId
    AND b.product.winner.memberId = :memberId
    """)
    List<Bid> findWinningBids(@Param("memberId") Long memberId);

    // 패찰
    @Query("""
    SELECT b FROM Bid b 
    WHERE b.memberId = :memberId 
    AND b.product.auctionStatus = 'END'
    AND b.myPrice < (
        SELECT MAX(b2.myPrice) FROM Bid b2 
        WHERE b2.product.id = b.product.id
        )
    """)
    List<Bid> findLosingBids(@Param("memberId") Long memberId);

    Optional<Bid> findByMemberIdAndProductId(Long memberId, Long productId);

    // 특정 상품에 대해 ACCEPTED 상태의 입찰 내역 가져오기
    List<Bid> findByProductAndBidStatus(Product product, BidStatus bidStatus);

    // 특정 상품의 입찰 내역을 최신순으로 정렬하여 가져오기 (경매 중)
    List<Bid> findByProductOrderByBidTimeDesc(Product product);

    // 특정 상품의 입찰 내역을 금액 기준으로 내림차순 정렬하여 가져옴
    List<Bid> findByProductOrderByMyPriceDesc(Product product);
}
