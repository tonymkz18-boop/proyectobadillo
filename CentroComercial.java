import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CentroComercial {
    // Si usas MongoDB en tu propia PC, deja esta dirección. 
    // Si usas la nube (MongoDB Atlas), aquí debes pegar tu enlace largo (ConnectionString).
    private static final String CONNECTION_STRING = "mongodb://localhost:27017"; 
    
    private static MongoCollection<Document> coleccionEmpleados;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Conectando a MongoDB...");
        
        try {
            MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("CentroComercialDB");
            coleccionEmpleados = database.getCollection("empleados");
            
            System.out.println("¡Conexión exitosa a la base de datos!");
            
            // Crea el administrador inicial si la base de datos está vacía
            if (coleccionEmpleados.countDocuments() == 0) {
                Document adminMaestro = new Document("usuario", "admin")
                        .append("contrasena", "admin123")
                        .append("rol", "Administrador")
                        .append("fechaRegistro", "Sistema");
                coleccionEmpleados.insertOne(adminMaestro);
            }

            menuPrincipal();

        } catch (Exception e) {
            System.out.println("\n[ERROR DE CONEXIÓN] No se pudo conectar a MongoDB.");
            System.out.println("Asegúrate de tener MongoDB ejecutándose o que tu enlace sea correcto.");
            System.out.println("Detalle: " + e.getMessage());
        }
    }

    private static void menuPrincipal() {
        int opcion = 0;
        do {
            System.out.println("\n==============================================");
            System.out.println("   SISTEMA CENTRO COMERCIAL (MONGO EDITION)   ");
            System.out.println("==============================================");
            System.out.println(" 1. 🔑 Iniciar Sesión");
            System.out.println(" 2. 📝 Registrarse (Nuevo Empleado)");
            System.out.println(" 3. ❌ Salir");
            System.out.println("==============================================");
            System.out.print("Selecciona una opción: ");
            
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("[ERROR] Ingresa un número válido.");
                scanner.nextLine();
                continue;
            }

            switch (opcion) {
                case 1: iniciarSesion(); break;
                case 2: registrarse(); break;
                case 3: System.out.println("Saliendo..."); break;
                default: System.out.println("Opción inválida.");
            }
        } while (opcion != 3);
    }

    private static void iniciarSesion() {
        System.out.println("\n--- INICIO DE SESIÓN ---");
        System.out.print("👤 Usuario: ");
        String user = scanner.nextLine().trim();
        System.out.print("🔒 Contraseña: ");
        String pass = scanner.nextLine();

        Document empleadoDoc = coleccionEmpleados.find(
            Filters.and(Filters.eq("usuario", user), Filters.eq("contrasena", pass))
        ).first();

        if (empleadoDoc != null) {
            String rol = empleadoDoc.getString("rol");
            System.out.println("\n🎉 ¡Acceso concedido! Bienvenido, " + user + " [" + rol + "]");
            
            if (rol.equals("Administrador")) {
                menuAdministrador();
            } else {
                System.out.println("Estatus: Activo. Presiona ENTER para salir.");
                scanner.nextLine();
            }
        } else {
            System.out.println("\n[❌ ERROR] Credenciales incorrectas.");
        }
    }

    private static void registrarse() {
        System.out.println("\n--- REGISTRO DE EMPLEADO ---");
        System.out.print("👤 Nombre de usuario: ");
        String user = scanner.nextLine().trim();

        Document existe = coleccionEmpleados.find(Filters.eq("usuario", user)).first();
        if (existe != null) {
            System.out.println("[❌ ERROR] Este usuario ya está registrado en MongoDB.");
            return;
        }

        System.out.print("🔒 Contraseña: ");
        String pass = scanner.nextLine();

        System.out.println("Selecciona el Rol:\n 1. Gerente\n 2. Administrador\n 3. Guardia");
        System.out.print("Opción: ");
        int opcRol = scanner.nextInt();
        scanner.nextLine();

        String rol = "Empleado General";
        if (opcRol == 1) rol = "Gerente";
        if (opcRol == 2) rol = "Administrador";
        if (opcRol == 3) rol = "Guardia";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fecha = dtf.format(LocalDateTime.now());

        Document nuevoEmpleado = new Document("usuario", user)
                .append("contrasena", pass)
                .append("rol", rol)
                .append("fechaRegistro", fecha);

        coleccionEmpleados.insertOne(nuevoEmpleado);
        System.out.println("\n✅ ¡Guardado en MongoDB exitosamente!");
    }

    private static void menuAdministrador() {
        int opcAdmin = 0;
        do {
            System.out.println("\n--- PANEL DE ADMINISTRADOR (MONGO) ---");
            System.out.println(" 1. 📊 Ver empleados en la Base de Datos");
            System.out.println(" 2. 🗑️ Eliminar empleado de MongoDB");
            System.out.println(" 3. 🚪 Cerrar Sesión");
            System.out.print("Selecciona una opción: ");
            
            if (scanner.hasNextInt()) {
                opcAdmin = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.nextLine();
                continue;
            }

            if (opcAdmin == 1) mostrarEmpleados();
            if (opcAdmin == 2) eliminarEmpleado();
            
        } while (opcAdmin != 3);
    }

    private static void mostrarEmpleados() {
        System.out.println("\n---------------------------------------------------------");
        System.out.printf("%-15s | %-15s | %-15s\n", "USUARIO", "ROL", "FECHA REGISTRO");
        System.out.println("---------------------------------------------------------");
        for (Document doc : coleccionEmpleados.find()) {
            System.out.printf("%-15s | %-15s | %-15s\n", 
                doc.getString("usuario"), doc.getString("rol"), doc.getString("fechaRegistro"));
        }
        System.out.println("---------------------------------------------------------");
    }

    private static void eliminarEmpleado() {
        mostrarEmpleados();
        System.out.print("\nEscribe el usuario exacto que deseas borrar de MongoDB: ");
        String userBorrar = scanner.nextLine().trim();

        if (userBorrar.equalsIgnoreCase("admin")) {
            System.out.println("[❌ RECHAZADO] No puedes borrar al administrador maestro.");
            return;
        }

        long eliminados = coleccionEmpleados.deleteOne(Filters.eq("usuario", userBorrar)).getDeletedCount();

        if (eliminados > 0) {
            System.out.println("\n[✅ ÉXITO] El usuario '" + userBorrar + "' fue eliminado de MongoDB.");
        } else {
            System.out.println("\n[❌ ERROR] No se encontró ese usuario.");
        }
    }
}