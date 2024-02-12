import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TablaSimbolos {

    public List<Token> tablaTokens = new ArrayList<>();
    public List<Simbolo> tablaSimbolos = new ArrayList<>();
    private int linea = 1;
    // Expresiones regulares usadas en el analisis.
    Pattern patronIniciarVariables = Pattern
            .compile("((float|int)\\s(([a-zA-Z0-9]+)\\,)(([a-zA-Z0-9]+)\\,)*(([a-zA-Z0-9]+)\\;))");
    Pattern patronleerOEscribirVariables = Pattern.compile("((Leer|Escribir)\\(([a-zA-Z0-9]*\\))\\;)");
    Pattern patronOperacion = Pattern.compile(
            "(\\w+)\\s*=\\s*((?:\\d+(?:\\.\\d+)?)|\\w+)\\s*((?:[-+*/]\\s*((?:\\d+(?:\\.\\d+)?)|\\w+)\\s*)+)(\\s*(\\([^()]+\\)|\\[[^\\[\\]]+\\]))?\\s*;");

    /*
     * Constructor de la tabla de simbolos
     */
    public TablaSimbolos(List<Token> tablaTokens) {
        this.tablaTokens = tablaTokens;
        Analizar();
    }

    /*
     * Analiza la tabla de tokens para la elaboración de la tabla de simbolos
     */
    private void Analizar() {
        String cadena = "";
        for (Token token : tablaTokens) {
            cadena += token.Valor
                    + (token.Tipo.equals("Palabra reservada") && !token.Valor.equals("Escribir")
                            && !token.Valor.equals("Leer") ? " " : "");
            if (token.Tipo.equals("Cambio de linea")) {
                linea++;
                cadena = "";
            } else {
                if (token.Tipo.equals("Constante")) {
                    String tipo = !cadena.contains(".") ? "int" : "float";
                    agregarSimbolo(token.Valor, tipo, token.Valor);
                }
                if (validar(cadena) == true) {
                    cadena = "";
                }
            }
        }
    }

    private void agregarSimbolo(String token, String tipo, String valor) {
        if (verificarToken(token)) {
            Simbolo simbolo = new Simbolo();
            simbolo.Token = token;
            simbolo.Tipo = tipo;
            simbolo.Repeticiones = 1;
            simbolo.Linea = "" + linea;
            simbolo.Valor = valor;
            tablaSimbolos.add(simbolo);
        }
    }

    /*
     * Comprueba que exista el Token en caso de existir añade una repeticion y no se
     * crea
     */
    private boolean verificarToken(String token) {
        for (Simbolo simbolo : tablaSimbolos) {
            if (simbolo.Token.equals(token)) {
                simbolo.Repeticiones++;
                simbolo.Linea = simbolo.Linea + "," + linea;
                return false;
            }
        }
        return true;
    }

    private boolean validar(String cadena) {

        Matcher matcherIniciarVariables = patronIniciarVariables.matcher(cadena);
        Matcher matcherleerOEscribirVariables = patronleerOEscribirVariables.matcher(cadena);
        Matcher matcherOperacion = patronOperacion.matcher(cadena);

        if (matcherIniciarVariables.find()) {
            String tipo = cadena.contains("int") ? "int" : "float";
            String variablesCadena = cadena.replace(" ", "");
            variablesCadena = variablesCadena.replace("int", "");
            variablesCadena = variablesCadena.replace("float", "");
            variablesCadena = variablesCadena.replace(";", "");
            String[] variables = variablesCadena.split(",");
            for (String variable : variables) {
                agregarSimbolo(variable, tipo, tipo.equals("int") ? "0" : "0.0");
            }
            return true;
        } else if (matcherleerOEscribirVariables.find()) {
            cadena = cadena.replace("Leer", "");
            cadena = cadena.replace("Escribir", "");
            cadena = cadena.replace("(", "");
            cadena = cadena.replace(")", "");
            cadena = cadena.replace(";", "");
            verificarToken(cadena);
            return true;
        } else if (matcherOperacion.find()) {
            String[] partes = cadena.split("=");
            String asignacion = partes[0];
            String operacion = (partes[1]).replace(";", "");
            verificarToken(asignacion);
            String[] operandos = operacion.split("\\s*[+\\-*/]\\s*");
            for (String operando : operandos) {
                verificarToken(operando);
            }
            return true;
        } else {
            return false;
        }
    }

}
