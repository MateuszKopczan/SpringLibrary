package com.example.library.controller;

import com.example.library.converter.BookJson;
import com.example.library.converter.JsonToPojoConverter;
import com.example.library.dto.FilterSortDto;
import com.example.library.entity.*;
import com.example.library.payu.OrderStatusResponse;
import com.example.library.service.MainService;
import com.example.library.service.adminService.AdminService;
import com.example.library.service.bookService.BookService;
import com.example.library.service.categoryService.CategoryService;
import com.example.library.service.emailService.EmailAsyncService;
import com.example.library.service.orderService.OrderService;
import com.example.library.service.payuService.PayUService;
import com.example.library.service.userService.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/")
@Controller
public class MainController {

    @Autowired
    private PayUService payUService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailAsyncService emailAsyncService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;


    @GetMapping("/")
    public String showHomePage(Model model){
        Book exclusiveBook = bookService.getExclusiveOffer();
        List<Book> latestBooks = bookService.findLatest();
        List<Book> featuredBooks = bookService.findFeatured();

        model.addAttribute("featuredBooks", featuredBooks);
        model.addAttribute("latestBooks", latestBooks);
        model.addAttribute("exclusiveOffer", exclusiveBook);
        return "home";
    }

    @GetMapping("/products")
    public String showProducts(@RequestParam(required = false) Integer page,
                               @RequestParam(required = false) String sort,
                               @RequestParam(required = false) String category,
                               @RequestParam(required = false) Integer downPrice,
                               @RequestParam(required = false) Integer upPrice,
                               Model model){
        if(page == null || page < 1)
            page = 1;
        if(category == null)
            category = "";
        List<Book> books;
        Long pageCount = 0L;
        if(!category.equals("") || downPrice != null || upPrice != null || sort != null){
            books = bookService.findFiltered((page - 1) * 40, 40, downPrice, upPrice, category, sort);
            model.addAttribute("filterSortDto", new FilterSortDto(sort, downPrice, upPrice, category));
            pageCount = bookService.getResultCount(true,  40, downPrice, upPrice, category);
        }
        else {
            books = bookService.findPage((page - 1) * 40, 40, "default");
            model.addAttribute("filterSortDto", new FilterSortDto(downPrice, upPrice, category));
            pageCount = bookService.getResultCount(false, 40,downPrice,upPrice, category);
        }

        List<Category> categories = categoryService.findAll();
        model.addAttribute("latestBooks", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("lastPage", (int)Math.ceil(pageCount / 40.0));
        model.addAttribute("categories", categories);

        return "products";
    }

    @PostMapping("/products")
    public String showSortedProducts(@ModelAttribute("filterSortDto") FilterSortDto filterSortDto,
                                     RedirectAttributes redirectAttributes){

        redirectAttributes.addAttribute("sort", filterSortDto.sort);
        redirectAttributes.addAttribute("downPrice", filterSortDto.downPrice);
        redirectAttributes.addAttribute("upPrice", filterSortDto.upPrice);
        redirectAttributes.addAttribute("category", filterSortDto.categoryName);
        return "redirect:/products";
    }

    @GetMapping("/products/{productId}")
    public String showProductDetail(@PathVariable int productId, Model model){
        Book book = bookService.findById(productId);
        List<Book> relatedBooks = bookService.findRelated(book.getCategories());

        model.addAttribute("relatedBooks", relatedBooks);
        model.addAttribute("product", book);
        return "product-details";
    }

    @GetMapping("/access-denied")
    public String accessDenied(){
        return "error/404";
    }

    @GetMapping("/logout")
    public String logoutUser(){
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/";
    }


    @PostMapping(value = "/notify", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> notify(@RequestBody String body, HttpServletRequest request) {
        String openPayuSignatureHeader = request.getHeader("OpenPayu-Signature");
        if(!payUService.verifySignature(openPayuSignatureHeader, body))
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);

        ObjectMapper objectMapper = new ObjectMapper();
        OrderStatusResponse payUResponse = null;

        try {
            payUResponse = objectMapper.readValue(body, OrderStatusResponse.class);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
        if(payUResponse.getOrder() == null) {
            System.out.println(payUResponse);
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
        String payUOrderId = payUResponse.getOrder().getOrderId();
        Order order = orderService.findByPayUId(payUOrderId);
        if(payUResponse.getOrder().getPayMethod() != null) {
            String payMethod = payUResponse.getOrder().getPayMethod().getType();
            order.setPayMethod(order.getPayMethodMapping().get(payMethod));
        }
        else
            order.setPayMethod("");

        if(payUResponse.getOrder().getStatus().equals(OrderStatus.COMPLETED.name())) {
            order.setStatus(OrderStatus.WAITING_FOR_SHIPMENT.name());
            emailAsyncService.sendOrderWaitingForShipment(order);
        }
        else
            order.setStatus(payUResponse.getOrder().getStatus());
        orderService.save(order);

        if(payUResponse.getOrder().getStatus().equals("CANCELED")) {
            bookService.returnProducts(order.getBooks());
            emailAsyncService.sendOrderCancel(order);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
