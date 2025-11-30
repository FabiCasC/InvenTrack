package app.model.estructuras;

import app.model.Lotes;

/**
 * Estructura de Datos: Cola Doble (Deque - Double Ended Queue)
 * Algoritmo soportado: DRIFO (Dynamic Removal In First Out)
 * * Esta estructura permite insertar y eliminar elementos tanto por el frente
 * como por el final, permitiendo flexibilidad total para gestionar lotes
 * por fecha de vencimiento.
 */
public class ColaDobleDrifo {

    // ==========================================
    // CLASE INTERNA: NODO DOBLEMENTE ENLAZADO
    // ==========================================
    private static class NodoDoble {
        Lotes lote;
        NodoDoble siguiente; // Puntero hacia adelante
        NodoDoble anterior;  // Puntero hacia atrás

        public NodoDoble(Lotes lote) {
            this.lote = lote;
            this.siguiente = null;
            this.anterior = null;
        }
    }

    // ==========================================
    // ATRIBUTOS DE LA COLA DOBLE
    // ==========================================
    private NodoDoble frente;      // Cabeza de la cola (Head)
    private NodoDoble finalCola;   // Cola de la estructura (Tail)
    private int tamano;

    public ColaDobleDrifo() {
        this.frente = null;
        this.finalCola = null;
        this.tamano = 0;
    }

    // ==========================================
    // MÉTODOS DE INSERCIÓN
    // ==========================================

    /**
     * Inserta un lote al inicio de la estructura.
     */
    public void insertarFrente(Lotes lote) {
        NodoDoble nuevo = new NodoDoble(lote);

        if (estaVacia()) {
            frente = nuevo;
            finalCola = nuevo;
        } else {
            nuevo.siguiente = frente; // El nuevo apunta al antiguo frente
            frente.anterior = nuevo;  // El antiguo frente apunta atrás al nuevo
            frente = nuevo;           // Actualizamos el puntero frente
        }
        tamano++;
    }

    /**
     * Inserta un lote al final de la estructura.
     * (Comúnmente usado al cargar la lista ordenada por vencimiento)
     */
    public void insertarFinal(Lotes lote) {
        NodoDoble nuevo = new NodoDoble(lote);

        if (estaVacia()) {
            frente = nuevo;
            finalCola = nuevo;
        } else {
            finalCola.siguiente = nuevo; // El antiguo final apunta al nuevo
            nuevo.anterior = finalCola;  // El nuevo apunta atrás al antiguo final
            finalCola = nuevo;           // Actualizamos el puntero final
        }
        tamano++;
    }

    // ==========================================
    // MÉTODOS DE ELIMINACIÓN
    // ==========================================

    /**
     * Elimina y devuelve el lote del frente.
     * (Usado para sacar lo que vence más pronto)
     */
    public Lotes eliminarFrente() {
        if (estaVacia()) return null;

        Lotes loteSalida = frente.lote;

        if (frente == finalCola) { // Caso: Solo había un elemento
            frente = null;
            finalCola = null;
        } else {
            frente = frente.siguiente; // Movemos frente al segundo
            frente.anterior = null;    // Rompemos el enlace hacia atrás
        }
        tamano--;
        return loteSalida;
    }

    /**
     * Elimina y devuelve el lote del final.
     * (Usado si se requiere estrategia LIFO o sacar lo más fresco)
     */
    public Lotes eliminarFinal() {
        if (estaVacia()) return null;

        Lotes loteSalida = finalCola.lote;

        if (frente == finalCola) { // Caso: Solo había un elemento
            frente = null;
            finalCola = null;
        } else {
            finalCola = finalCola.anterior; // Retrocedemos al penúltimo
            finalCola.siguiente = null;     // Rompemos el enlace hacia adelante
        }
        tamano--;
        return loteSalida;
    }

    // ==========================================
    // MÉTODOS DE CONSULTA Y UTILIDAD
    // ==========================================

    public Lotes verFrente() {
        return estaVacia() ? null : frente.lote;
    }

    public Lotes verFinal() {
        return estaVacia() ? null : finalCola.lote;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamaño() {
        return tamano;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ColaDRIFO [ ");
        NodoDoble actual = frente;
        while (actual != null) {
            sb.append(actual.lote.toString()).append(" <-> ");
            actual = actual.siguiente;
        }
        sb.append("NULL ]");
        return sb.toString();
    }
}
