import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;

// junit die korchi

public class MyAPICalling {

    Properties prop;
    MyAPICalling myAPICalling;

    @Test
    public void doLogin() throws ConfigurationException, IOException {

         myAPICalling = new MyAPICalling();
        myAPICalling.readConfig();
        RestAssured.baseURI = myAPICalling.prop.getProperty("baseUrl");
        Response res =
                given()
                        .contentType("application/json")
                        .body("{" +
                                "\"email\": \"salman@roadtocareer.net\",\n" +
                                "\"password\": \"1234\"}")
                .when()
                        .post("/user/login")
                .then()
                        .assertThat().statusCode(200).extract().response();

        // then() part is optional

//        System.out.println(res.asString());

        JsonPath jsonObj = res.jsonPath();
        String token = jsonObj.get("token");
//        System.out.println(token);

        saveEnvVar("token", token);
    }

    //confidential info, so config file e rakhbo.
    public static void saveEnvVar(String key, String value) throws ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration("./src/test/resources/config.properties");

        config.setProperty(key, value);
        config.save();
    }

    @Test
    public void getUserInfo() throws ConfigurationException, IOException {
        myAPICalling = new MyAPICalling();
        myAPICalling.readConfig();
        RestAssured.baseURI = myAPICalling.prop.getProperty("baseUrl");

        Response res =
                given()
                        .contentType("application/json")
                        .header("Authorization", myAPICalling.prop.getProperty("token"))
                        .when()
                        .get("/user/search/id/12643")
                        .then()
                        .assertThat().statusCode(200).extract().response();

        System.out.println(res.asString());

        JsonPath jsonObj = res.jsonPath();
        String name = jsonObj.get("user.name");

        System.out.println(name);
    }


    public void readConfig() throws IOException {
        prop = new Properties();
        FileInputStream file = new FileInputStream("./src/test/resources/config.properties");
        prop.load(file);
    }

//    public static void main(String[] args) throws IOException {
//        MyAPICalling myAPICalling = new MyAPICalling();
//        myAPICalling.readConfig();
//        String token = myAPICalling.prop.getProperty("token");
//        System.out.println(token);
//    }
}

