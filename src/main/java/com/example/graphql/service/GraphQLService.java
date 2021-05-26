package com.example.graphql.service;

import com.example.graphql.model.Book;
import com.example.graphql.repository.BookRepository;
import com.example.graphql.service.dataFetcher.AllBooksDataFetcher;
import com.example.graphql.service.dataFetcher.BookDataFetcher;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import java.io.File;
import java.io.IOException;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

@Service
public class GraphQLService {

    @Value("classpath:books.graphql")
    Resource resource;

    private GraphQL graphQL;

    @Autowired
    BookRepository bookRepository;


    @Autowired
    AllBooksDataFetcher allBooksDataFetcher;
    @Autowired
    BookDataFetcher bookDataFetcher;



    // load schema at application start up
    @PostConstruct
    private void loadSchema() throws IOException {

        //Load Books into the Book Repository
        loadDataIntoHSQL();

        // get the schema
        File schemaFile = resource.getFile();
        // parse schema
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildRuntimeWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private void loadDataIntoHSQL() {

        Stream.of(
                new Book("1", "Mrityunjaya", "Kindle Edition", "Shivaji Sawant", "Nov 2017"),
                new Book("2", "ShrimanYogi", "Orielly", "Ranjit Desai", "Jan 2015"),
                new Book("3", "Mahabharata", "Shree", "Venkat", "Dec 2016")
        ).forEach(book -> {
            bookRepository.save(book);
        });
    }


    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring.dataFetcher("allBooks",allBooksDataFetcher)
                                                                  .dataFetcher("book",bookDataFetcher))
                .build();
    }

    public GraphQL getGraphQL(){
        return graphQL;
    }
}
