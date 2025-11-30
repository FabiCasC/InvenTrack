package app.model.estructuras;

import app.model.Lotes;

public class NodoDoble {
    private Lotes lote;
    private NodoDoble siguiente; // El que sigue
    private NodoDoble anterior;  // El que está detrás

    public NodoDoble(Lotes lote) {
        this.lote = lote;
        this.siguiente = null;
        this.anterior = null;
    }

    public Lotes getLote() { return lote; }
    public void setLote(Lotes lote) { this.lote = lote; }
    
    public NodoDoble getSiguiente() { return siguiente; }
    public void setSiguiente(NodoDoble siguiente) { this.siguiente = siguiente; }
    
    public NodoDoble getAnterior() { return anterior; }
    public void setAnterior(NodoDoble anterior) { this.anterior = anterior; }
}
