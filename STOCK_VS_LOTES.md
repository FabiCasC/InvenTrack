# 📦 Diferencia entre STOCK y LOTES

## 🔍 Conceptos Clave

### **STOCK** 
- Es el **número total** de unidades de un producto que tienes disponible
- Es un **solo valor** (ejemplo: 100 unidades)
- Se almacena directamente en el objeto `Productos` como `stock_actual`

### **LOTES**
- Son **grupos** del mismo producto que llegaron en **fechas diferentes**
- Cada lote tiene su **fecha de entrada** y **fecha de vencimiento**
- Se usan para aplicar **algoritmos de rotación** (FIFO, LIFO, DRIFO)

---

## 📊 Ejemplo Práctico

### Escenario: Leche Fresca

**STOCK TOTAL = 100 litros**

Pero esos 100 litros llegaron en 3 fechas diferentes:

- **Lote 1**: 30 litros que llegaron el 01/01/2025 → vencen 31/01/2025
- **Lote 2**: 40 litros que llegaron el 10/01/2025 → vencen 09/02/2025  
- **Lote 3**: 30 litros que llegaron el 20/01/2025 → vencen 19/02/2025

**Stock = 100** (30 + 40 + 30)
**Lotes = 3 grupos** con fechas diferentes

---

## 🎯 ¿Para qué sirven los LOTES?

Los lotes permiten aplicar **algoritmos de rotación** cuando hay una **salida** de inventario:

### FIFO (First In, First Out) - Para Perecibles
- **Se vende primero lo que entró primero**
- Del ejemplo: Si vendes 50 litros, se toman del Lote 1 (30) y del Lote 2 (20)
- **Razón**: Evita que los productos más antiguos se venzan

### LIFO (Last In, First Out) - Para No Perecibles
- **Se vende primero lo que entró al final**
- Del ejemplo: Si vendes 50 litros, se toman del Lote 3 (30) y del Lote 2 (20)
- **Razón**: Para productos que no se dañan (como materiales de construcción apilados)

### DRIFO (Date Rotation In, First Out) - Por fecha de vencimiento
- **Se vende primero lo que vence primero**
- Se ordena por fecha de vencimiento, no por entrada

---

## 💻 En el Código

### STOCK (en Productos.java)
```java
private int stock_actual;  // Número total: 100
```

### LOTES (en Lotes.java)
```java
private String loteId;           // "LOT-PROD001-001"
private int cantidad;            // 30 (parte del stock)
private Date fecha_Entrada;      // 01/01/2025
private Date fecha_Vencimiento;  // 31/01/2025
```

### Relación
```
Producto: Leche (Stock Total = 100)
├── Lote 1: 30 unidades, entrada 01/01
├── Lote 2: 40 unidades, entrada 10/01  
└── Lote 3: 30 unidades, entrada 20/01
```

---

## 🔄 Flujo en el Sistema

### Cuando hay una ENTRADA:
1. Se **aumenta el STOCK** del producto (ej: de 70 a 100)
2. Si el producto tiene método de rotación, se **crea un NUEVO LOTE** con esa cantidad
3. El lote tiene su fecha de entrada y vencimiento

### Cuando hay una SALIDA:
1. Se **verifica el STOCK** disponible (¿hay suficiente?)
2. Se aplica el **algoritmo de rotación** (FIFO/LIFO) usando los **LOTES**
3. Se **disminuye el STOCK** del producto
4. Se actualizan o eliminan los **LOTES** según corresponda

---

## ✅ Resumen

| Aspecto | STOCK | LOTES |
|---------|-------|-------|
| **¿Qué es?** | Número total de unidades | Grupos con fechas diferentes |
| **Tipo de dato** | Un solo número (int) | Lista de objetos Lotes |
| **Ejemplo** | 100 litros | 3 lotes: 30, 40, 30 |
| **¿Se actualiza?** | Sí, en cada movimiento | Sí, cuando hay rotación |
| **¿Para qué sirve?** | Saber cuánto tienes | Aplicar algoritmos FIFO/LIFO |
| **¿Obligatorio?** | Sí, siempre | Solo si tiene método de rotación |

---

## 🎓 Analogía

Piensa en una **biblioteca**:

- **STOCK** = Total de libros en la biblioteca (ej: 1000 libros)
- **LOTES** = Libros que llegaron en diferentes fechas
  - Lote de enero: 200 libros
  - Lote de febrero: 300 libros
  - Lote de marzo: 500 libros
  
Si aplicas FIFO, prestas primero los libros que llegaron en enero.

---

## 🔑 Puntos Clave

1. **Stock es el total**, lotes son las partes
2. **Stock siempre existe**, lotes solo si hay rotación (FIFO/LIFO)
3. **Lotes permiten rastrear** qué unidades entraron primero/último
4. **Lotes permiten aplicar** algoritmos de rotación automáticamente
5. **Suma de lotes = Stock total** del producto

