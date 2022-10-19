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

public class UserTests extends BaseTestClass {


    RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .addHeader("api_key", Authorisation.getApiKey())
            .log(LogDetail.ALL)
            .build();

    @BeforeClass
    public void beforeClass() {
        createNewUser();
    }

    @AfterClass
    public void afterClass() {
        deleteUserByName("theUser");
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

    private void createNewUser() {
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
                .post(STORE_USER)
                .prettyPeek();

        given()
                .pathParam("userId", 10)
                .header("api_key", Authorisation.getApiKey())
                .when()
                .get(STORE_USER + "/{userId}")
                .prettyPeek()
                .then()
                .body("userName", equalTo("theUser"));
    }

    private void deleteUserByName(String username) {
        given()
                .log().everything()
                .contentType(ContentType.JSON)
                .header("api_key", Authorisation.getApiKey())
                .pathParam("username", username)
                .expect().statusCode(200)
                .when()
                .delete(STORE_USER + "/{username}");

        Assert.assertEquals(
                given()
                        .log().everything()
                        .contentType(ContentType.JSON)
                        .pathParam("username", username)
                        .get(STORE_USER + "/{username}")
                        .asString(), "User not found");
    }
}
