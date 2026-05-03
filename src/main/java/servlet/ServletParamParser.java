package servlet;

/**
 * Utilidad para parsing seguro de parámetros de servlet.
 * Encapsula el manejo de excepciones NumberFormatException.
 */
public class ServletParamParser {

    /**
     * Parsea un parámetro como Long.
     * @param value El valor string del parámetro.
     * @param fieldName Nombre del campo para mensajes de error.
     * @return El valor parseado.
     * @throws NumberFormatException si el valor no es un número válido.
     */
    public static Long parseLong(String value, String fieldName) throws NumberFormatException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Valor inválido para " + fieldName + ": " + value);
        }
    }

    /**
     * Parsea un parámetro como Integer.
     * @param value El valor string del parámetro.
     * @param fieldName Nombre del campo para mensajes de error.
     * @return El valor parseado.
     * @throws NumberFormatException si el valor no es un número válido.
     */
    public static Integer parseInt(String value, String fieldName) throws NumberFormatException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Valor inválido para " + fieldName + ": " + value);
        }
    }

    /**
     * Parsea un parámetro como Double.
     * @param value El valor string del parámetro.
     * @param fieldName Nombre del campo para mensajes de error.
     * @return El valor parseado.
     * @throws NumberFormatException si el valor no es un número válido.
     */
    public static Double parseDouble(String value, String fieldName) throws NumberFormatException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Valor inválido para " + fieldName + ": " + value);
        }
    }
}
