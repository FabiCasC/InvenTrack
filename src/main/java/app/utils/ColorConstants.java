package app.utils;

import java.awt.Color;

/**
 * Paleta de colores "Tech Enterprise" para la aplicación InvenTrack
 * Mantiene consistencia visual en toda la aplicación
 */
public class ColorConstants {
    
    // ===== PALETA PRINCIPAL: TECH ENTERPRISE =====
    
    /** Azul Profundo - Primario (Sidebar/Headers) */
    public static final Color AZUL_PROFUNDO = new Color(0x1E3A8A);
    
    /** Azul Acero - Secundario (Botones/Selecciones) */
    public static final Color AZUL_ACERO = new Color(0x3B82F6);
    
    /** Blanco Humo - Fondo general */
    public static final Color BLANCO_HUMO = new Color(0xF3F4F6);
    
    /** Gris Pizarra - Texto general */
    public static final Color GRIS_PIZARRA = new Color(0x1F2937);
    
    /** Blanco puro - Contenedores/Tablas */
    public static final Color BLANCO_PURO = new Color(0xFFFFFF);
    
    /** Gris claro - Líneas divisorias */
    public static final Color GRIS_CLARO = new Color(0xE5E7EB);
    
    /** Gris tenue - Filas alternadas */
    public static final Color GRIS_TENUE = new Color(0xF9FAFB);
    
    // ===== PALETA FUNCIONAL: ALGORITMOS =====
    
    /** Verde Esmeralda - FIFO (Perecibles) */
    public static final Color VERDE_ESMERALDA = new Color(0x10B981);
    
    /** Naranja Ámbar - LIFO (No Perecibles) */
    public static final Color NARANJA_AMBAR = new Color(0xF59E0B);
    
    /** Violeta/Púrpura - DRIFO (Mixto) */
    public static final Color VIOLETA = new Color(0x8B5CF6);
    
    /** Cian - Listas Enlazadas (Historial) */
    public static final Color CIAN = new Color(0x06B6D4);
    
    // ===== PALETA DE ALERTAS =====
    
    /** Rojo Alerta - Stock Crítico/Vencido */
    public static final Color ROJO_ALERTA = new Color(0xEF4444);
    
    /** Amarillo Advertencia - Stock Bajo/Riesgo Medio */
    public static final Color AMARILLO_ADVERTENCIA = new Color(0xFBBF24);
    
    /** Verde Éxito - Stock Saludable/Predicción Estable */
    public static final Color VERDE_EXITO = new Color(0x22C55E);
    
    /** Gris Neutro - Stock Inactivo/Cancelado */
    public static final Color GRIS_NEUTRO = new Color(0x9CA3AF);
    
    // ===== BOTONES DE ACCIÓN =====
    
    /** Azul - Editar */
    public static final Color BOTON_EDITAR = AZUL_ACERO; // #3B82F6
    
    /** Rojo - Eliminar */
    public static final Color BOTON_ELIMINAR = ROJO_ALERTA; // #EF4444
    
    /** Violeta - Ver Gráfico/Árbol */
    public static final Color BOTON_GRAFICO = VIOLETA; // #8B5CF6
    
    // ===== COLORES ADICIONALES ÚTILES =====
    
    /** Gris para texto secundario */
    public static final Color GRIS_TEXTO_SECUNDARIO = new Color(0x6B7280);
    
    /** Gris muy claro para fondos de paneles */
    public static final Color GRIS_MUY_CLARO = new Color(0xF9FAFB);
    
    /** Azul hover (más oscuro que azul acero) */
    public static final Color AZUL_HOVER = new Color(0x2563EB);
    
    /** Sidebar más oscuro */
    public static final Color SIDEBAR_FONDO = AZUL_PROFUNDO;
    
    /** Sidebar hover */
    public static final Color SIDEBAR_HOVER = new Color(0x1E40AF);
}

