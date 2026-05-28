import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Scanner;

public class CentroComercial {
    // Cambia esto por tu cadena de MongoDB Atlas si lo usas en la nube
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    
    private static MongoCollection<Document> coleccionEmpleados;
    private static MongoCollection<Document> coleccionCamaras;
    private static MongoCollection<Document> coleccionTransacciones;
    private static Scanner scanner = new Scanner(System.in);

    // =========================================================
    //   PALETA DE COLORES ANSI (DISEÑO MÁSTER)
    // =========================================================
    public static final String RESET = "\u001B[0m";
    public static final String NEGRITA = "\u001B[1m";
    
    // Colores de texto
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String MORADO = "\u001B[35m";
    public static final String CIAN = "\u001B[36m";
    public static final String BLANCO = "\u001B[37m";
    
    // Fondos (para alertas críticas)
    public static final String FONDO_ROJO = "\u001B[41m";
    public static final String FONDO_VERDE = "\u001B[42m";

    public static void main(String[] args) {
        limpiarPantalla();
        mostrarLogoNexus();
        
        System.out.print(NEGRITA + CIAN + "[SISTEMA] Inicializando módulos de seguridad... " + RESET);
        
        try {
            MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("CentroComercialDB");
            
            coleccionEmpleados = database.getCollection("empleados");
            coleccionCamaras = database.getCollection("camaras");
            coleccionTransacciones = database.getCollection("transacciones");
            
            System.out.println(NEGRITA + VERDE + "[ONLINE]" + RESET);
            Thread.sleep(800); // Pequeña pausa estética inicial
            
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
    //   MÉTODOS DE CONTROL DE PANTALLA Y TIEMPO
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

    // Duerme el sistema por 2 segundos para dar tiempo de lectura y luego limpia
    private static void esperarYLimpiar() {
        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        limpiarPantalla();
    }

    // Para listados largos (CCTV, Transacciones) donde el usuario controla el ritmo con ENTER
    private static void presionarEnterParaContinuar() {
        System.out.print(NEGRITA + AMARILLO + "\n Presione ENTER para volver al menú..." + RESET);
        scanner.nextLine();
    }

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
            coleccionTransacciones.insertOne(new Document("tipo", "Retiro Cajeros Automáticos").append("monto", 4500.0).append("fecha", "22/05/2026 11:15").append("estado", "Seguro"));
            coleccionTransacciones.insertOne(new Document("tipo", "Apertura Bóveda Principal").append("monto", 0.0).append("fecha", "22/05/2026 14:30").append("estado", "REPORTE: Acceso fuera de horario comercial"));
        }
    }

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
                    System.out.println("\n" + AMARILLO + "[NEXUS] Desconectando bases de datos... Hecho." + RESET);
                    System.out.println(NEGRITA + VERDE + "[NEXUS] Terminal apagada de forma segura. ¡Buen día!" + RESET);
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
            System.out.println("\n" + VERDE + "[ACCESO CONCEDIDO]" + AMARILLO + " Verificando credenciales..." + RESET);
            System.out.println(NEGRITA + VERDE + "¡Bienvenid@ de vuelta, " + user.toUpperCase() + "!" + RESET);
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            limpiarPantalla();
            
