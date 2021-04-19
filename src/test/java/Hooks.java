import io.cucumber.java.Before;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Hooks {
    @Before("@before_get_access_token")
    public void beforeGetAccessToken() throws Throwable {

        //Parámetros de la llamada
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("grant_type", "password");
        parameters.put("username", StepDefinitions.rsaUtil.encryptValue(StepDefinitions.userFieldString + StepDefinitions.username));
        parameters.put("password", StepDefinitions.rsaUtil.encryptValue(StepDefinitions.password));

        //Cebecera de la llamada
        Map<String,Object> headers = new HashMap<String,Object>();
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString(StepDefinitions.credentials.getBytes()));
        headers.put("channel", "NBM");

        //Ejecución de la llamada
        Response response = given().headers(headers).params(parameters).
                post(StepDefinitions.baseUrl + "/oauth/token").
                then().assertThat().statusCode(200).
                log().all().
                contentType(ContentType.JSON).
                extract().response();

        //Obtención del token de acceso
        JSONObject responseJson = new JSONObject(response.asString());
        StepDefinitions.access_token = responseJson.getString("access_token");
        singleSelectCustomerAuthenticationDetails();

    }

    public void singleSelectCustomerAuthenticationDetails() {

        //Parámetros de la llamada
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("access_token", StepDefinitions.access_token);

        //Parámetros de la llamada
        Map<String,Object> headers = new HashMap<String,Object>();
        headers.put("channel", "NBM");
        headers.put("Content-Type" , "application/json");

        //Contrucción del cuerpo de la llamada creando un mensaje encriptado
        String param = " ";
        String password = StepDefinitions.secureMessageUtil.getRandom(10);
        String data = StepDefinitions.secureMessageUtil.createSecureMessage(
                param,
                password,
                StepDefinitions.secureMessageUtil.getRandom(16),
                StepDefinitions.secureMessageUtil.getRandom(16)
        ).toString();

        //Ejecución de la llamada
        Response response = given().queryParams(parameters).headers(headers).body(data).
                post(StepDefinitions.baseUrl + "/rest/callService/json/singleSelectCustomerAuthenticationDetails").
                then().assertThat().statusCode(200).
                log().all().
                contentType(ContentType.JSON).
                extract().response();

        //Desencriptado de la respuesta
        String decodedResponse = StepDefinitions.secureMessageUtil.resolveMessage(response.asString(), password);
        JSONObject responseJson = new JSONObject(decodedResponse);
        JSONObject authenticationDetails = responseJson.getJSONObject("singleSelectCustomerAuthenticationDetails").getJSONObject("authenticationDetails");

        StepDefinitions.userId = authenticationDetails.getString("executingOperatorId");
        StepDefinitions.idClienteRecibido = authenticationDetails.getInt("identificationNumber");

        String email = authenticationDetails.getString("emailAddressComplete");
        String firstName = authenticationDetails.getString("firstName1");
        String lastName = authenticationDetails.getString("lastName1");
        String tokenValue = authenticationDetails.getString("tokenValue");

        System.out.println("\n\n" + "singleSelectCustomerAuthenticationDetails (Authentications details): " + authenticationDetails.toString() + "\n\n");

        processSystemUserSecurityBNK(email, firstName, lastName, tokenValue);

    }

    public void processSystemUserSecurityBNK(String email, String firstName, String lastName, String tokenValue) {

        //Parámetros de la llamada
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("access_token", StepDefinitions.access_token);

        //Parámetros de la llamada
        Map<String,Object> headers = new HashMap<String,Object>();
        headers.put("channel", "NBM");
        headers.put("Content-Type" , "application/json; charset=utf-8");

        //Contrucción del cuerpo de la llamada creando un mensaje encriptado
        String param = "{ \n" +
                "   \"processSystemUserSecurityBNK\":{ \n" +
                "      \"customer\":{ \n" +
                "         \"@name\":\"customer\",\n" +
                "         \"electronicsContact\":{ \n" +
                "            \"emailAddressComplete\":\"" + email + "\"\n" +
                "         },\n" +
                "         \"token\":{ \n" +
                "            \"value\":\"" + tokenValue + "\"\n" +
                "         },\n" +
                "         \"ipDetail\":{ \n" +
                "            \"ipDescription\":\"automático\"\n" +
                "         },\n" +
                "         \"firstName1\":\"" + firstName + "\",\n" +
                "         \"lastName1\":\"" + lastName + "\"\n" +
                "      }\n" +
                "   }\n" +
                "}\n";

        System.out.println("\n\n" + "processSystemUserSecurityBNK (Sin cifrar): " + param + "\n\n");

        String password = StepDefinitions.secureMessageUtil.getRandom(10);
        String data = StepDefinitions.secureMessageUtil.createSecureMessage(
                param,
                password,
                StepDefinitions.secureMessageUtil.getRandom(16),
                StepDefinitions.secureMessageUtil.getRandom(16)
        ).toString();

        System.out.println("\n\n" + "processSystemUserSecurityBNK (CIFRADO): " + data + "\n\n");

        //Ejecución de la llamada
        Response response = given().queryParams(parameters).headers(headers).body(data).
                post(StepDefinitions.baseUrl + "/rest/callService/json/processSystemUserSecurityBNK").
                then().assertThat().statusCode(200).
                log().all().
                contentType(ContentType.JSON).
                extract().response();

        //Desencriptado de la respuesta
        String decodedResponse = StepDefinitions.secureMessageUtil.resolveMessage(response.asString(), password);
        System.out.println("\n\n" + "processSystemUserSecurityBNK (Respuesta decodificada): " + decodedResponse + "\n\n");


    }

}
