package tests;

import base.BaseTest;
import com.github.javafaker.Faker;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static utils.TestHelpers.deleteOrganization;

public class OrganizationsPostTest extends BaseTest {

    static List<String> orgIds;

    @BeforeAll
    public static void setUp() {
        BaseTest.beforeAll();
        orgIds = new ArrayList<String>();
    }

    @Test
    public void createNewOrganization() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "New Organization")
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        orgIds.add(json.getString("id"));

        Assertions.assertThat(json.getString("displayName")).isEqualTo("New Organization");

    }

    @Test
    public void createOrganizationWithoutDisplayName() {
        given()
                .spec(reqSpec)
                .queryParam("displayName", "")
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                .statusCode(400);
    }

    @Test
    public void createOrganizationWithDescription() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Organization with desc")
                .queryParam("desc", "Some description")
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        orgIds.add(json.getString("id"));

        Assertions.assertThat(json.getString("displayName")).isEqualTo("Organization with desc");
        Assertions.assertThat(json.getString("desc")).isEqualTo("Some description");

    }

    @Test
    public void createOrganizationWithMinLengthName() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Organization with name")
                .queryParam("name", "org")
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        orgIds.add(json.getString("id"));

        Assertions.assertThat(json.getString("displayName")).isEqualTo("Organization with name");
        //name is not exactly the same because other signs are added
        //no information about extra signs in docs
        Assertions.assertThat(json.getString("name")).contains("org");

    }

    @Test
    public void createOrganizationWithUppercaseName() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Organization with uppercase")
                .queryParam("name", "Organization Name")
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                //received status 200 but it's not consistent with docs
                //no information about converting to lowercase in docs
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        orgIds.add(json.getString("id"));

        Assertions.assertThat(json.getString("displayName")).isEqualTo("Organization with uppercase");
        Assertions.assertThat(json.getString("name")).isLowerCase();

    }

    @Test
    public void createOrganizationWithAllowedSignsInName() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Organization with allowed signs")
                .queryParam("name", "org_123")
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        orgIds.add(json.getString("id"));

        Assertions.assertThat(json.getString("displayName")).isEqualTo("Organization with allowed signs");
        Assertions.assertThat(json.getString("name")).isEqualTo("org_123");

    }

    @Test
    public void createOrganizationWithNotEnoughSignsInName() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Organization with too short name")
                .queryParam("name", "or")
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                //received status 200 but it's not consistent with docs
                //no information about extra signs in docs
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        orgIds.add(json.getString("id"));

        Assertions.assertThat(json.getString("displayName")).isEqualTo("Organization with too short name");
        Assertions.assertThat(json.getString("name").length()).isGreaterThan(2);

    }

    @Test
    public void createOrganizationWithHTTPWebsite() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Organization with HTTP website")
                .queryParam("website", "http://olastb.com")
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        orgIds.add(json.getString("id"));

        Assertions.assertThat(json.getString("displayName")).isEqualTo("Organization with HTTP website");
        Assertions.assertThat(json.getString("website")).isEqualTo("http://olastb.com");

    }

    @Test
    public void createOrganizationWithHTTPSWebsite() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Organization with HTTPS website")
                .queryParam("website", "https://olastb.com")
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        orgIds.add(json.getString("id"));

        Assertions.assertThat(json.getString("displayName")).isEqualTo("Organization with HTTPS website");
        Assertions.assertThat(json.getString("website")).isEqualTo("https://olastb.com");

    }

    @Test
    public void createOrganizationWithWebsiteWithoutProtocol() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Organization without protocol")
                .queryParam("website", "www.olastb.com")
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                //received status 200 but it's not consistent with docs
                //no information about adding protocol in docs
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        orgIds.add(json.getString("id"));

        Assertions.assertThat(json.getString("displayName")).isEqualTo("Organization without protocol");
        Assertions.assertThat(json.getString("website")).startsWith("http");

    }

    @Test
    public void verifyNamesUniqueness(){

        String fakeOrgName = new Faker().company().name();

        Response response1 = given()
                .spec(reqSpec)
                .queryParam("displayName", "Organization 1")
                .queryParam("name", fakeOrgName)
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response1.jsonPath();
        String orgName1 = json.get("name");
        orgIds.add(json.getString("id"));

        Response response2 = given()
                .spec(reqSpec)
                .queryParam("displayName", "Organization 2")
                .queryParam("name", fakeOrgName)
                .when()
                .post(BASE_URL + END_ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        json = response2.jsonPath();
        String orgName2 = json.get("name");
        orgIds.add(json.getString("id"));

        Assertions.assertThat(orgName1).isNotEqualTo(orgName2);

    }

    @AfterAll
    public static void deleteOrganizations() {
        for (String org : orgIds) {
            deleteOrganization(org, reqSpec, BASE_URL, END_ORGANIZATIONS);
        }
    }
}
