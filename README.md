# InvenTrack - Sistema de Inventario Inteligente

## Requisitos previos

- **Java JDK 17** o superior
- **Apache Maven** 3.6 o superior
- **Firebase** configurado (archivo de credenciales)

## Verificación de requisitos

Verifica que tienes instalado Java 17:

```bash
java -version
```

Verifica que tienes Maven instalado:

```bash
maven -version
```

## Configuración de Firebase

1. Asegúrate de que el archivo `firebase-credentials.json` esté en una de las siguientes ubicaciones:
   - `src/main/java/assets/firebase/firebase-credentials.json`
   - `src/main/java/assets/firabase/firebase-credentials.json` (nota: hay un typo en "firabase")
   - `src/main/resources/assets/firebase/firebase-credentials.json`
   - En la raíz del proyecto: `firebase-credentials.json`

## Cómo ejecutar el proyecto

### Opción 1: Ejecutar con Maven (recomendado)

Desde la raíz del proyecto, ejecuta:

```bash
mvn clean compile exec:java -Dexec.mainClass="app.Main"
```

O simplemente:

```bash
mvn exec:java
```

### Opción 2: Compilar y ejecutar manualmente

1. **Compilar el proyecto:**

```bash
mvn clean compile
```

2. **Ejecutar desde la clase compilada:**

```bash
java -cp "target/classes;target/dependency/*" app.Main
```

**Nota para Windows:** Si estás en Windows y el comando anterior no funciona, usa:

```bash
java -cp "target/classes;target\dependency\*" app.Main
```

### Opción 3: Empaquetar y ejecutar JAR

1. **Crear el JAR ejecutable:**

```bash
mvn clean package
```

2. **Ejecutar el JAR:**

```bash
java -jar target/ERPInteligenteInventario-1.0-SNAPSHOT.jar
```

**Nota:** Para que el JAR sea ejecutable, es necesario configurar el plugin `maven-assembly-plugin` o `maven-shade-plugin` en el `pom.xml`.

### Opción 4: Desde un IDE (NetBeans, IntelliJ, Eclipse)

#### NetBeans:
1. Abre el proyecto en NetBeans
2. Click derecho en el proyecto → "Run" o presiona F6

#### IntelliJ IDEA:
1. Abre el proyecto en IntelliJ
2. Ve a `Run` → `Edit Configurations`
3. Agrega una nueva configuración "Application"
4. Clase principal: `app.Main`
5. Haz clic en "Run" o presiona Shift+F10

#### Eclipse:
1. Importa el proyecto como proyecto Maven
2. Click derecho en `Main.java` → `Run As` → `Java Application`

## Solución de problemas

### Error: "No se encontró el archivo de credenciales de Firebase"
- Verifica que el archivo `firebase-credentials.json` esté en una de las rutas mencionadas arriba
- Verifica que el archivo tenga permisos de lectura

### Error: "Java version"
- Asegúrate de tener Java 17 instalado
- Verifica que JAVA_HOME esté configurado correctamente

### Error: "Maven no encontrado"
- Instala Maven o agrega Maven al PATH de tu sistema
- Verifica con `mvn -version`

### Error de dependencias
Si Maven no puede descargar las dependencias:

```bash
mvn clean install -U
```

El flag `-U` fuerza a Maven a actualizar las dependencias.

## Estructura del proyecto

```
InvenTrack/
├── pom.xml                 # Configuración de Maven
├── src/
│   └── main/
│       ├── java/
│       │   └── app/
│       │       ├── Main.java          # Punto de entrada
│       │       ├── config/            # Configuración (Firebase)
│       │       ├── controller/        # Controladores MVC
│       │       ├── model/             # Modelos de datos
│       │       ├── repository/        # Repositorios de datos
│       │       ├── service/           # Lógica de negocio
│       │       ├── utils/             # Utilidades
│       │       └── view/              # Vistas Swing
│       └── resources/
│           └── assets/
│               └── firebase/
│                   └── firebase-credentials.json
└── target/                # Archivos compilados (generado)
```

## Contacto

Para más información sobre la integración MVC, consulta: `DOC_ INTEGRACION MVC.md`

