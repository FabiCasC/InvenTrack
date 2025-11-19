package app;

import app.config.FirebaseConfig;
import app.service.LoteService;
import app.service.MovimientoService;
import app.service.PedidoService;
import app.service.ProductoService;
import app.service.ProveedorService;
import app.service.UsuarioService;
import java.util.Scanner;

public class Main {
    //metodos de prueba
    public static void main(String[] args) {
        FirebaseConfig.estado();
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("\n**** Menu de prueba ****");
            System.out.println("0. Salir");
            System.out.println("1. Listar usuarios");
            System.out.println("2. Listar productos");
            System.out.println("3. Mostrar proveedores");
            System.out.println("4. Mostrar lotes");
            System.out.println("5. Mostrar movimientos");
            System.out.println("6. Mostrar pedidos");
            System.out.print("Ingrese una opcion: ");
            System.out.println("");
            op = sc.nextInt();

            switch (op) {
                case 1: {
                    UsuarioService usuario = new UsuarioService();
                    usuario.mostrarUsuarios();
                    break;
                }
                case 2: {
                    ProductoService producto = new ProductoService();
                    producto.mostrarProductos();
                    break;
                }
                case 3: {
                    ProveedorService prov = new ProveedorService();
                    prov.mostrarProveedores();
                    break;
                }
                case 4: {
                    LoteService l = new LoteService();
                    l.mostrarLotes();
                    break;
                }
                case 5: {
                    MovimientoService m = new MovimientoService();
                    m.mostrarMovimientos();
                    break;
                }
                case 6: {
                    PedidoService p = new PedidoService();
                    p.mostrarPedidos();
                    break;
                }
                case 0: {
                    System.out.println("Saliendo del sistema...");
                    break;
                }
                default: {
                    System.out.println("Opción inválida, intenta de nuevo.");
                }
            }

        } while (op != 0);

    }
}
    

