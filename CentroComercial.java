import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Scanner;

public class CentroComercial {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    
    private static MongoCollection<Document> coleccionEmpleados;
    private static MongoCollection<Document> coleccionCamaras;
    private static MongoCollection<Document> coleccionTransacciones;
    private static Scanner scanner = new Scanner(System.in);

    // =========================================================
    //   PALETA DE COLORES ANSI
    // =========================================================
    public static final String RESET = "\u001B[0m";
    public static final String NEGRITA = "\u001B[1m";
    
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String MORADO = "\u001B[35m";
    public static final String CIAN = "\u001B[36m";
    public static final String BLANCO = "\u001B[37m";
    
    public static final String FONDO_ROJO = "\u001B[41m";
    public static final String FONDO_VERDE = "\u001B[42m";

    public static void main(String[] args) {
        limpiarPantalla();
        mostrarLogoNexus();
        
        System.out.print(NEGRITA + CIAN + "[SISTEMA] Conectando a la infraestructura central... " + RESET);
        
        try {
            MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("CentroComercialDB");
            
            coleccionEmpleados = database.getCollection("empleados");
            coleccionCamaras = database.getCollection("camaras");
            coleccionTransacciones = database.getCollection("transacciones");
            
            System.out.println(NEGRITA + VERDE + "[ONLINE]" + RESET);
            simularBarraCarga("Cargando base de datos", 5);
            
            configurarDatosIniciales();
            menuPrincipal();

        } catch (Exception e) {
            System.out.println(NEGRITA + ROJO + "[OFFLINE]" + RESET);
            System.out.println("\n" + ROJO + NEGRITA + "┌──────────────────────────────────────────────────────────┐");
            System.out.println("│               ERROR CRÍTICO DE CONEXIÓN                  │");
            System.out.println("├──────────────────────────────────────────────────────────┤");
            System.out.println("│ No se pudo establecer enlace con el servidor MongoDB.    │");
            System.out.println("│ Verifique que el servicio local o Atlas esté activo.     │");
            System.out.println("└──────────────────────────────────────────────────────────┘" + RESET);
        }
    }

