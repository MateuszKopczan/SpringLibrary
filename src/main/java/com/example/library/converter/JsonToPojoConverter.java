package com.example.library.converter;


import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class JsonToPojoConverter {

    private ClassLoader classLoader = JsonToPojoConverter.class.getClassLoader();
    private Random random = new Random();

    public List<BookJson> getBooksObjects(){
        //List<Book> books = new ArrayList<>();
        List<BookJson> booksJson = new ArrayList<>();
        try{
            ObjectMapper mapper = new ObjectMapper();
            booksJson = Arrays.asList(mapper.readValue(
                    new File(classLoader.getResource("static/json/books.json").getFile()), BookJson[].class));

//            for(BookJson bookJson : booksJson)
//                books.add(getBookObject(bookJson));

        } catch(Exception e){
            e.printStackTrace();
        }
        return booksJson;
    }

    public Book getBookObject(BookJson bookJson){
        Book book = new Book();
        book.setId(0);
        book.setTitle(bookJson.getTitle());
        book.setPageCount(bookJson.getPageCount());
        book.setNumberToBorrow(random.nextInt(20));
        book.setNumberForSale(random.nextInt(30));
        book.setPrice(random.nextInt(200) + 30);
        book.setImageUrl(bookJson.getThumbnailUrl());
        book.setIsbn(bookJson.getIsbn());
        if(bookJson.getPublishedDate() != null)
            book.setPublicationYear(bookJson.getPublishedDate().getYear());
        book.setShortDescription(bookJson.getShortDescription());
        book.setLongDescription(bookJson.getLongDescription());
//        for (String name : bookJson.getAuthors()) {
//            Author author = new Author();
//            author.setId(0);
//            author.setName(name);
//            author.addBook(book);
//            book.addAuthor(author);
//        }


        return book;
    }
}
