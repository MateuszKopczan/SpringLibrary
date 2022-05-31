package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.entity.Cart;
import com.example.library.config.userDetails.CustomUserDetails;
import com.example.library.service.bookService.BookService;
import com.example.library.service.cartService.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("cart")
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private BookService bookService;

    @GetMapping("")
    public String showCart(Model model, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int id = userDetails.getUser().getId();
        Cart cart = cartService.findByUserId(id);
        if (cart == null){
            cart = Cart.builder().user(userDetails.getUser()).build();
            cartService.save(cart);
        }
        List<Book> booksInCart = cart.getBooks();
        Map<Book, Integer> map = cartService.createProductsMap(booksInCart);
        booksInCart.sort(Comparator.comparing(Book::getTitle));

        model.addAttribute("products", booksInCart);
        model.addAttribute("map", map);
        return "account/cart";
    }

    @PostMapping("/add")
    public String addToCart(@ModelAttribute("Book") Book book, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int id = userDetails.getUser().getId();
        Cart cart = cartService.findByUserId(id);
        if (cart == null)
            cart = Cart.builder().user(userDetails.getUser()).build();
        Book dbBook = bookService.findById(book.getId());
        cart.addBookToCart(dbBook);
        cartService.save(cart);
        return "redirect:/cart";
    }

    @GetMapping("/remove")
    public String removeFromCart(@RequestParam("productId") int id, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Cart cart = cartService.findByUserId(userDetails.getUser().getId());
        cart.removeAllBooksFromCartOfGivenId(id);
        cartService.save(cart);
        return "redirect:/cart";
    }

    @PostMapping("/update/decrease")
    public String decreaseProductQuantityInCart(@ModelAttribute("Book") Book book, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Cart cart = cartService.findByUserId(userDetails.getUser().getId());
        cart.removeBookFromCart(book.getId());
        cartService.save(cart);
        return "redirect:/cart";
    }

    @PostMapping("/update/increase")
    public String increaseProductQuantityInCart(@ModelAttribute("Book") Book book, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Cart cart = cartService.findByUserId(userDetails.getUser().getId());
        Book dbBook = bookService.findById(book.getId());
        cart.addBookToCart(dbBook);
        cartService.save(cart);
        return "redirect:/cart";
    }

}