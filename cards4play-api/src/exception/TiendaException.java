package exception;


public class TiendaException extends RuntimeException {

    private final int status;

    public TiendaException(int status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    public int getStatus() { return status; }

    public static TiendaException datosInvalidos(String m)  { return new TiendaException(400, m); }
    public static TiendaException noAutenticado(String m)   { return new TiendaException(401, m); }
    public static TiendaException prohibido(String m)       { return new TiendaException(403, m); }
    public static TiendaException noEncontrado(String m)    { return new TiendaException(404, m); }
    public static TiendaException conflicto(String m)       { return new TiendaException(409, m); }
    public static TiendaException yaOcurrio(String m)       { return new TiendaException(410, m); }
}
