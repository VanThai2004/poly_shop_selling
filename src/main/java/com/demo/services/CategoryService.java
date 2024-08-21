package com.demo.services;

import com.demo.models.Category;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

//TODO: Connect to database

@Service
public class CategoryService {

    public List<Category> getAll(){
        return Arrays.asList(
            new Category("IP", "IPhone"),
            new Category("ANDR", "Android")
        );
    }
}
