package com.readyauction.app.chat.repository;

import com.readyauction.app.auction.dto.ProductDto;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.entity.Winner;
import com.readyauction.app.chat.dto.ChatProductDto;
import com.readyauction.app.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatProductRepository extends JpaRepository<Product, Long> {

    Product findProductById(Long productId);

}
