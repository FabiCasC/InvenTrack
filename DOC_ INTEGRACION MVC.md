# Integración MVC: UI → Controller → Service → Repository

Este documento describe la arquitectura y el flujo de integración implementado en InvenTrack siguiendo el patrón MVC (Model-View-Controller) con separación en capas.

## Arquitectura General

```
┌─────────────┐
│     VIEW    │  (Interfaz Gráfica - Swing)
│  (UI/Forms) │
└──────┬──────┘
       │
       ↓
┌─────────────┐
│  CONTROLLER │  (Controla el flujo y valida entrada)
└──────┬──────┘
       │
       ↓
┌─────────────┐
│   SERVICE   │  (Lógica de negocio y validaciones)
└──────┬──────┘
       │
       ↓
┌─────────────┐
│ REPOSITORY  │  (Acceso a datos - Firestore)
└──────┬──────┘
       │
       ↓
┌─────────────┐
│  FIRESTORE  │  (Base de datos)
└─────────────┘
```

## Flujo de Datos

### 1. View (Vista/Interfaz)
- **Responsabilidad**: Capturar entrada del usuario y mostrar datos
- **Ubicación**: `app.view.*`
- **Ejemplo**: `LoginView`, `DashboardView`, `RegistroView`
- **Acciones**:
  - Captura eventos del usuario (clics, texto, etc.)
  - Llama a los métodos del Controller
  - Muestra resultados (mensajes, tablas, etc.)

**Ejemplo:**
```java
// En LoginView.java
private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {
    String email = txtEmail.getText();
    String contraseña = new String(txtContraseña.getPassword());
    
    LoginController controller = new LoginController();
    Usuarios usuario = controller.iniciarSesion(email, contraseña);
    
    if (usuario != null) {
        // Navegar al dashboard
    }
}
```

### 2. Controller (Controlador)
- **Responsabilidad**: 
  - Validar datos de entrada
  - Orquestar llamadas a Services
  - Manejar mensajes de error/éxito al usuario
- **Ubicación**: `app.controller.*`
- **Ejemplo**: `LoginController`, `ProductoController`, `MovimientoController`

**Características**:
- No contiene lógica de negocio
- No accede directamente a la base de datos
- Maneja la interacción entre View y Service
- Presenta mensajes al usuario (JOptionPane)

**Ejemplo:**
```java
// En ProductoController.java
public boolean registrarProducto(String nombre, String tipo, ...) {
    try {
        // Validación básica de entrada
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }
        
        // Crear objeto modelo
        Productos producto = new Productos();
        producto.setNombre(nombre);
        // ...
        
        // Delegar a Service
        Productos productoRegistrado = productoService.registrarProducto(producto, loteInicial);
        
        // Mostrar resultado
        mostrarExito("Producto registrado: " + productoRegistrado.getProductoId());
        return true;
        
    } catch (Exception e) {
        mostrarError(e.getMessage());
        return false;
    }
}
```

### 3. Service (Servicio - Lógica de Negocio)
- **Responsabilidad**:
  - Implementar reglas de negocio
  - Validar reglas de dominio
  - Orquestar múltiples repositories si es necesario
  - Manejar transacciones lógicas
- **Ubicación**: `app.service.*`
- **Ejemplo**: `ProductoService`, `AuthService`, `MovimientoService`

**Características**:
- Contiene la lógica de negocio compleja
- Puede usar múltiples repositories
- Realiza validaciones de reglas de negocio
- Maneja excepciones de negocio

**Ejemplo:**
```java
// En ProductoService.java
public Productos registrarProducto(Productos producto, Lotes loteInicial) {
    // Validación de reglas de negocio
    validarProducto(producto);
    
    // Verificar dependencias
    if (!proveedorRepository.existsById(producto.getProveedorId())) {
        throw new IllegalArgumentException("El proveedor no existe");
    }
    
    // Usar Repository para persistir
    productoRepository.save(producto);
    
    // Lógica adicional (lotes, etc.)
    if (tieneMetodoRotacion(producto.getMetodo_rotacion()) && loteInicial != null) {
        registrarLoteInicial(producto.getProductoId(), loteInicial);
    }
    
    return producto;
}
```

### 4. Repository (Repositorio - Acceso a Datos)
- **Responsabilidad**:
  - Abstraer el acceso a Firestore
  - Implementar operaciones CRUD
  - Convertir entre objetos del dominio y documentos de Firestore
- **Ubicación**: `app.repository.*`
- **Ejemplo**: `ProductoRepository`, `UsuarioRepository`, `MovimientoRepository`

**Características**:
- Solo conoce Firestore y los modelos
- No contiene lógica de negocio
- Métodos simples: save, findById, findAll, update, delete
- Maneja mapeo entre objetos Java y documentos Firestore

