package ua.expandapis.model.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.expandapis.dto.ProductDTO;
import ua.expandapis.model.entity.Product;
import ua.expandapis.model.entity.User;
import ua.expandapis.model.repository.ProductRepository;
import ua.expandapis.util.MapperUtil;

import java.util.List;

@Slf4j
@Service
@Transactional
public class ProductService {
    @PersistenceContext
    private EntityManager entityManager;
    private final ProductRepository productRepository;
    private final MapperUtil mapperUtil;

    public ProductService(ProductRepository productRepository, MapperUtil mapperUtil) {
        this.productRepository = productRepository;
        this.mapperUtil = mapperUtil;
    }

    public List<ProductDTO> findAll() {
        log.info("Find All Products Service");
        List<Product> products = productRepository.findAll();
        return mapperUtil.convertToDtoList(products, ProductDTO.class);
    }

    public List<ProductDTO> saveAll(List<ProductDTO> productsDTO) {
        log.info("Save All Products Service");
        List<Product> products = mapperUtil.convertToEntityList(productsDTO, Product.class);
        return mapperUtil.convertToEntityList(
                productRepository.saveAll(products), ProductDTO.class);
    }
}
