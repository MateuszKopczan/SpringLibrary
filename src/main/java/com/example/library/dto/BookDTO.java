package com.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {

    private int id;

    @NotNull(message = "Title is required")
    @Size(min=3, message = "Title is required")
    private String title;

    @NotNull(message="Put number 0 to skip")
    private int pageCount;

    @NotNull(message = "Number for sale is required")
    private int numberForSale;

    @NotNull(message = "Price is required")
    private float price;

    private String imageUrl;

    @NotNull(message = "ISBN is required")
    @Size(min=10, max=13, message = "Input correct ISBN with length between 10-13")
    private String isbn;

    private int publicationYear;
    private String shortDescription;
    private String longDescription;
    private String authors;

    @NotNull(message="Categories are required")
    private String categories;
}