            switch (rol) {
                case "Administrador": menuAdministrador(); break;
                case "Guardia": menuGuardia(); break;
                case "Gerente": menuGerente(); break;
                default:
                    System.out.println(ROJO + "[AVISO] Sin privilegios de panel. Presione ENTER." + RESET);
                    scanner.nextLine();
            }
        } else {
            System.out.println("\n" + FONDO_ROJO + BLANCO + NEGRITA + " [DENEGADO] El usuario o la contraseña ingresados no existen. " + RESET);
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
            System.out.println("\n" + ROJO + "[ERROR] Registro cancelado. El usuario '" + user + "' ya existe." + RESET);
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
        System.out.println("\n" + FONDO_VERDE + BLANCO + NEGRITA + " [ÉXITO] Sincronizado. Empleado '" + user + "' dado de alta como [" + rol + "]. " + RESET);
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
                case 1: mostrarEmpleados(); break;
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

    private static void visualizarCamaras() {
        limpiarPantalla();
        System.out.println("\n" + NEGRITA + AZUL + "┌──────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                    CIRCUITO CERRADO DE TELEVISIÓN                    │");
        System.out.println("├──────────────────────────────────────────────────────────────────────┤" + RESET);
        for (Document camara : coleccionCamaras.find()) {
            String estadoRaw = camara.getString("estado").trim();
            String estadoColor;
            
            if (estadoRaw.equalsIgnoreCase("OPERANDO")) {
                estadoColor = VERDE + "OPERANDO" + RESET;
            } else if (estadoRaw.equalsIgnoreCase("FALLA")) {
                estadoColor = ROJO + "FALLA   " + RESET;
            } else {
                estadoColor = AMARILLO + "ALERTADO" + RESET;
            }

            System.out.printf(NEGRITA + AZUL + "│" + RESET + "  UBICACIÓN: " + CIAN + "%-15s" + RESET + " │ ESTADO: %s │ NOTA: " + AMARILLO + "%-21s" + RESET + NEGRITA + AZUL + " │\n" + RESET,
                camara.getString("zona"), estadoColor, camara.getString("alerta"));
        }
        System.out.println(NEGRITA + AZUL + "└──────────────────────────────────────────────────────────────────────┘" + RESET);
        presionarEnterParaContinuar();
        limpiarPantalla();
    }

    private static void reportarFallaCamara() {
        limpiarPantalla();
        System.out.print("\n Ingrese el sector de la cámara afectada (Pasillos/Banco/Área de Comida/Cine): ");
        String zona = scanner.nextLine().trim();
        
        Document camara = coleccionCamaras.find(Filters.eq("zona", zona)).first();
        if (camara != null) {
            System.out.print(" Redacte la anomalía observada: ");
            String problema = scanner.nextLine();
            
            coleccionCamaras.updateOne(Filters.eq("zona", zona),
                new Document("$set", new Document("estado", "ALERTADO").append("alerta", problema)));
            System.out.println("\n" + VERDE + "[BASE DE DATOS] Registro de falla guardado y sincronizado." + RESET);
        } else {
            System.out.println("\n" + ROJO + "[ERROR] Sector erróneo. No existe cámara mapeada." + RESET);
        }
        esperarYLimpiar(); 
    }

    private static void verMovimientosBanco() {
        limpiarPantalla();
        System.out.println("\n" + NEGRITA + MORADO + "┌─────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                         REGISTROS FINANCIEROS CENTRALES                         │");
        System.out.println("├─────────────────────────────────────────────────────────────────────────────────┤" + RESET);
        for (Document tx : coleccionTransacciones.find()) {
            System.out.printf(NEGRITA + MORADO + "│  " + CIAN + "%s" + RESET + " │ Detalle: %-25s │ Monto: " + VERDE + "$%9.2f" + RESET + " │ Obs: %-20s " + NEGRITA + MORADO + "│\n" + RESET,
                tx.getString("fecha"), tx.getString("tipo"), tx.getDouble("monto"), tx.getString("estado"));
        }
        System.out.println(NEGRITA + MORADO + "└─────────────────────────────────────────────────────────────────────────────────┘" + RESET);
        presionarEnterParaContinuar();
        limpiarPantalla();
    }

    private static void verProblemasBanco() {
        limpiarPantalla();
        System.out.println("\n" + NEGRITA + ROJO + "┌──────────────────────────────────────────────────────────────────────┐");
        System.out.println("│               AUDITORÍA DE RIESGOS AUTOMÁTICA DEL BANCO              │");
        System.out.println("├──────────────────────────────────────────────────────────────────────┤" + RESET);
        
        boolean incidencias = false;
        for (Document tx : coleccionTransacciones.find()) {
            String estado = tx.getString("estado");
            if (estado.toLowerCase().contains("reporte")) {
                System.out.printf(ROJO + NEGRITA + "   ANOMALÍA DETECTADA (%s)\n" + RESET + "   Evento: %s\n" + AMARILLO + "   Nivel de Riesgo: %s\n\n" + RESET,
                    tx.getString("fecha"), tx.getString("tipo"), estado);
                incidencias = true;
            }
        }
        
        if (!incidencias) {
            System.out.println(VERDE + "   Servidores financieros estables. Cero anomalías monetarias hoy." + RESET);
        }
        System.out.println(NEGRITA + ROJO + "└──────────────────────────────────────────────────────────────────────┘" + RESET);
        presionarEnterParaContinuar();
        limpiarPantalla();
    }

    private static void mostrarEmpleados() {
        limpiarPantalla();
        System.out.println("\n" + NEGRITA + CIAN + "┌──────────────────────────────────────────────────────────┐");
        System.out.println("│                PLANTILLA ACTIVA DE NEXUS                 │");
        System.out.println("├──────────────────────────────────────────────────────────┤" + RESET);
        for (Document doc : coleccionEmpleados.find()) {
            String rol = doc.getString("rol");
            String rolColor = BLANCO;
            if (rol.equals("Administrador")) rolColor = ROJO;
            if (rol.equals("Gerente")) rolColor = MORADO;
            if (rol.equals("Guardia")) rolColor = VERDE;

            System.out.printf(NEGRITA + CIAN + "│" + RESET + "   Usuario: %-15s │ Rango: " + rolColor + "%-18s" + RESET + NEGRITA + CIAN + " │\n" + RESET, 
                doc.getString("usuario"), rol);
        }
        System.out.println(NEGRITA + CIAN + "└──────────────────────────────────────────────────────────┘" + RESET);
    }

    private static void eliminarEmpleado() {
        mostrarEmpleados();
        System.out.print("\n Ingrese el nombre de usuario de la baja definitiva: ");
        String userBorrar = scanner.nextLine().trim();

        if (userBorrar.equalsIgnoreCase("admin")) {
            System.out.println("\n" + FONDO_ROJO + BLANCO + NEGRITA + " [RECHAZADO] No es posible revocar permisos al Administrador Root. " + RESET);
            esperarYLimpiar();
            return;
        }

        long eliminados = coleccionEmpleados.deleteOne(Filters.eq("usuario", userBorrar)).getDeletedCount();
        if (eliminados > 0) {
            System.out.println("\n" + VERDE + "[CONFIGURACIÓN] El usuario '" + userBorrar + "' fue eliminado del servidor." + RESET);
        } else {
            System.out.println("\n" + ROJO + "[ERROR] Operación fallida. El usuario ingresado no existe." + RESET);
        }
        esperarYLimpiar(); 
    }
}