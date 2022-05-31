package com.example.library.controller;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.entity.Order;
import com.example.library.entity.OrderStatus;
import com.example.library.service.MainService;
import com.example.library.service.adminService.AdminService;
import com.example.library.service.bookService.BookService;
import com.example.library.service.cartService.CartService;
import com.example.library.service.emailService.EmailAsyncService;
import com.example.library.service.orderService.OrderService;
import com.example.library.service.payuService.PayUService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin")
@Controller
public class AdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private BookService bookService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private PayUService payUService;

    @Autowired
    private EmailAsyncService emailAsyncService;

    @GetMapping("/orders")
    public String showOrdersPage(Model model){
        List<Order> orders = orderService.findAll();

        model.addAttribute("orders", orders);
        return "admin/admin";
    }

    @PostMapping("/orders")
    public String showFilteredOrders(@RequestParam(required = false, name = "completed") boolean completed,
                                     @RequestParam(required = false, name = "waitingForShipment") boolean waitingForShipment,
                                     @RequestParam(required = false, name = "canceled") boolean canceled,
                                     @RequestParam(required = false, name = "shipment") boolean shipment, Model model){
        List<Order> allOrders = adminService.findFilteredOrders(completed, canceled, waitingForShipment, shipment);

        model.addAttribute("orders", allOrders);
        return "admin/admin";
    }

    @GetMapping("/orders/{orderId}")
    public String showOrderDetails(@PathVariable("orderId") int orderId, Model model){
        Order order = orderService.findById(orderId);
        List<Book> products = order.getBooks();
        Map<Book, Integer> map = cartService.createProductsMap(products);

        model.addAttribute("order", order);
        model.addAttribute("map", map);
        model.addAttribute("products", products);
        return "account/orderDetails";
    }

    @PostMapping("/orders/{productId}")
    public String markOrder(@PathVariable("productId") int id,
                            @RequestParam("status") String status){
        Order order = orderService.findById(id);
        if(status.equals(OrderStatus.SHIPMENT.name())){
            order.setStatus(OrderStatus.SHIPMENT.name());
            orderService.save(order);
            emailAsyncService.sendOrderShipment(order);
        }
        else if(status.equals(OrderStatus.CANCELED.name())){
            payUService.createRefund(order);
            bookService.returnProducts(order.getBooks());
            order.setStatus(OrderStatus.CANCELED.name());
            orderService.save(order);
            emailAsyncService.sendOrderCancel(order);
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/products")
    public String showProductForm(Model model){
        model.addAttribute("book", new BookDTO());
        return "admin/admin-product";
    }

    @PostMapping("/products")
    public String addProduct(@Valid @ModelAttribute("book") BookDTO bookDTO, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors())
            return "admin/admin-product";
        bookService.save(bookDTO);
        model.addAttribute("message", "Book was added");
        model.addAttribute("book", new BookDTO());
        return "admin/admin-product";
    }

    @GetMapping("/storage")
    public String showWarehouse(){
        return "admin/warehouse";
    }

    @GetMapping("/storage/{productId}")
    public String showProductForm(@PathVariable("productId") int productId, Model model){
        if(!model.containsAttribute("book")){
            Book book = bookService.findById(productId);
            if(book == null)
                return "error/404";
            model.addAttribute("book", book);
            model.addAttribute("crmBook", new BookDTO());
        }
        return "admin/warehouse";
    }

    @PostMapping("/storage")
    public String showBookFromWarehouse(@RequestParam(value = "id", required = false) int id, Model model,
                                        RedirectAttributes redirectAttributes){
        Book book = null;
        if(id != 0)
            book = bookService.findById(id);
        if(book == null) {
            model.addAttribute("message", "Not found");
            return "admin/warehouse";
        }
        redirectAttributes.addFlashAttribute("book", book);
        redirectAttributes.addFlashAttribute("crmBook", new BookDTO());
        return "redirect:/admin/storage/" + book.getId();
    }

    @PostMapping("/storage/{productId}")
    public String updateBook(@ModelAttribute("book") Book book, Model model){
        Book dbBook = bookService.findById(book.getId());
        dbBook.setNumberForSale(book.getNumberForSale());
        dbBook.setPrice(book.getPrice());
        dbBook.setImageUrl(book.getImageUrl());
        bookService.save(dbBook);
        model.addAttribute("book", dbBook);
        model.addAttribute("confirmation", "Book was updated");
        return "admin/warehouse";
    }

    @PostMapping("/storage/{productId}/remove")
    public String removeBook(@PathVariable("productId") int id, RedirectAttributes redirectAttributes){
        bookService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Book was deleted");
        return "redirect:/admin/storage";
    }
}
