package com.deepanshu.controller;

import com.deepanshu.modal.Product;
import com.deepanshu.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pincodes")
public class PincodeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{pincode}/products")
    public ResponseEntity<List<Product>> getProductsByPincode(@PathVariable String pincode) {
        List<Product> products = productService.findProductsByPincode(pincode);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
    }
}