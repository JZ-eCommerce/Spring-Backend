package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.repository.jpa.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public void add(Account account){
        final Cart cart = Cart.createCart(account);
        cartRepository.save(cart);
    }

    // FIXME: cart, cart_product 분리해야 됨.
    public void removeByAccount(Account account) {
        cartRepository.deleteByAccountId(account.getId()); // cart_product 삭제
        cartRepository.delete(account.getCart());          // cart 삭제
    }

    public Cart readByAccountId(Long accountId) {
        return cartRepository.findByAccountId(accountId)
                .orElseThrow(EntityNotFoundException::new);
    }
}
