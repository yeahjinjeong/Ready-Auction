package com.readyauction.app.auction;

import com.readyauction.app.auction.dto.ProductDto;
import com.readyauction.app.auction.entity.Category;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuctionControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        // 기존 데이터 초기화 및 테스트용 데이터 삽입
        productRepository.deleteAll();
        Product product = Product.builder()
                .memberId(1L)
                .name("Test Product")
                .category(Category.BASEBALL)
                .description("테스트 상품입니다.")
                .bidUnit(10)
                .endTime(new Timestamp(System.currentTimeMillis() + 3600000))
                .startTime(new Timestamp(System.currentTimeMillis()))
                .currentPrice(100)
                .immediatePrice(200)
//                .images("test_image.jpg")
                .build();
        productRepository.save(product);
    }

    @Test
    public void testSearchAuction() {
        String url = "http://localhost:" + port + "/auction/auction";

        ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("Test Product");
    }
}