**Ejemplo:**
```java
// En ProductoRepository.java
public String save(Productos producto) throws ExecutionException, InterruptedException {
    if (producto.getProductoId() == null || producto.getProductoId().trim().isEmpty()) {
        producto.setProductoId(generateProductoId());
    }
    
    Map<String, Object> productoData = new HashMap<>();
    productoData.put("nombre", producto.getNombre());
    // ... mapear todos los campos
    
    db.collection(COLLECTION_NAME)
            .document(producto.getProductoId())
            .set(productoData)
            .get();
    
    return producto.getProductoId();
}
```

## Ejemplos Completos de Flujo

### Ejemplo 1: Registrar un Producto

```
1. Usuario llena formulario en ProductoView
   ↓
2. ProductoView llama a: controller.registrarProducto(...)
   ↓
3. ProductoController valida entrada y crea objeto Productos
   ↓
4. ProductoController llama a: productoService.registrarProducto(...)
   ↓
5. ProductoService valida reglas de negocio
   ↓
6. ProductoService usa: productoRepository.save(...)
   ↓
7. ProductoRepository guarda en Firestore
   ↓
8. Respuesta fluye de vuelta: Repository → Service → Controller → View
   ↓
9. View muestra mensaje de éxito/error
```

### Ejemplo 2: Iniciar Sesión

```
1. Usuario ingresa email y contraseña en LoginView
   ↓
2. LoginView llama a: controller.iniciarSesion(email, contraseña)
   ↓
3. LoginController valida que campos no estén vacíos
   ↓
4. LoginController llama a: authService.login(email, contraseña)
   ↓
5. AuthService usa: usuarioRepository.findByEmail(email)
   ↓
6. UsuarioRepository consulta Firestore
   ↓
7. AuthService valida contraseña
   ↓
8. Retorna Usuario o lanza excepción
   ↓
9. Controller maneja resultado y muestra mensaje
   ↓
10. View navega al Dashboard si login es exitoso
```

## Estructura de Paquetes

```
app/
├── view/           # Interfaces gráficas (Swing)
├── controller/     # Controladores MVC
├── service/        # Lógica de negocio
├── repository/     # Acceso a datos (Firestore)
├── model/          # Modelos de dominio (POJOs)
├── config/         # Configuración (Firebase)
└── utils/          # Utilidades
```

## Controllers Disponibles

- **LoginController**: Autenticación de usuarios
- **RegistroController**: Registro de nuevos usuarios
- **ProductoController**: Gestión de productos (CRUD)
- **MovimientoController**: Gestión de movimientos de inventario
- **PedidoController**: Gestión de pedidos
- **ProveedorController**: Gestión de proveedores

## Services Disponibles

- **AuthService**: Autenticación y autorización
- **ProductoService**: Lógica de negocio de productos
- **MovimientoService**: Lógica de movimientos y rotación (FIFO/LIFO)
- **PedidoService**: Lógica de pedidos y control de stock
- **ProveedorService**: Lógica de proveedores
- **UsuarioService**: Gestión de usuarios
- **ReporteService**: Generación de reportes

## Repositories Disponibles

- **UsuarioRepository**: CRUD de usuarios
- **ProductoRepository**: CRUD de productos
- **MovimientoRepository**: CRUD de movimientos
- **PedidoRepository**: CRUD de pedidos
- **ProveedorRepository**: CRUD de proveedores

## Beneficios de esta Arquitectura

1. **Separación de Responsabilidades**: Cada capa tiene una responsabilidad clara
2. **Mantenibilidad**: Fácil de modificar y extender
3. **Testabilidad**: Cada capa puede probarse independientemente
4. **Reutilización**: Services y Repositories pueden reutilizarse
5. **Escalabilidad**: Fácil agregar nuevas funcionalidades

## Convenciones y Mejores Prácticas

1. **Controllers**: 
   - Siempre validan entrada básica
   - Manejan mensajes al usuario
   - No contienen lógica de negocio compleja

2. **Services**:
   - Contienen toda la lógica de negocio
   - Pueden usar múltiples repositories
   - Lanzan excepciones descriptivas

3. **Repositories**:
   - Solo operaciones CRUD
   - No lógica de negocio
   - Abstracción del acceso a datos

4. **Manejo de Errores**:
   - Services lanzan excepciones
   - Controllers capturan y muestran mensajes
   - Repositories propagan excepciones de Firestore

## Notas Importantes

- Los Services actuales (`ProductoService`, `MovimientoService`, `PedidoService`) aún tienen acceso directo a Firestore para operaciones complejas (lotes, algoritmos de rotación)
- Los Repositories están siendo integrados progresivamente
- La migración completa a Repositories se puede hacer gradualmente sin romper funcionalidad existente

