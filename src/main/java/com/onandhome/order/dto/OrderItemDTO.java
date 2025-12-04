package com.onandhome.order.dto;

import com.onandhome.order.entity.OrderItem;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderItemDTO {
    
    private Long orderItemId;
    private Long id; // 관리자용
    
    private Long productId;
    
    private String productName;
    
    private int productPrice; // 상품 가격
    
    private int orderPrice; // 주문 가격
    
    private int price; // orderPrice 별칭 (프론트엔드 호환성)
    
    private int quantity;
    
    /**
     * OrderItem Entity를 DTO로 변환
     */
    public static OrderItemDTO fromEntity(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .orderItemId(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .productPrice(orderItem.getProduct().getPrice())
                .orderPrice(orderItem.getOrderPrice())
                .price(orderItem.getOrderPrice()) // 별칭
                .quantity(orderItem.getCount())
                .build();
    }
    
    /**
     * 주문 상품의 총 가격 계산
     */
    public int getTotalPrice() {
        return orderPrice * quantity;
    }
}