    // =========================================================
    //   HERRAMIENTAS DE CONTROL, TIEMPO Y ALINEACIÓN RECTA
    // =========================================================
    private static void limpiarPantalla() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    private static void esperarYLimpiar() {
        try {
            Thread.sleep(2200); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        limpiarPantalla();
    }

    private static void presionarEnterParaContinuar() {
        System.out.print(NEGRITA + AMARILLO + "\n Presione ENTER para regresar al menú..." + RESET);
        scanner.nextLine();
    }

    private static void simularBarraCarga(String mensaje, int iteraciones) {
        System.out.print(NEGRITA + BLANCO + " " + mensaje + " [");
        for (int i = 0; i < iteraciones; i++) {
            try { Thread.sleep(150); } catch (Exception e) {}
            System.out.print(VERDE + "■");
        }
        System.out.println(BLANCO + "] Exitoso." + RESET);
        try { Thread.sleep(400); } catch (Exception e) {}
        limpiarPantalla();
    }

    // Método mágico: Rellena con espacios exactos ignorando caracteres invisibles de color
    private static String ajustarContenido(String textoVisible, int anchoMaximo) {
        int longitudReal = textoVisible.replaceAll("\\u001B\\[[;\\d]*m", "").length();
        int espaciosNecesarios = anchoMaximo - longitudReal;
        if (espaciosNecesarios < 0) espaciosNecesarios = 0;
        return textoVisible + " ".repeat(espaciosNecesarios);
    }

    // =========================================================
    //   LOGOTIPO E INICIALIZACIÓN
    // =========================================================
    private static void mostrarLogoNexus() {
        System.out.println(NEGRITA + MORADO + "======================================================================");
        System.out.println(" ███╗   ██╗███████╗██╗  ██╗██╗   ██╗███████╗   ██████╗ ██████╗ ███╗   ███╗");
        System.out.println(" ████╗  ██║██╔════╝╚██╗██╔╝██║   ██║██╔════╝  ██╔════╝██╔═══██╗████╗ ████║");
        System.out.println(" ██╔██╗ ██║█████╗   ╚███╔╝ ██║   ██║███████╗  ██║     ██║   ██║██╔████╔██║");
        System.out.println(" ██║╚██╗██║██╔══╝   ██╔██╗ ██║   ██║╚════██║  ██║     ██║   ██║██║╚██╔╝██║");
        System.out.println(" ██║ ╚████║███████╗██╔╝ ██╗╚██████╔╝███████║  ╚██████╗╚██████╔╝██║ ╚═╝ ██║");
        System.out.println(" ╚═╝  ╚═══╝╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝   ╚═════╝ ╚═════╝ ╚═╝     ╚═╝");
        System.out.println("                      P R O Y E C T O   C O M E R C I A L             ");
        System.out.println("======================================================================" + RESET);
    }

    private static void configurarDatosIniciales() {
        if (coleccionEmpleados.countDocuments() == 0) {
            coleccionEmpleados.insertOne(new Document("usuario", "admin").append("contrasena", "admin123").append("rol", "Administrador"));
            coleccionEmpleados.insertOne(new Document("usuario", "guardia1").append("contrasena", "123").append("rol", "Guardia"));
            coleccionEmpleados.insertOne(new Document("usuario", "gerente1").append("contrasena", "123").append("rol", "Gerente"));
        }
        if (coleccionCamaras.countDocuments() == 0) {
            coleccionCamaras.insertOne(new Document("zona", "Pasillos").append("estado", "OPERANDO").append("alerta", "Ninguna"));
            coleccionCamaras.insertOne(new Document("zona", "Banco").append("estado", "OPERANDO").append("alerta", "Ninguna"));
            coleccionCamaras.insertOne(new Document("zona", "Área de Comida").append("estado", "OPERANDO").append("alerta", "Ninguna"));
            coleccionCamaras.insertOne(new Document("zona", "Cine").append("estado", "FALLA").append("alerta", "Revisar cableado de datos"));
        }
        if (coleccionTransacciones.countDocuments() == 0) {
            coleccionTransacciones.insertOne(new Document("tipo", "Depósito de Valores").append("monto", 500000.0).append("fecha", "22/05/2026 10:00").append("estado", "Seguro"));
            coleccionTransacciones.insertOne(new Document("tipo", "Retiro Cajeros").append("monto", 4500.0).append("fecha", "22/05/2026 11:15").append("estado", "Seguro"));
            coleccionTransacciones.insertOne(new Document("tipo", "Apertura Bóveda").append("monto", 0.0).append("fecha", "22/05/2026 14:30").append("estado", "Riesgo: Acceso No Autorizado"));
        }
    }

    // =========================================================
    //   MENÚS DE INTERFAZ DEL SISTEMA
    // =========================================================
    private static void menuPrincipal() {
        int opcion = 0;
        do {
            System.out.println("\n" + NEGRITA + AZUL + "┌──────────────────────────────────────────────────────────┐");
            System.out.println("│            " + CIAN + "NEXUS - CONTROL DE SEGURIDAD MÁSTER" + AZUL + "           │");
            System.out.println("├──────────────────────────────────────────────────────────┤");
            System.out.println("│  " + AMARILLO + "[1]" + BLANCO + " Iniciar Sesión en la Central                         " + AZUL + "│");
            System.out.println("│  " + AMARILLO + "[2]" + BLANCO + " Registrar Nuevo Personal                             " + AZUL + "│");
            System.out.println("│  " + ROJO + "[3]" + BLANCO + " Apagar Terminal Central                              " + AZUL + "│");
            System.out.println("└──────────────────────────────────────────────────────────┘" + RESET);
            System.out.print(NEGRITA + CIAN + " Digite su opción (1-3): " + RESET);
            
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("\n" + FONDO_ROJO + BLANCO + NEGRITA + " [AVISO] Entrada no válida. Use caracteres numéricos. " + RESET);
                scanner.nextLine();
                esperarYLimpiar();
                continue;
            }

            switch (opcion) {
                case 1: iniciarSesion(); break;
                case 2: registrarse(); break;
                case 3:
                    limpiarPantalla();
                    System.out.println("\n" + AMARILLO + "[NEXUS] Cerrando hilos de base de datos... Hecho." + RESET);
                    System.out.println(NEGRITA + VERDE + "[NEXUS] Terminal apagada correctamente. ¡Buen turno!" + RESET);
                    break;
                default:
                    System.out.println("\n" + ROJO + "[AVISO] Opción fuera del rango estipulado." + RESET);
                    esperarYLimpiar();
            }
        } while (opcion != 3);
    }

