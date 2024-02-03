import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TablaTokens {

    private String codigoFuente = "";
    List<Token> tablaTokens = new ArrayList<>();
    private int linea = 1;
    // Patrones
    Pattern patronCambioLinea = Pattern.compile("\n");

    // Usar los siguientes patrones para el analisis
    // Pattern patronIniciarVariables = Pattern
    // .compile("((Real|Entero)
    // (([a-zA-Z0-9]+)\\,)(([a-zA-Z0-9]+)\\,)*(([a-zA-Z0-9]+)\\;))");
    // Pattern patronleerOEscribirVariables =
    // Pattern.compile("((Leer|Escribir)\\(([a-zA-Z0-9]*\\))\\;)");
    // Pattern patronOperacion = Pattern.compile(
    // "(\\w+)\\s*=\\s*((?:\\d+(?:\\.\\d+)?)|\\w+)\\s*((?:[-+*/]\\s*((?:\\d+(?:\\.\\d+)?)|\\w+)\\s*)+)(\\s*(\\([^()]+\\)|\\[[^\\[\\]]+\\]))?\\s*;");

    /*
     * Constructor de la tabla de tokens
     */
    public TablaTokens(String codigoFuente) {
        this.codigoFuente = codigoFuente;
    }

    /*
     * Analiza el codigo fuente para obtener los tokens
     */
    public void Analizar() {
        char[] caracteres = codigoFuente.toCharArray();
        String palabra = "";
        for (int indice = 0; indice < caracteres.length; indice++) {
            char caracter = caracteres[indice];
            palabra += caracter;
            String tipoCaracter = evaluarCaracter(caracter);
            // Para detectar el cambio de linea
            Matcher matcherCambioLinea = patronCambioLinea.matcher(codigoFuente);
            matcherCambioLinea.region(0, indice);
            int lineaV = 1;
            while (matcherCambioLinea.find()) {
                lineaV++;
            }
            if (lineaV > linea) {
                linea = lineaV;
                agregarToken("" + linea, "Cambio de linea");
            }
            if (tipoCaracter != null) {
                agregarToken(palabra, tipoCaracter);
                palabra = "";
            } else {

                if (evaluarCaracter(caracteres[(indice + 1)]) != null || caracteres[(indice + 1)] == ' ') {
                    palabra = palabra.trim();
                    String tipoCadena = evaluarCadena(palabra);
                    agregarToken(palabra, tipoCadena);
                    palabra = "";
                }
                if (caracter == ' ') {
                    palabra = "";
                }
            }
        }
    }

    /*
     * Agrega un token a la lista
     */
    private void agregarToken(String valor, String tipo) {
        Token oToken = new Token(valor, tipo);
        tablaTokens.add(oToken);
    }

    /*
     * Evalua un caracter y regresa el tipo del mismo del mismo
     */
    private String evaluarCaracter(char caracter) {
        switch (caracter) {
            case '+':
                return "Suma";
            case '-':
                return "Resta";
            case '*':
                return "Multiplicación";
            case '/':
                return "División";
            case '=':
                return "Asignación";
            case ',':
                return "Coma";
            case '(':
                return "Paréntesis de apertura";
            case ')':
                return "Paréntesis de cierre";
            case ';':
                return "Punto y coma";
            default:
                return null;
        }
    }

    /*
     * Evalua una cadena y regresa el tipo del mismo
     */
    private String evaluarCadena(String palabra) {
        if (Pattern.matches("(float|int|Escribir|Leer)", palabra)) {
            return "Palabra reservada";
        } else if (Pattern.matches("^[a-zA-Z]+$", palabra)) {
            return "Identificador";
        } else if (Pattern.matches("\\d+(\\.\\d)?", palabra)) {
            return "Constante";
        } else {
            return null;
        }
    }

}
