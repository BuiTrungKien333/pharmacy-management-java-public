package com.pharmacy.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pagination {

    private int pageNumber; 
    
    private int pageSize;
    
    private int totalRecords;  
    
    public int getTotalPages() {
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    public int getOffset() {
        return (pageNumber - 1) * pageSize;
    }
    
}