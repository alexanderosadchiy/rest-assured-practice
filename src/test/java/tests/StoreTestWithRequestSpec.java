import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;

public class StoreTestWithRequestSpec extends BaseTestClass {

    RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .addHeader("api_key", Authorisation.getApiKey())
            .log(LogDetail.ALL)
            .build();

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
        Response response = given(requestSpecification)
                .pathParam("orderId", 10)
                .when()
                .get(STORE_ORDER_ENDPOINT + "/{orderId}");

        response.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/order.json"));
        response.prettyPeek();
    }

    private void createNewOrder() {
        given(requestSpecification)
                .body("{\n" +
                        "\"id\": 10,\n" +
                        "  \"petId\": 198772,\n" +
                        "  \"quantity\": 7,\n" +
                        "  \"shipDate\": \"2022-10-16T11:07:44.786Z\",\n" +
                        "  \"status\": \"approved\",\n" +
                        "  \"complete\": true\n" +
                        "}")
                .when()
                .post(STORE_ORDER_ENDPOINT)
                .prettyPeek();

        given(requestSpecification)
                .pathParam("orderId", 10)
                .when()
                .get(STORE_ORDER_ENDPOINT + "/{orderId}")
                .prettyPeek()
                .then()
                .body("petId", equalTo(198772));
    }

    private void deleteOrderById(Integer orderId) {
        given(requestSpecification)
                .pathParam("orderId", orderId)
                .expect().statusCode(200)
                .when()
                .delete(STORE_ORDER_ENDPOINT + "/{orderId}");

        Assert.assertEquals(
                given(requestSpecification)
                        .pathParam("orderId", orderId)
                        .get(STORE_ORDER_ENDPOINT + "/{orderId}")
                        .asString(), "Order not found");
    }
}