    private static void iniciarSesion() {
        limpiarPantalla();
        System.out.println("\n" + NEGRITA + CIAN + "┌──────────────────────────────────────────────────────────┐");
        System.out.println("│               MÓDULO DE AUTENTICACIÓN CONTROL            │");
        System.out.println("├──────────────────────────────────────────────────────────┤" + RESET);
        System.out.print(NEGRITA + BLANCO + "   Nombre de Usuario: " + RESET);
        String user = scanner.nextLine().trim();
        System.out.print(NEGRITA + BLANCO + "   Contraseña de Red: " + RESET);
        String pass = scanner.nextLine();
        System.out.println(NEGRITA + CIAN + "└──────────────────────────────────────────────────────────┘" + RESET);

        Document empleadoDoc = coleccionEmpleados.find(
            Filters.and(Filters.eq("usuario", user), Filters.eq("contrasena", pass))
        ).first();

        if (empleadoDoc != null) {
            String rol = empleadoDoc.getString("rol");
            System.out.println("\n" + VERDE + "[ACCESO CONCEDIDO]" + AMARILLO + " Autenticando token de red..." + RESET);
            simularBarraCarga("Desbloqueando perfil", 4);
            
            System.out.println(NEGRITA + VERDE + " ¡Bienvenid@ al sistema corporativo, " + user.toUpperCase() + "! (" + rol + ")" + RESET);
            try { Thread.sleep(1500); } catch (Exception e) {}
            limpiarPantalla();
            
            switch (rol) {
                case "Administrador": menuAdministrador(); break;
                case "Guardia": menuGuardia(); break;
                case "Gerente": menuGerente(); break;
            }
        } else {
            System.out.println("\n" + FONDO_ROJO + BLANCO + NEGRITA + " [DENEGADO] Las credenciales ingresadas son incorrectas. " + RESET);
            esperarYLimpiar();
        }
    }

    private static void registrarse() {
        limpiarPantalla();
        System.out.println("\n" + NEGRITA + MORADO + "┌──────────────────────────────────────────────────────────┐");
        System.out.println("│              ALTA DE PERSONAL - BASE DE DATOS            │");
        System.out.println("├──────────────────────────────────────────────────────────┤" + RESET);
        System.out.print(NEGRITA + BLANCO + "   Proponga un nombre de usuario: " + RESET);
        String user = scanner.nextLine().trim();

        Document existe = coleccionEmpleados.find(Filters.eq("usuario", user)).first();
        if (existe != null) {
            System.out.println(NEGRITA + MORADO + "└──────────────────────────────────────────────────────────┘" + RESET);
            System.out.println("\n" + ROJO + "[ERROR] Transacción cancelada. El usuario '" + user + "' ya está registrado." + RESET);
            esperarYLimpiar();
            return;
        }

        System.out.print(NEGRITA + BLANCO + "   Genere una contraseña segura:  " + RESET);
        String pass = scanner.nextLine();
        System.out.println(NEGRITA + MORADO + "├──────────────────────────────────────────────────────────┤");
        System.out.println("│  Seleccione el Cargo Operativo:                          │");
        System.out.println("│  " + AMARILLO + "[1]" + BLANCO + " Gerencia Corporativa                                 " + MORADO + "│");
        System.out.println("│  " + AMARILLO + "[2]" + BLANCO + " Administración Máster                                " + MORADO + "│");
        System.out.println("│  " + AMARILLO + "[3]" + BLANCO + " Guardia de Seguridad CCTV                            " + MORADO + "│");
        System.out.println("└──────────────────────────────────────────────────────────┘" + RESET);
        System.out.print(NEGRITA + CIAN + " Rol (1-3): " + RESET);
        
        int opcRol = scanner.nextInt();
        scanner.nextLine();

        String rol = "Empleado General";
        if (opcRol == 1) rol = "Gerente";
        if (opcRol == 2) rol = "Administrador";
        if (opcRol == 3) rol = "Guardia";

        Document nuevoEmpleado = new Document("usuario", user).append("contrasena", pass).append("rol", rol);
        coleccionEmpleados.insertOne(nuevoEmpleado);
        System.out.println("\n" + FONDO_VERDE + BLANCO + NEGRITA + " [ÉXITO] Base de datos actualizada. Alta procesada para [" + rol + "]. " + RESET);
        esperarYLimpiar();
    }

