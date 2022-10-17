import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;

public class StoreTests extends BaseTestClass{

    @BeforeClass
    public void beforeClass() {
        createNewOrder();
    }

    @AfterClass
    public void afterClass() {
        deleteOrderById(10);
    }

    @Test
    public void getOrderByID(){
        Response response = given()
                .log().everything()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("orderId", 10)
                .header("api_key", Authorisation.getApiKey())
                .when()
                .get(STORE_ORDER_ENDPOINT + "/{orderId}");

        response.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/order.json"));
        response.prettyPeek();
    }

    private void createNewOrder() {
        given()
                .relaxedHTTPSValidation()
                .log().everything()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "\"id\": 10,\n" +
                        "  \"petId\": 198772,\n" +
                        "  \"quantity\": 7,\n" +
                        "  \"shipDate\": \"2022-10-16T11:07:44.786Z\",\n" +
                        "  \"status\": \"approved\",\n" +
                        "  \"complete\": true\n" +
                        "}")
                .header("api_key", Authorisation.getApiKey())
                .when()
                .post(STORE_ORDER_ENDPOINT)
                .prettyPeek();

        given()
                .log().everything()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("orderId", 10)
                .header("api_key", Authorisation.getApiKey())
                .when()
                .get(STORE_ORDER_ENDPOINT + "/{orderId}")
                .prettyPeek()
                .then()
                .body("petId", equalTo(198772));
    }

    private void deleteOrderById(Integer orderId) {
        given()
                .log().everything()
                .contentType(ContentType.JSON)
                .header("api_key", Authorisation.getApiKey())
                .pathParam("orderId", orderId)
                .expect().statusCode(200)
                .when()
                .delete(STORE_ORDER_ENDPOINT + "/{orderId}");

        Assert.assertEquals(
                given()
                        .log().everything()
                        .contentType(ContentType.JSON)
                        .pathParam("orderId", orderId)
                        .get(STORE_ORDER_ENDPOINT + "/{orderId}")
                        .asString(), "Order not found");
    }

}
