package com.example.graphql.service.dataFetcher;

import com.example.graphql.model.Book;
import com.example.graphql.repository.BookRepository;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class BookDataFetcher implements DataFetcher <Book> {

    @Autowired
   private BookRepository bookRepository;

    @Override
    public Book get(DataFetchingEnvironment dataFetchingEnvironment) {

       String isbn =   dataFetchingEnvironment.getArgument("id");


        Book book = bookRepository.findById(isbn).orElse(null);
        return book;
    }
}