    private static void menuAdministrador() {
        int opc = 0;
        do {
            System.out.println("\n" + NEGRITA + ROJO + "┌──────────────────────────────────────────────────────────┐");
            System.out.println("│             " + AMARILLO + "INTERFAZ MÁSTER: ADMINISTRACIÓN" + ROJO + "              │");
            System.out.println("├──────────────────────────────────────────────────────────┤");
            System.out.println("│  " + CIAN + "[1]" + BLANCO + " Desplegar Plantilla Completa de Empleados          " + ROJO + "│");
            System.out.println("│  " + CIAN + "[2]" + BLANCO + " Revocar Contrato / Eliminar Personal               " + ROJO + "│");
            System.out.println("│  " + CIAN + "[3]" + BLANCO + " Enlace en Vivo: Sistema de Cámaras (CCTV)          " + ROJO + "│");
            System.out.println("│  " + ROJO + "[4]" + BLANCO + " Bloquear Consola (Cerrar Sesión)                   " + ROJO + "│");
            System.out.println("└──────────────────────────────────────────────────────────┘" + RESET);
            System.out.print(NEGRITA + CIAN + " Comando: " + RESET);
            opc = scanner.nextInt(); scanner.nextLine();

            switch (opc) {
                case 1: mostrarEmpleados(); presionarEnterParaContinuar(); limpiarPantalla(); break;
                case 2: eliminarEmpleado(); break;
                case 3: visualizarCamaras(); break;
                case 4: limpiarPantalla(); break;
            }
        } while (opc != 4);
    }

    private static void menuGuardia() {
        int opc = 0;
        do {
            System.out.println("\n" + NEGRITA + VERDE + "┌──────────────────────────────────────────────────────────┐");
            System.out.println("│           " + CIAN + "PANEL DE MONITOREO TÁCTICO: GUARDIA" + VERDE + "            │");
            System.out.println("├──────────────────────────────────────────────────────────┤");
            System.out.println("│  " + AMARILLO + "[1]" + BLANCO + " Inspeccionar Cámaras Perimetrales                    " + VERDE + "│");
            System.out.println("│  " + AMARILLO + "[2]" + BLANCO + " Registrar Incidencia / Falla de Video                " + VERDE + "│");
            System.out.println("│  " + ROJO + "[3]" + BLANCO + " Cerrar Sesión de Seguridad                           " + VERDE + "│");
            System.out.println("└──────────────────────────────────────────────────────────┘" + RESET);
            System.out.print(NEGRITA + CIAN + " Comando: " + RESET);
            opc = scanner.nextInt(); scanner.nextLine();

            switch (opc) {
                case 1: visualizarCamaras(); break;
                case 2: reportarFallaCamara(); break;
                case 3: limpiarPantalla(); break;
            }
        } while (opc != 3);
    }

    private static void menuGerente() {
        int opc = 0;
        do {
            System.out.println("\n" + NEGRITA + AMARILLO + "┌──────────────────────────────────────────────────────────┐");
            System.out.println("│          " + MORADO + "PANEL FINANCIERO Y AUDITORÍA: GERENCIA" + AMARILLO + "          │");
            System.out.println("├──────────────────────────────────────────────────────────┤");
            System.out.println("│  " + CIAN + "[1]" + BLANCO + " Desplegar Libro de Transacciones (Banco)           " + AMARILLO + "│");
            System.out.println("│  " + CIAN + "[2]" + BLANCO + " Auditoría de Incidencias Críticas del Banco        " + AMARILLO + "│");
            System.out.println("│  " + ROJO + "[3]" + BLANCO + " Salir de la Cuenta Ejecutiva                       " + AMARILLO + "│");
            System.out.println("└──────────────────────────────────────────────────────────┘" + RESET);
            System.out.print(NEGRITA + CIAN + " Comando: " + RESET);
            opc = scanner.nextInt(); scanner.nextLine();

            switch (opc) {
                case 1: verMovimientosBanco(); break;
                case 2: verProblemasBanco(); break;
                case 3: limpiarPantalla(); break;
            }
        } while (opc != 3);
    }

