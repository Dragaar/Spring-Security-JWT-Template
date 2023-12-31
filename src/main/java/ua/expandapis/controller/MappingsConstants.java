package ua.expandapis.controller;

public class MappingsConstants {
    private static final String BASE_MAPPING = "";

    public static final String AUTH_CONTROLLER_MAPPING = BASE_MAPPING + "/user";
    public static final String AUTHENTICATION_MAPPING = "/authenticate";
    public static final String REGISTRATION_MAPPING = "/add";
    //public static final String ID_POST_VARIABLE = "postId";
   // public static final String ID_VARIABLE = "id";

    public static final String PRODUCT_CONTROLLER_MAPPING = BASE_MAPPING + "/products";
    public static final String ADD_ALL_PRODUCTS_MAPPING = "/add";
    public static final String GET_ALL_PRODUCTS_MAPPING = "/all";
    //public static final String ID_PARAM = "id";


    private MappingsConstants() {
    }
}
