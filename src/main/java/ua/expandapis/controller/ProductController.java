package ua.expandapis.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.expandapis.dto.JsonProductRequestDTO;
import ua.expandapis.dto.ProductDTO;
import ua.expandapis.model.service.ProductService;

import java.util.List;

import static ua.expandapis.controller.MappingsConstants.*;

@Slf4j
@RestController
@RequestMapping(value = PRODUCT_CONTROLLER_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    ProductService productService;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(ADD_ALL_PRODUCTS_MAPPING)
    public ResponseEntity<?> addAllProducts(@RequestBody @Valid JsonProductRequestDTO requestDTO)
    {
        log.info("Add All Products Controller");
        List<ProductDTO> productDTOList = requestDTO.getRecords();

        JsonProductRequestDTO resultDTO = new JsonProductRequestDTO();
        resultDTO.setRecords( productService.saveAll(productDTOList) );

        return ResponseEntity.ok(resultDTO);
    }
    @GetMapping(GET_ALL_PRODUCTS_MAPPING)
    public ResponseEntity<List<ProductDTO>> getAllProducts()
    {
        log.info("Get All Products Controller");
        return ResponseEntity.ok(productService.findAll());
    }
}
