# language: es
@before_get_access_token
Característica: Posición consolidada
  Como cliente del banco
  Quiero poder consultar la posicion consolidada de mis productos
  Para saber el estado de los mismos, tanto por el tipo de productos como por su saldo.

  Esquema del escenario: Una sola cuenta
    Dado que soy un cliente del banco con identificador (pasaporte, cedula) <idCliente>
    Cuando accedo a consultar mis "cuentas"
    Entonces obtengo una cuenta con código "<codigoCuenta>" y saldo igual a "<saldo>"

    Ejemplos: Cuenta
      | idCliente  | codigoCuenta                                                          | saldo    |
      | 240431155  | uriTech=PRODUCTO@2321348;uriSFB=04#001ROLEMPLEADO#1039014428#USD#ECU# | 80305.41  |


  Esquema del escenario: Una sola tarjeta de credito
    Dado que soy un cliente del banco con identificador (pasaporte, cedula) <idCliente>
    Cuando accedo a consultar mis "tarjetas de credito"
    Entonces obtengo una tarjeta de credito con código "<codigoTarjeta>" y valor disponible igual a "<valorDisponible>"

    Ejemplos: Tarjeta de credito
      | idCliente  | codigoTarjeta                                                              | valorDisponible   |
      | 240431155 | uriTech=PRODUCTO@2321406;uriSFB=MCE1#IHC#MT0077#USD#ECU#Cedula#0240431155   | 2000.00           |


  Esquema del escenario: Varios productos bancarios
    Dado que soy un cliente del banco con identificador (pasaporte, cedula) <idCliente>
    Cuando accedo a consultar mis "productos bancarios"
    Entonces obtengo una cuenta con código "<codigoCuenta>" y saldo igual a "<saldo>"
    Y obtengo una tarjeta de credito con código "<codigoTarjeta>" y valor disponible igual a "<valorDisponible>"

    Ejemplos: Cuenta y tarjeta de credito
      | idCliente  | codigoCuenta                                                          | saldo    | codigoTarjeta                                                             | valorDisponible |
      | 240431155  | uriTech=PRODUCTO@2321348;uriSFB=04#001ROLEMPLEADO#1039014428#USD#ECU# | 80305.41 | uriTech=PRODUCTO@2321406;uriSFB=MCE1#IHC#MT0077#USD#ECU#Cedula#0240431155 | 2000.00         |
