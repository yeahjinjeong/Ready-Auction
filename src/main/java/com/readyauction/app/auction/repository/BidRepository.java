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

    /** 지영 - 마이페이지 경매 등록 내역 조회 시 필요 **/
    // 입찰 중 -
    @Query("SELECT b.product FROM Bid b WHERE b.memberId = :memberId AND b.bidStatus = 'CONFIRMED' AND b.product.auctionStatus != 'END'")
    List<Product> findActiveBids(@Param("memberId") Long memberId);

    /** 지영 - 마이페이지 경매 참여 내역 조회 시 필요 **/
    @Query("SELECT DISTINCT b.product.id FROM Bid b WHERE b.memberId = :memberId")
    List<Long> findProductIdsWithBidsByMemberId(Long memberId);
}
