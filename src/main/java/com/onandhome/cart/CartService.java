package com.onandhome.cart;

import java.util.List;
import java.util.Optional;

import com.onandhome.cart.entity.CartItem;
import com.onandhome.admin.adminProduct.entity.Product;
import com.onandhome.admin.adminProduct.ProductRepository;
import com.onandhome.user.UserRepository;
import com.onandhome.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Slf4j
public class CartService {
    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;


    public List<CartItem> getCartItems(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null)
            return List.of();
        return cartRepo.findByUser(user);
    }

    @Transactional
    public CartItem addToCart(Long userId, Long productId, int qty) {
        log.info("장바구니 담기 시작 - userId: {}, productId: {}, qty: {}", userId, productId, qty);
        
        User user = userRepo.findById(userId).orElseThrow(() -> 
            new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Product p = productRepo.findById(productId).orElseThrow(() -> 
            new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));
        
        log.info("사용자 조회 성공: {}, 상품 조회 성공: {}", user.getId(), p.getId());
        
        // 이미 장바구니에 있는 상품인지 확인
        Optional<CartItem> existing = cartRepo.findByUserAndProduct(user, p);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + Math.max(qty, 1);
            item.setQuantity(newQty);
            log.info("기존 장바구니 아이템 수량 증가: {}", newQty);
            return cartRepo.save(item);
        }
        
        // 새로운 아이템 추가
        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(p);
        item.setQuantity(Math.max(qty, 1));
        log.info("새로운 장바구니 아이템 생성 - quantity: {}", item.getQuantity());
        return cartRepo.save(item);
    }

    @Transactional
    public CartItem updateQuantity(Long cartItemId, int quantity) {
        CartItem item = cartRepo.findById(cartItemId).orElseThrow();
        item.setQuantity(Math.max(quantity, 1));
        return cartRepo.save(item);
    }

    @Transactional
    public void removeItem(Long cartItemId) {
        cartRepo.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        cartRepo.deleteByUser(user);
    }
}
