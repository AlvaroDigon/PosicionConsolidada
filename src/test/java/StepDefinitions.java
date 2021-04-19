import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;


public class StepDefinitions {

    protected static Properties properties = null;

    static {
        try {
            properties = new Properties();
            properties.load(StepDefinitions.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //URL base
    protected static String baseUrl = properties.getProperty("baseUrl");
    protected static String credentials = properties.getProperty("credentials");
    protected static String userFieldString = "uriSFB=Login#";

    //Datos del usuario
    protected static String username = properties.getProperty("username"); //Otro usuario: evolutivo106
    protected static String password = properties.getProperty("password");

    //Identificador del cliente
    public static String userId;

    //Datos de los productos
    protected static String codigoCuentaRecibido;
    protected static String codigoTarjetaRecibido;

    protected static String saldoRecibido;
    protected static String valorDisponibleRecibido;

    protected static int idClienteRecibido;

    //Token de acceso
    protected static String access_token;

    //Instancias de clases auxiliares
    protected static RSAUtilSingleton rsaUtil = RSAUtilSingleton.getInstance();
    protected static SecureMessageUtil secureMessageUtil = new SecureMessageUtil();

    @Dado("que soy un cliente del banco con identificador \\(pasaporte, cedula) {int}")
    public void queSoyUnClienteDelBancoConIdentificadorPasaporteCedula(int idCliente) {

        Assert.assertEquals(idCliente, idClienteRecibido);

    }

    @Cuando("accedo a consultar mis {string}")
    public void accedoAConsultarMiPosicionConsolidada(String productos) {

        String codigoProducto;
        switch (productos){

            case "cuentas":
                codigoProducto = "ACCOUNTS";
                break;

            case "tarjetas de credito":
                codigoProducto = "CREDIT_CARD";
                break;

            default:
                codigoProducto = "ALL";
        }

        //Parámetros de la llamada
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("access_token", access_token);

        //Parámetros de la llamada
        Map<String,Object> headers = new HashMap<String,Object>();
        headers.put("channel", "NBM");
        headers.put("featureId", "ROL@5096");
        headers.put("Content-Type" , "application/json; charset=utf-8");

        //Contrucción del cuerpo de la llamada creando un mensaje encriptado
        String param = "{ \n" +
                "   \"systemUser\":{ \n" +
                "      \"@name\":\"systemUser\",\n" +
                "      \"customer\":{ \n" +
                "         \"identificationType\":{ \n" +
                "            \"mnemonic\":\"USUARIO_CLAVE\"\n" +
                "         }\n" +
                "      },\n" +
                "      \"userId\":\"" + userId + ";userName=" + username + ";\"\n" +
                "   },\n" +
                "   \"queryType\":{ \n" +
                "      \"@name\":\"queryType\",\n" +
                "      \"mnemonic\":\"ALL\"\n" +
                "   },\n" +
                "   \"collection\":{ \n" +
                "      \"@name\":\"productFamilies\",\n" +
                "      \"productFamily\":{ \n" +
                "         \"mnemonic\":\"" + codigoProducto + "\"\n" +
                "      }\n" +
                "   },\n" +
                "   \"generic\":{ \n" +
                "      \"mode\":\"FULL\"\n" +
                "   }\n" +
                "}\n";

        System.out.println("\n\n" +  "massiveSelectCustomerOperationWithPermissions (Sin cifrar): " + param + "\n\n");

        String password = secureMessageUtil.getRandom(10);
        String data = secureMessageUtil.createSecureMessage(
                param,
                password,
                secureMessageUtil.getRandom(16),
                secureMessageUtil.getRandom(16)
        ).toString();

        System.out.println("\n\n" + "massiveSelectCustomerOperationWithPermissions (CIFRADO): " + data + "\n\n");

        //Ejecución de la llamada
        Response response = given().queryParams(parameters).headers(headers).body(data).
                post(baseUrl + "/rest/callService/json/massiveSelectCustomerOperationWithPermissions").
                then().assertThat().statusCode(200).
                log().all().
                contentType(ContentType.JSON).
                extract().response();

        //Desencriptado de la respuesta
        String decodedResponse = secureMessageUtil.resolveMessage(response.asString(), password);
        System.out.println("\n\n" + "massiveSelectCustomerOperationWithPermissions (Respuesta decodificada): " + decodedResponse + "\n\n");

        JSONObject responseJson = new JSONObject(decodedResponse);

        if(productos.compareTo("cuentas") == 0 || productos.compareTo("productos bancarios") == 0) {
            JSONObject account = responseJson.getJSONObject("massiveSelectCustomerOperationWithPermissions").getJSONObject("accounts").getJSONArray("account").getJSONObject(0);

            codigoCuentaRecibido = account.getJSONObject("customerOperationPermission").getJSONObject("customerOperation").getString("customerOperationId");
            saldoRecibido = account.getString("availableBalance");

        }

        if(productos.compareTo("tarjetas de credito") == 0 || productos.compareTo("productos bancarios") == 0){

            JSONObject creditCard = responseJson.getJSONObject("massiveSelectCustomerOperationWithPermissions").getJSONObject("creditCards").getJSONArray("creditCard").getJSONObject(0);

            codigoTarjetaRecibido = creditCard.getJSONObject("customerOperationPermission").getJSONObject("customerOperation").getString("customerOperationId");
            valorDisponibleRecibido = creditCard.getString("availablePurchaseLimit");

        }

    }

    @Entonces("obtengo una cuenta con código {string} y saldo igual a {string}")
    public void obtengoUnaCuentaConCódigoYSaldoIgualA(String codigoCuenta, String saldo) {

        Assert.assertEquals(codigoCuenta, codigoCuentaRecibido);
        Assert.assertEquals(saldo, saldoRecibido);

    }

    @Entonces("obtengo una tarjeta de credito con código {string} y valor disponible igual a {string}")
    public void obtengoUnaTarjetaDeCreditoConCódigoYValorDisponibleIgualA(String codigoTarjeta, String valorDisponible) {

        Assert.assertEquals(codigoTarjeta, codigoTarjetaRecibido);
        Assert.assertEquals(valorDisponible, valorDisponibleRecibido);

    }

}

