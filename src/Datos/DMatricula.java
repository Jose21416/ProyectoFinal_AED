package Datos;

public class DMatricula {

    private int numMatricula;   // correlativo (100001, 100002, …)
    private int codAlumno;      // id del alumno
    private int codCurso;       // id del curso
    private String fecha;       // "dd/MM/yyyy"
    private String hora;        // "HH:mm:ss"

    public DMatricula() {
    }

    public DMatricula(int numMatricula, int codAlumno, int codCurso, String fecha, String hora) {
        this.numMatricula = numMatricula;
        this.codAlumno = codAlumno;
        this.codCurso = codCurso;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getNumMatricula() {
        return numMatricula;
    }

    public void setNumMatricula(int numMatricula) {
        this.numMatricula = numMatricula;
    }

    public int getCodAlumno() {
        return codAlumno;
    }

    public void setCodAlumno(int codAlumno) {
        this.codAlumno = codAlumno;
    }

    public int getCodCurso() {
        return codCurso;
    }

    public void setCodCurso(int codCurso) {
        this.codCurso = codCurso;
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

}
