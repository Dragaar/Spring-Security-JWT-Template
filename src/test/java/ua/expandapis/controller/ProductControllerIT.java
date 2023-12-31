package ua.expandapis.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import ua.expandapis.dto.JsonProductRequestDTO;
import ua.expandapis.dto.ProductDTO;
import ua.expandapis.model.entity.Product;
import ua.expandapis.model.repository.ProductRepository;
import ua.expandapis.util.MapperUtil;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.expandapis.TestEnvironmentConstants.*;
import static ua.expandapis.controller.MappingsConstants.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
public class ProductControllerIT {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    MapperUtil mapperUtil;
    ObjectMapper objectMapper;
    ProductRepository productRepository;

    @Autowired
    public ProductControllerIT(MapperUtil mapperUtil, ObjectMapper objectMapper, ProductRepository productRepository) {
        this.mapperUtil = mapperUtil;
        this.objectMapper = objectMapper;
        this.productRepository = productRepository;
    }

    @BeforeEach
    public void setup() {
        log.info("setUp");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }
    @AfterEach
    void cleanUp() {
        log.info("cleanUp");
        Optional<Product> product = productRepository.findByItemCode(ITEM_CODE);
        if(product.isPresent())
            productRepository.deleteById(product.get().getId());
        Optional<Product> product2 = productRepository.findByItemCode(SECOND_ITEM_CODE);
        if(product2.isPresent())
            productRepository.deleteById(product2.get().getId());
    }

    @Test
    @DisplayName("Test Wac configuration")
    public void testGivenWacProductController() {

        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertNotNull(servletContext);
        Assertions.assertTrue(servletContext instanceof MockServletContext);
        Assertions.assertNotNull(webApplicationContext.getBean("productController"));
    }

    @Test
    @DisplayName("Add all Products")
    public void testAddAllProducts() throws Exception {
        log.info("Add all Products");
        var productDTO = addAllProductsPostRequest();
        assertTrue(productRepository.existsById(productDTO.get(0).getId()));
        assertEquals(ITEM_NAME, productDTO.get(0).getItemName());

        assertTrue(productRepository.existsById(productDTO.get(1).getId()));
        assertEquals(SECOND_ITEM_NAME, productDTO.get(1).getItemName());

    }

    private List<ProductDTO> addAllProductsPostRequest() throws Exception {
        List<ProductDTO> productDTOList = jsonToProductRequestDTO(mockMvc.perform(post(PRODUCT_CONTROLLER_MAPPING + ADD_ALL_PRODUCTS_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "\t\"records\" : [\n" +
                                "{\n" +
                                "\t\"entryDate\": \"" + DATE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\",\n" +
                                "\t\"itemCode\": \"" + ITEM_CODE + "\",\n" +
                                "\t\"itemName\": \"" + ITEM_NAME + "\",\n" +
                                "\t\"itemQuantity\": \"" + ITEM_QUANTITY + "\",\n" +
                                "\t\"status\": \"" + ITEM_STATE.getState() + "\"\n" +
                                "},\n" +
                                "\t{\n" +
                                "\t\"entryDate\": \"" + SECOND_DATE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\",\n" +
                                "\t\"itemCode\": \"" + SECOND_ITEM_CODE + "\",\n" +
                                "\t\"itemName\": \"" + SECOND_ITEM_NAME + "\",\n" +
                                "\t\"itemQuantity\": \"" + SECOND_ITEM_QUANTITY + "\",\n" +
                                "\t\"status\": \"" + SECOND_ITEM_STATE.getState() + "\" }]" +
                                "}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());
        return productDTOList;
    }

    private List<ProductDTO> jsonToProductRequestDTO(String json) throws IOException {
        JsonProductRequestDTO productRequestDTO = objectMapper.readValue(json, JsonProductRequestDTO.class);
        return productRequestDTO.getRecords();
    }

    @Test
    @DisplayName("Invalid Add All Products format")
    public void testInvalidAddAllProductsFormat() throws Exception {
        log.info("Invalid Add All Products");
        invalidAddAllProductsRequest();
    }
    private void invalidAddAllProductsRequest() throws Exception {
        mockMvc.perform(post(PRODUCT_CONTROLLER_MAPPING + ADD_ALL_PRODUCTS_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "\t\"records\" : [\n" +
                                "{\n" +
                                "\t\"entryDate\": \"" + DATE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\",\n" +
                                "\t\"itemName\": \"" + ITEM_NAME + "\",\n" +
                                "\t\"itemQuantity\": \"" + ITEM_QUANTITY + "\",\n" +
                                "\t\"status\": \"" + ITEM_STATE.getState() + "\"\n" +
                                "},\n" +
                                "\t{\n" +
                                "\t\"entryDate\": \"" + SECOND_DATE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\",\n" +
                                "\t\"itemCode\": \"" + SECOND_ITEM_CODE + "\",\n" +
                                "\t\"itemName\": \"" + SECOND_ITEM_NAME + "\",\n" +
                                "\t\"itemQuantity\": \"" + SECOND_ITEM_QUANTITY + "\"\n }]" +
                                "}"))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();
    }

    @Test
    @DisplayName("Incorrect Add All Product State")
    public void testIncorrectAddAllProductState() throws Exception {
        log.info("Incorrect Add All Product State");
        incorrectAddAllProductStatePostRequest();
    }

    private void incorrectAddAllProductStatePostRequest() throws Exception {
        mockMvc.perform(post(PRODUCT_CONTROLLER_MAPPING + ADD_ALL_PRODUCTS_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "\t\"records\" : [\n" +
                                "{\n" +
                                "\t\"entryDate\": \"" + DATE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\",\n" +
                                "\t\"itemCode\": \"" + ITEM_CODE + "\",\n" +
                                "\t\"itemName\": \"" + ITEM_NAME + "\",\n" +
                                "\t\"itemQuantity\": \"" + ITEM_QUANTITY + "\",\n" +
                                "\t\"status\": \"" + INCORRECT_ITEM_STATE + "\"\n" +
                                "},\n" +
                                "\t{\n" +
                                "\t\"entryDate\": \"" + SECOND_DATE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\",\n" +
                                "\t\"itemCode\": \"" + SECOND_ITEM_CODE + "\",\n" +
                                "\t\"itemName\": \"" + SECOND_ITEM_NAME + "\",\n" +
                                "\t\"itemQuantity\": \"" + SECOND_ITEM_QUANTITY + "\",\n" +
                                "\t\"status\": \"" + SECOND_INCORRECT_ITEM_STATE + "\" }]" +
                                "}"))
                .andExpect(status().is4xxClientError());
    }



    @Test
    @DisplayName("Get all Products")
    public void testGetAllProducts() throws Exception {
        log.info("Get all Products");
        var initialProductDTOList = addAllProductsPostRequest();

        var resultProductDTOList = getAllProductsPostRequest();
        assertThat(resultProductDTOList).contains(initialProductDTOList.get(0), initialProductDTOList.get(1));
    }

    private List<ProductDTO> getAllProductsPostRequest() throws Exception {
        List<ProductDTO> productDTOList = jsonToProductDTO(mockMvc.perform(get(PRODUCT_CONTROLLER_MAPPING + GET_ALL_PRODUCTS_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());
        return productDTOList;
    }

    private List<ProductDTO> jsonToProductDTO(String json) throws IOException {
        return objectMapper.readValue(json, new TypeReference<List<ProductDTO>>(){});
    }


}