    // =========================================================
    //   MÓDULOS DE VISTA E INFORMACIÓN (CUADROS PERFECTOS)
    // =========================================================
    private static void visualizarCamaras() {
        limpiarPantalla();
        System.out.println("\n" + NEGRITA + AZUL + "┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                      CIRCUITO CERRADO DE TELEVISIÓN (CCTV)                   │");
        System.out.println("├──────────────────────────────────────────────────────────────────────────────┤" + RESET);
        
        for (Document camara : coleccionCamaras.find()) {
            String zona = camara.getString("zona");
            String estadoRaw = camara.getString("estado").trim();
            String alerta = camara.getString("alerta");
            
            String estadoColor;
            if (estadoRaw.equalsIgnoreCase("OPERANDO")) {
                estadoColor = VERDE + "OPERANDO" + RESET;
            } else if (estadoRaw.equalsIgnoreCase("FALLA")) {
                estadoColor = ROJO + "FALLA   " + RESET;
            } else {
                estadoColor = AMARILLO + "ALERTADO" + RESET;
            }

            // Construimos la línea interna
            String col1 = " UBICACIÓN: " + CIAN + zona;
            String col2 = "│ ESTADO: " + estadoColor;
            String col3 = "│ NOTA: " + AMARILLO + alerta;

            // Rellenamos de manera controlada para que queden perfectas
            col1 = ajustarContenido(col1, 26);
            col2 = ajustarContenido(col2, 20);
            col3 = ajustarContenido(col3, 30);

            System.out.println(NEGRITA + AZUL + "│" + RESET + col1 + NEGRITA + AZUL + col2 + NEGRITA + AZUL + col3 + NEGRITA + AZUL + "│" + RESET);
        }
        System.out.println(NEGRITA + AZUL + "└──────────────────────────────────────────────────────────────────────────────┘" + RESET);
        presionarEnterParaContinuar();
        limpiarPantalla();
    }

    private static void reportarFallaCamara() {
        limpiarPantalla();
        System.out.print("\n Ingrese el sector de la cámara afectada (Pasillos/Banco/Área de Comida/Cine): ");
        String zona = scanner.nextLine().trim();
        
        Document camara = coleccionCamaras.find(Filters.eq("zona", zona)).first();
        if (camara != null) {
            System.out.print(" Redacte la anomalía observada en el sector: ");
            String problema = scanner.nextLine();
            
            coleccionCamaras.updateOne(Filters.eq("zona", zona),
                new Document("$set", new Document("estado", "ALERTADO").append("alerta", problema)));
            System.out.println("\n" + VERDE + "[BASE DE DATOS] Incidencia de seguridad vinculada y guardada." + RESET);
        } else {
            System.out.println("\n" + ROJO + "[ERROR] Error de mapeo. El sector '" + zona + "' no existe en el sistema." + RESET);
        }
        esperarYLimpiar(); 
    }

    private static void verMovimientosBanco() {
        limpiarPantalla();
        System.out.println("\n" + NEGRITA + MORADO + "┌─────────────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                            REGISTROS MONETARIOS DE AUDITORÍA                            │");
        System.out.println("├─────────────────────────────────────────────────────────────────────────────────────────┤" + RESET);
        
        for (Document tx : coleccionTransacciones.find()) {
            String fecha = tx.getString("fecha");
            String tipo = tx.getString("tipo");
            double monto = tx.getDouble("monto");
            String estado = tx.getString("estado");

            String txtMonto = (monto > 0) ? String.format("$%,.2f", monto) : "$0.00";

            String col1 = " " + CIAN + fecha;
            String col2 = "│ Detalle: " + BLANCO + tipo;
            String col3 = "│ Monto: " + VERDE + txtMonto;
            String col4 = "│ Obs: " + AMARILLO + estado;

            col1 = ajustarContenido(col1, 19);
            col2 = ajustarContenido(col2, 28);
            col3 = ajustarContenido(col3, 20);
            col4 = ajustarContenido(col4, 20);

            System.out.println(NEGRITA + MORADO + "│" + RESET + col1 + NEGRITA + MORADO + col2 + NEGRITA + MORADO + col3 + NEGRITA + MORADO + col4 + NEGRITA + MORADO + "│" + RESET);
        }
        System.out.println(NEGRITA + MORADO + "└─────────────────────────────────────────────────────────────────────────────────────────┘" + RESET);
        presionarEnterParaContinuar();
        limpiarPantalla();
    }

