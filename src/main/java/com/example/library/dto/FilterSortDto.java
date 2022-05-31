package com.example.library.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
public class FilterSortDto {

    public String sort;
    public Integer downPrice;
    public Integer upPrice;
    public String categoryName;

    public FilterSortDto(){}

    public FilterSortDto(Integer downPrice, Integer upPrice, String categoryName) {
        this.downPrice = downPrice;
        this.upPrice = upPrice;
        this.categoryName = categoryName;
    }

    public FilterSortDto(String sort, Integer downPrice, Integer upPrice, String categoryName) {
        this.sort = sort;
        this.downPrice = downPrice;
        this.upPrice = upPrice;
        this.categoryName = categoryName;
    }
}
