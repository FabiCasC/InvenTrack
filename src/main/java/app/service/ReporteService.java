
package app.service;

import app.model.Lotes;
import app.model.Movimientos;
import app.model.Productos;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReporteService {
    //reportes basicos
    private final Firestore db;
    private final String COLLECTION_PRODUCTOS = "Productos";
    private final String COLLECTION_LOTES = "Lotes";
    private final String COLLECTION_MOVIMIENTOS = "Movimientos";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public ReporteService() {
        this.db = FirestoreClient.getFirestore();
    }

    //inventario completo reporte
    public void generarInventarioCompleto() throws ExecutionException, InterruptedException {
        System.out.println("\nINVENTARIO COMPLETO\n");

        QuerySnapshot productosSnapshot = db.collection(COLLECTION_PRODUCTOS).get().get();

        if (productosSnapshot.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (QueryDocumentSnapshot doc : productosSnapshot.getDocuments()) {
            Productos producto = doc.toObject(Productos.class);
            producto.setProductoId(doc.getId());

            System.out.println("\nProducto: " + producto.getNombre());
            System.out.println("  ID: " + producto.getProductoId());
            System.out.println("  Tipo: " + producto.getTipo());
            System.out.println("  Metodo de Rotacion: " + producto.getMetodo_rotacion());
            System.out.println("  Stock Actual: " + producto.getStock_actual() + " unidades");
            System.out.println("  Stock Minimo: " + producto.getStock_minimo() + " unidades");
            System.out.println("  Stock Maximo: " + producto.getStock_maximo() + " unidades");
            System.out.println("  Proveedor ID: " + producto.getProveedorId());

            //obtener los lotes con metodo de rotacion
            if (tieneMetodoRotacion(producto.getMetodo_rotacion())) {
                QuerySnapshot lotesSnapshot = db.collection(COLLECTION_PRODUCTOS)
                        .document(producto.getProductoId())
                        .collection(COLLECTION_LOTES)
                        .get()
                        .get();

                if (!lotesSnapshot.isEmpty()) {
                    System.out.println("  Lotes disponibles:");
                    for (QueryDocumentSnapshot loteDoc : lotesSnapshot.getDocuments()) {
                        Lotes lote = loteDoc.toObject(Lotes.class);
                        System.out.println("    * Lote " + lote.getLoteId()
                                + " - Cantidad: " + lote.getCantidad()
                                + " - Vence: " + dateFormat.format(lote.getFecha_Vencimiento()));
                    }
                } else {
                    System.out.println("  Lotes: Sin lotes registrados");
                }
            }
            System.out.println("--------------------------------------------------------------------------------");
        }
    }

    //reporte productos por vencer
    public void generarProductosPorVencer() throws ExecutionException, InterruptedException {
        System.out.println("\nPRODUCTOS POR VENCER\n");

        Date hoy = new Date();
        Calendar cal = Calendar.getInstance();

        //fecha dentro de 30 dias
        cal.setTime(hoy);
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date dentro30Dias = cal.getTime();

        QuerySnapshot productosSnapshot = db.collection(COLLECTION_PRODUCTOS).get().get();

        List<String> productos7dias = new ArrayList<>();
        List<String> productos15dias = new ArrayList<>();
        List<String> productos30dias = new ArrayList<>();

        for (QueryDocumentSnapshot doc : productosSnapshot.getDocuments()) {
            Productos producto = doc.toObject(Productos.class);
            producto.setProductoId(doc.getId());

            if (tieneMetodoRotacion(producto.getMetodo_rotacion())) {
                QuerySnapshot lotesSnapshot = db.collection(COLLECTION_PRODUCTOS)
                        .document(producto.getProductoId())
                        .collection(COLLECTION_LOTES)
                        .get()
                        .get();

                for (QueryDocumentSnapshot loteDoc : lotesSnapshot.getDocuments()) {
                    Lotes lote = loteDoc.toObject(Lotes.class);
                    Date fechaVenc = lote.getFecha_Vencimiento();

                    if (fechaVenc != null && fechaVenc.after(hoy) && fechaVenc.before(dentro30Dias)) {
                        long diasRestantes = (fechaVenc.getTime() - hoy.getTime()) / (1000 * 60 * 60 * 24);

                        String info = producto.getNombre() + " - Lote: " + lote.getLoteId()
                                + " - Cantidad: " + lote.getCantidad()
                                + " - Vence: " + dateFormat.format(fechaVenc)
                                + " (" + diasRestantes + " dias)";

                        if (diasRestantes <= 7) {
                            productos7dias.add(info);
                        } else if (diasRestantes <= 15) {
                            productos15dias.add(info);
                        } else {
                            productos30dias.add(info);
                        }
                    }
                }
            }
        }

        System.out.println("\nCRITICO - Vencen en 7 dias o menos:");
        if (productos7dias.isEmpty()) {
            System.out.println("  Ninguno");
        } else {
            for (String prod : productos7dias) {
                System.out.println("  [URGENTE] " + prod);
            }
        }

        System.out.println("\nATENCION - Vencen entre 8 y 15 dias:");
        if (productos15dias.isEmpty()) {
            System.out.println("  Ninguno");
        } else {
            for (String prod : productos15dias) {
                System.out.println("  [ALERTA] " + prod);
            }
        }

        System.out.println("\nINFORMACION - Vencen entre 16 y 30 dias:");
        if (productos30dias.isEmpty()) {
            System.out.println("  Ninguno");
        } else {
            for (String prod : productos30dias) {
                System.out.println("  [INFO] " + prod);
            }
        }

    }

    //movimientos del mes
    public void generarMovimientosDelMes() throws ExecutionException, InterruptedException {
        System.out.println("\nMOVIMIENTOS DEL MES ACTUAL\n");

        //obtener fecha actual
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date primerDiaMes = cal.getTime();

        QuerySnapshot movimientosSnapshot = db.collection(COLLECTION_MOVIMIENTOS).get().get();

        int totalEntradas = 0;
        int totalSalidas = 0;
        int contadorEntradas = 0;
        int contadorSalidas = 0;

        for (QueryDocumentSnapshot doc : movimientosSnapshot.getDocuments()) {
            Movimientos mov = doc.toObject(Movimientos.class);
            mov.setMovimientoId(doc.getId());

            //filtro del mes actual
            if (mov.getFecha() != null && mov.getFecha().after(primerDiaMes)) {
                System.out.println("\nMovimiento ID: " + mov.getMovimientoId());
                System.out.println("  Tipo: " + mov.getTipo_movimiento().toUpperCase());
                System.out.println("  Producto ID: " + mov.getProductoId());
                System.out.println("  Cantidad: " + mov.getCantidad() + " unidades");
                System.out.println("  Algoritmo: " + mov.getAlgoritmo());
                System.out.println("  Fecha: " + dateFormat.format(mov.getFecha()));

                if (mov.getTipo_movimiento().equalsIgnoreCase("entrada")) {
                    totalEntradas += mov.getCantidad();
                    contadorEntradas++;
                } else {
                    totalSalidas += mov.getCantidad();
                    contadorSalidas++;
                }
            }
        }

        System.out.println("RESUMEN:");
        System.out.println("  Total Entradas: " + contadorEntradas + " movimientos - " + totalEntradas + " unidades");
        System.out.println("  Total Salidas: " + contadorSalidas + " movimientos - " + totalSalidas + " unidades");
    }

    //metodos de apoyo
    private boolean tieneMetodoRotacion(String metodoRotacion) {
        return metodoRotacion != null
                && (metodoRotacion.equalsIgnoreCase("FIFO")
                || metodoRotacion.equalsIgnoreCase("LIFO")
                || metodoRotacion.equalsIgnoreCase("DRIFO"));
    }

    //prueba para generar los tres reportes
    public void generarReportes() {
        try {
            System.out.println("\n\n");
            System.out.println("                         GENERANDO REPORTES");

            generarInventarioCompleto();
            Thread.sleep(500);

            generarProductosPorVencer();
            Thread.sleep(500);

            generarMovimientosDelMes();

            System.out.println("\n\nREPORTES GENERADOS EXITOSAMENTE\n");

        } catch (Exception e) {
            System.out.println("ERROR AL GENERAR REPORTES: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
