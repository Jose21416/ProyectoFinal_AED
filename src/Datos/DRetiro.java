package Datos;

public class DRetiro {

    private int numRetiro;       // correlativo (200001, 200002, …)
    private int numMatricula;    // matrícula asociada
    private String fecha;        // "dd/MM/yyyy"
    private String hora;         // "HH:mm:ss"

    public DRetiro() {
    }

    public DRetiro(int numRetiro, int numMatricula, String fecha, String hora) {
        this.numRetiro = numRetiro;
        this.numMatricula = numMatricula;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getNumRetiro() {
        return numRetiro;
    }

    public void setNumRetiro(int numRetiro) {
        this.numRetiro = numRetiro;
    }

    public int getNumMatricula() {
        return numMatricula;
    }

    public void setNumMatricula(int numMatricula) {
        this.numMatricula = numMatricula;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    @Override
    public String toString() {
        return "Retiro{"
                + "numRetiro=" + numRetiro
                + ", numMatricula=" + numMatricula
                + ", fecha='" + fecha + '\''
                + ", hora='" + hora + '\''
                + '}';
    }
}
