package br.com.siecola.aws_project01.controller;

import br.com.siecola.aws_project01.enums.EventType;
import br.com.siecola.aws_project01.model.Product;
import br.com.siecola.aws_project01.repository.ProductRepository;
import br.com.siecola.aws_project01.service.ProductPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductPublisher publisher;

//    public ProductController(ProductRepository productRepository){
//        this.repository = productRepository;
//    }

    @GetMapping
    public Iterable<Product> findAll(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id){
        Optional<Product> optional = repository.findById(id);
        if(optional.isPresent()){
            return new ResponseEntity<Product>(optional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product){
        Product productCreated = repository.save(product);
        publisher.publishProductEvent(productCreated, EventType.PRODUCT_CREATED, "user1");
        return new ResponseEntity<Product>(productCreated, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("id") long id){
        if(repository.existsById(id)){
            product.setId(id);
            Product productUpd = repository.save(product);
            publisher.publishProductEvent(productUpd, EventType.PRODUCT_UPDATE, "user2");
            return new ResponseEntity<Product>(productUpd, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id){
        Optional<Product> optional = repository.findById(id);
        if(optional.isPresent()){
            Product product = optional.get();
            repository.delete(product);
            publisher.publishProductEvent(product, EventType.PRODUCT_DELETED, "user3");
            return new ResponseEntity<Product>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/bycode")
    public ResponseEntity<Product> findByCode(@RequestParam String code){
        Optional<Product> optional = repository.findByCode(code);
        if(optional.isPresent()){
            return new ResponseEntity<Product>(optional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
