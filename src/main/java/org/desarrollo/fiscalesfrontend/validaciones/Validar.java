package org.desarrollo.fiscalesfrontend.validaciones;

public class Validar {

    public static boolean validarCadena(String cadena) {
        if (cadena.isEmpty()) {
            return false;
        }
        return cadena.length() >= 4;
    }

    public static boolean validarEdad(Integer num) {
        return num > 16 && num < 90;
    }

    public static boolean validarEnteroPositivo(Integer num2) {
        return num2 > 0;
    }
}