    private static void verProblemasBanco() {
        limpiarPantalla();
        System.out.println("\n" + NEGRITA + ROJO + "┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                     AUDITORÍA AUTOMÁTICA DE RIESGO DE BANCO                  │");
        System.out.println("├──────────────────────────────────────────────────────────────────────────────┤" + RESET);
        
        boolean incidencias = false;
        for (Document tx : coleccionTransacciones.find()) {
            String estado = tx.getString("estado");
            if (estado.toLowerCase().contains("riesgo") || estado.toLowerCase().contains("reporte")) {
                System.out.println(ROJO + NEGRITA + "   [!] ANOMALÍA FINANCIERA DETECTADA (" + tx.getString("fecha") + ")" + RESET);
                System.out.println("       Bóveda afectada: " + tx.getString("tipo"));
                System.out.println(AMARILLO + "       Estado de Alerta: " + estado + "\n" + RESET);
                incidencias = true;
            }
        }
        
        if (!incidencias) {
            System.out.println(VERDE + "   Servidores financieros óptimos. Sin alertas críticas de seguridad." + RESET);
        }
        System.out.println(NEGRITA + ROJO + "└──────────────────────────────────────────────────────────────────────────────┘" + RESET);
        presionarEnterParaContinuar();
        limpiarPantalla();
    }

    private static void mostrarEmpleados() {
        limpiarPantalla();
        System.out.println("\n" + NEGRITA + CIAN + "┌──────────────────────────────────────────────────────────┐");
        System.out.println("│                PLANTILLA ACTIVA DE NEXUS                 │");
        System.out.println("├──────────────────────────────────────────────────────────┤" + RESET);
        for (Document doc : coleccionEmpleados.find()) {
            String usuario = doc.getString("usuario");
            String rol = doc.getString("rol");
            
            String rolColor = BLANCO;
            if (rol.equals("Administrador")) rolColor = ROJO;
            if (rol.equals("Gerente")) rolColor = MORADO;
            if (rol.equals("Guardia")) rolColor = VERDE;

            String col1 = "   Usuario: " + BLANCO + usuario;
            String col2 = " │ Rango: " + rolColor + rol;

            col1 = ajustarContenido(col1, 28);
            col2 = ajustarContenido(col2, 28);

            System.out.println(NEGRITA + CIAN + "│" + RESET + col1 + NEGRITA + CIAN + col2 + NEGRITA + CIAN + "│" + RESET);
        }
        System.out.println(NEGRITA + CIAN + "└──────────────────────────────────────────────────────────┘" + RESET);
    }

    private static void eliminarEmpleado() {
        mostrarEmpleados();
        System.out.print("\n Ingrese el nombre de usuario de la baja definitiva: ");
        String userBorrar = scanner.nextLine().trim();

        if (userBorrar.equalsIgnoreCase("admin")) {
            System.out.println("\n" + FONDO_ROJO + BLANCO + NEGRITA + " [RECHAZADO] No se pueden remover privilegios al Administrador del Core. " + RESET);
            esperarYLimpiar();
            return;
        }

        long eliminados = coleccionEmpleados.deleteOne(Filters.eq("usuario", userBorrar)).getDeletedCount();
        if (eliminados > 0) {
            System.out.println("\n" + VERDE + "[CONFIGURACIÓN] El empleado '" + userBorrar + "' fue revocado con éxito." + RESET);
        } else {
            System.out.println("\n" + ROJO + "[ERROR] Operación fallida. El usuario solicitado no existe." + RESET);
        }
        esperarYLimpiar(); 
    }
}