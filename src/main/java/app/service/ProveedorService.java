/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.service;

import app.model.Proveedores;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.util.List;



public class ProveedorService {
    public void mostrarProveedores() {
    try {
        Firestore bd = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = bd.collection("Proveedores").get();
        List<QueryDocumentSnapshot> documentos = future.get().getDocuments();

        for (QueryDocumentSnapshot d : documentos) {

            // convierte el documento a objeto java para usar los atributos
            Proveedores p = d.toObject(Proveedores.class);

            // actualiza id del proveedor al atributo en java
            p.setProveedorId(d.getId());

            // mostrar datos
            System.out.println("ID: " + p.getProveedorId());
            System.out.println("Nombre: " + p.getNombre());
            System.out.println("Teléfono: " + p.getTelefono());
            System.out.println("Email: " + p.getEmail());
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
