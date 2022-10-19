import io.restassured.RestAssured;

public class BaseTestClass {

    protected String STORE_ORDER_ENDPOINT = "/store/order";
    protected String STORE_USER = "/store/user";


    public BaseTestClass() {
        RestAssured.baseURI = "https://petstore3.swagger.io";
        RestAssured.basePath = "/api/v3";
//        RestAssured.baseURI = "http://localhost";
//        RestAssured.port = 18080;
    }
}
