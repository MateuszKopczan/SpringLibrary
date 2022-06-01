package com.example.library.service.cartService;

import com.example.library.dao.cartDAO.CartDAO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class CartServiceImpl implements CartService{

    @Autowired
    private CartDAO cartDAO;

    @Override
    public Cart findByUserId(long id) {
        return cartDAO.findByUserId(id);
    }

    @Override
    public void save(Cart cart) {
        cartDAO.save(cart);
    }

    @Override
    public void deleteById(long id) {
        cartDAO.deleteById(id);
    }

    @Override
    public Map<Book, Integer> createProductsMap(List<Book> products) {
        if (products == null)
            return new LinkedHashMap<Book, Integer>();
        Map<Book, Integer> map = new LinkedHashMap<>();
        for(Book book : products){
            book.getAuthors().sort(Comparator.comparing(Author::getName));
            if(map.containsKey(book)){
                Integer value = map.get(book);
                if (value == null)
                    value = 0;
                value++;
                map.put(book, value);
            }
            else
                map.put(book, 1);
        }
        return map;
    }

    @Override
    public float getCartValue(Cart cart) {
        float value = 0;
        for(Book book : cart.getBooks())
            value += book.getPrice();
        return value;
    }
}
