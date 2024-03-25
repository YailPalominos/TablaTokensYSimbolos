public class AnalisisSemantico {

    public static void AnalizarOperacion(String cadena) {
        String[] partes = cadena.split("=");
        String asignacion = partes[0];
        String operacion = (partes[1]).replace(";", "");
        String[] operandos = operacion.split("\\s*[+\\-*/]\\s*");
        Simbolo simboloAsignacion = TablaSimbolos.obtenerSimbolo(asignacion);
        String tipoAsignacion = simboloAsignacion.Tipo;
        String tipoOperandos = "";
        for (int i = 0; i < operandos.length - 1; i++) {
            Simbolo simbolooperador1 = TablaSimbolos.obtenerSimbolo(operandos[i]);
            Simbolo simbolooperador2 = TablaSimbolos.obtenerSimbolo(operandos[i + 1]);
            if (simbolooperador1.Tipo != simbolooperador2.Tipo) {
                TablaSimbolos.GenerarError("Error Semántico", cadena,
                        "No se pueden mesclar los tipos de variables " + simbolooperador1.Token + "'"
                                + simbolooperador1.Tipo + "'" + " <-> " + simbolooperador2.Token + "'"
                                + simbolooperador2.Tipo + "'");
            } else {
                tipoOperandos = simbolooperador2.Tipo;
            }
        }
        if (!tipoAsignacion.equals(tipoOperandos)) {
            TablaSimbolos.GenerarError("Error Semántico", cadena,
                    "No se le puede asignar un valor distinto de '" + tipoAsignacion + "'");
        }
    }
}