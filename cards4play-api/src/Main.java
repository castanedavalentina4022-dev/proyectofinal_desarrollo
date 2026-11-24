import api.TiendaApi;
import model.Tienda;

public class Main {
    public static void main(String[] args) {
        Tienda tienda = new Tienda();
        new TiendaApi(tienda).iniciar();
    }
}
