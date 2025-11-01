package Utilitarios;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class UFechaHora {

    private static final DateTimeFormatter F_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter F_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private UFechaHora() {
    }

    public static String fechaActual() {
        return LocalDate.now().format(F_FECHA);
    }

    public static String horaActual() {
        return LocalTime.now().format(F_HORA);
    }
}
