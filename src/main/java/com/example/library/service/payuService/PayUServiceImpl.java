package com.example.library.service.payuService;

import com.example.library.entity.Book;
import com.example.library.entity.Order;
import com.example.library.payu.*;
import com.example.library.service.payuService.properties.PayUProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PayUServiceImpl implements PayUService{

    @Autowired
    private PayUProperties payUProperties;

    @Override
    public String getAuthorizationToken() {
        RestTemplate restTemplate = new RestTemplate();
        String authorizationUri = getAuthenticationUri();
        AuthorizationToken payUAuthorizationToken = restTemplate.getForObject(authorizationUri, AuthorizationToken.class);
        return payUAuthorizationToken.getAccess_token();
    }

    @Override
    public OrderCreateResponse createNewOrder(Order order, Map<Book, Integer> map, String userEmail) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

        HttpHeaders headers = getHeaders();
        OrderCreateRequest orderCreateRequest = getOrderCreateRequest(order, map);
        String requestBody = convertOrderCreateRequestToJSON(orderCreateRequest);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(payUProperties.getUrl(), HttpMethod.POST, entity, String.class);

        return convertJsonToOrderCreateResponse(response.getBody());
    }

    @Override
    public OrderStatusResponse checkPaymentStatus() {
        return null;
    }

    @Override
    public OrderRefundResponse createRefund(Order order) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        String description = "Refund: " + order.getPayUOrderId();
        OrderRefundRequest orderRefundRequest = OrderRefundRequest.builder()
                .refund(OrderRefundRequest.Refund.builder()
                        .description(description)
                        .amount((int) (order.getPrice() * 100))
                        .build())
                .build();
        String requestBody = convertOrderRefundRequestToJSON(orderRefundRequest);
        System.out.println(requestBody);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String refundUrl = payUProperties.getUrl() + "/" + order.getPayUOrderId() + "/refunds";
        System.out.println(refundUrl);
        ResponseEntity<String> response = restTemplate.exchange(refundUrl, HttpMethod.POST, entity, String.class);
        System.out.println(response.getBody());
        return convertJsonToOrderRefundResponse(response.getBody());
    }

    @Override
    public boolean verifySignature(String header, String body) {
        Map<String, String> headerValues = Arrays.stream(header.split(";"))
                                        .map(s -> s.split("="))
                                        .collect(Collectors.toMap(s -> s[0], s -> s[1]));
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String concatenated = body + payUProperties.getSecondKey();
            md.update(concatenated.getBytes());
            byte[] digest = md.digest();
            String expectedSignature = DatatypeConverter.printHexBinary(digest).toLowerCase();
            return expectedSignature.equals(headerValues.get("signature"));
        } catch (NoSuchAlgorithmException e){
            return false;
        }
    }

    private String getAuthenticationUri(){
        return payUProperties.getAuthorizationUrl() + "?grant_type=client_credentials&client_id=" +
                payUProperties.getClientId() + "&client_secret=" + payUProperties.getClientSecret();
    }

    private HttpHeaders getHeaders(){
        String authorizationToken = getAuthorizationToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authorizationToken);
        return headers;
    }

    private OrderCreateRequest getOrderCreateRequest(Order order, Map<Book, Integer> map){
        return OrderCreateRequest.builder()
                .notifyUrl(payUProperties.getNotifyUrl())
                .continueUrl(payUProperties.getContinueUrl())
                .customerIp("127.0.0.1")
                .merchantPosId(payUProperties.getMerchantPosId())
                .description("Order: " + order.getId())
                .currencyCode("PLN")
                .totalAmount(String.valueOf((int) order.getPrice() * 100))
                .products(getProducts(map))
                .buyer(getBuyerObject(order))
                .build();
    }

    private OrderCreateRequest.PayUBuyer getBuyerObject(Order order){
        return OrderCreateRequest.PayUBuyer.builder()
                .firstName(order.getOrderDetail().getFirstName())
                .lastName(order.getOrderDetail().getLastName())
                .phone(order.getOrderDetail().getPhone())
                .email(order.getOrderDetail().getEmail())
                .language("en")
                .delivery(
                        OrderCreateRequest.PayUDelivery.builder()
                                .city(order.getOrderDetail().getAddress().getCity())
                                .street(order.getOrderDetail().getAddress().getStreet())
                                .postalCode(order.getOrderDetail().getAddress().getPostalCode())
                                .countryCode("PL")
                                .build()
                ).build();
    }

    private String convertOrderCreateRequestToJSON(OrderCreateRequest orderCreateRequest){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String json = "";
        try {
            json = objectMapper.writeValueAsString(orderCreateRequest);
        } catch(JsonProcessingException e){
            e.printStackTrace();
        }
        return json;
    }

    private String convertOrderRefundRequestToJSON(OrderRefundRequest orderRefundRequest){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String json = "";
        try {
            json = objectMapper.writeValueAsString(orderRefundRequest);
        } catch(JsonProcessingException e){
            e.printStackTrace();
        }
        return json;
    }

    private List<OrderCreateRequest.Product> getProducts(Map<Book, Integer> map){
        List<OrderCreateRequest.Product> productsList = new LinkedList<>();

        for(Map.Entry<Book, Integer> entry : map.entrySet()){
            productsList.add(
                    OrderCreateRequest.Product.builder()
                            .name(entry.getKey().getTitle())
                            .quantity(String.valueOf(entry.getValue()))
                            .unitPrice(String.valueOf((int) entry.getKey().getPrice() * 100))
                    .build());
        }
        return productsList;
    }

    private OrderCreateResponse convertJsonToOrderCreateResponse(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            return objectMapper.readValue(json, OrderCreateResponse.class);
        } catch(Exception e){
            e.printStackTrace();
            return OrderCreateResponse.builder().build();
        }
    }

    private OrderRefundResponse convertJsonToOrderRefundResponse(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            return objectMapper.readValue(json, OrderRefundResponse.class);
        } catch(Exception e){
            e.printStackTrace();
            return OrderRefundResponse.builder().build();
        }
    }
}
