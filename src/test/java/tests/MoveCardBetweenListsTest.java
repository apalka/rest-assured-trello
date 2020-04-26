package tests;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class MoveCardBetweenListsTest extends BaseTest {

        private static String boardId;
        private static String firstListId;
        private static String secondListId;
        private static String newCardId;

        @BeforeAll
        public static void setUp(){
            BaseTest.beforeAll();
        }

        @Test
        @Order(1)
        public void createBoard() {

            Response response = given()
                    .spec(reqSpec)
                    .queryParam("name", "my new board")
                    .queryParam("defaultLists", false)
                    .when()
                    .post(BASE_URL + END_BOARDS)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            JsonPath json = response.jsonPath();

            boardId = json.get("id");

            Assertions.assertThat(json.getString("name")).isEqualTo("my new board");

        }

        @Test
        @Order(2)
        public void createFirstList() {

            Response response = given()
                    .spec(reqSpec)
                    .queryParam("name", "my first list")
                    .queryParam("idBoard", boardId)
                    .when()
                    .post(BASE_URL + END_LISTS)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            JsonPath json = response.jsonPath();

            firstListId = json.get("id");

            Assertions.assertThat(json.getString("name")).isEqualTo("my first list");

        }

        @Test
        @Order(3)
        public void createSecondList() {

            Response response = given()
                    .spec(reqSpec)
                    .queryParam("name", "my second list")
                    .queryParam("idBoard", boardId)
                    .when()
                    .post(BASE_URL + END_LISTS)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            JsonPath json = response.jsonPath();

            secondListId = json.get("id");

            Assertions.assertThat(json.getString("name")).isEqualTo("my second list");

        }

        @Test
        @Order(4)
        public void addCardToFirstList() {

            Response response = given()
                    .spec(reqSpec)
                    .queryParam("name", "my new card")
                    .queryParam("idList", firstListId)
                    .when()
                    .post(BASE_URL + END_CARDS)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            JsonPath json = response.jsonPath();

            newCardId = json.get("id");

            Assertions.assertThat(json.getString("name")).isEqualTo("my new card");
            Assertions.assertThat(json.getString("idList")).isEqualTo(firstListId);

        }

        @Test
        @Order(5)
        public void moveCardToSecondList() {

            Response response = given()
                    .spec(reqSpec)
                    .queryParam("idList", secondListId)
                    .when()
                    .put(BASE_URL + END_CARDS + "/" + newCardId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            JsonPath json = response.jsonPath();

            newCardId = json.get("id");

            Assertions.assertThat(json.getString("name")).isEqualTo("my new card");
            Assertions.assertThat(json.getString("idList")).isEqualTo(secondListId);

        }

        @Test
        @Order(6)
        public void deleteBoard() {
            given()
                    .spec(reqSpec)
                    .when()
                    .delete(BASE_URL + END_BOARDS + "/" + boardId)
                    .then()
                    .statusCode(200);
        }
    }
