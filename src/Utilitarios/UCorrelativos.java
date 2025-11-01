package Utilitarios;

import java.util.concurrent.atomic.AtomicInteger;

public final class UCorrelativos {

    private static final AtomicInteger MATRICULA = new AtomicInteger(100001);
    private static final AtomicInteger RETIRO = new AtomicInteger(200001);

    private UCorrelativos() {
    }

    public static int nextMatricula() {
        return MATRICULA.getAndIncrement();
    }

    public static int nextRetiro() {
        return RETIRO.getAndIncrement();
    }
}
