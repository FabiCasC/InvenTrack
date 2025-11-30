package app.model.estructuras;

import app.model.Lotes;

public class ColaDoble {
    private NodoDoble frente;  // Cabeza
    private NodoDoble finalCola; // Cola
    private int tamano;

    public ColaDoble() {
        this.frente = null;
        this.finalCola = null;
        this.tamano = 0;
    }

    // --- MÉTODOS DE INSERCIÓN ---

    public void insertarFrente(Lotes lote) {
        NodoDoble nuevo = new NodoDoble(lote);
        if (estaVacia()) {
            frente = nuevo;
            finalCola = nuevo;
        } else {
            nuevo.setSiguiente(frente);
            frente.setAnterior(nuevo);
            frente = nuevo;
        }
        tamano++;
    }

    public void insertarFinal(Lotes lote) {
        NodoDoble nuevo = new NodoDoble(lote);
        if (estaVacia()) {
            frente = nuevo;
            finalCola = nuevo;
        } else {
            finalCola.setSiguiente(nuevo);
            nuevo.setAnterior(finalCola);
            finalCola = nuevo;
        }
        tamano++;
    }

    // --- MÉTODOS DE ELIMINACIÓN ---

    public Lotes eliminarFrente() {
        if (estaVacia()) return null;

        Lotes lote = frente.getLote();
        
        if (frente == finalCola) { // Solo había uno
            frente = null;
            finalCola = null;
        } else {
            frente = frente.getSiguiente();
            frente.setAnterior(null); // Romper referencia hacia atrás
        }
        tamano--;
        return lote;
    }

    public Lotes eliminarFinal() {
        if (estaVacia()) return null;

        Lotes lote = finalCola.getLote();

        if (frente == finalCola) { // Solo había uno
            frente = null;
            finalCola = null;
        } else {
            finalCola = finalCola.getAnterior();
            finalCola.setSiguiente(null); // Romper referencia hacia adelante
        }
        tamano--;
        return lote;
    }

    // --- MÉTODOS DE CONSULTA ---

    public Lotes verFrente() {
        return estaVacia() ? null : frente.getLote();
    }

    public Lotes verFinal() {
        return estaVacia() ? null : finalCola.getLote();
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamaño() {
        return tamano;
    }
}
