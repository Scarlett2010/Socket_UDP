import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        Scanner scanner = null;
        try {
            socket = new DatagramSocket();
            InetAddress dirServidor = InetAddress.getByName("localhost");
            scanner = new Scanner(System.in);

            // Enviar mensaje inicial para establecer conexión
            String mensajeInicial = "Conectar";
            byte[] bufferEnvio = mensajeInicial.getBytes();
            DatagramPacket paqueteEnvio = new DatagramPacket(
                    bufferEnvio, bufferEnvio.length, dirServidor, 5000);
            socket.send(paqueteEnvio);

            boolean puntajeRecibido = false;
            while (!puntajeRecibido) {
                // Recibir mensaje del servidor
                byte[] bufferRecepcion = new byte[1024];
                DatagramPacket paqueteRecepcion = new DatagramPacket(bufferRecepcion, bufferRecepcion.length);
                socket.receive(paqueteRecepcion);

                String mensajeRecibido = new String(paqueteRecepcion.getData(), 0, paqueteRecepcion.getLength());
                System.out.println(mensajeRecibido);

                if (mensajeRecibido.startsWith("Puntaje final:") && mensajeRecibido.contains("/20")) {
                    puntajeRecibido = true;
                } else if (mensajeRecibido.startsWith("PREGUNTA Nro")) {
                    System.out.print("Tu respuesta: ");
                    String respuesta = scanner.nextLine();

                    bufferEnvio = respuesta.getBytes();
                    paqueteEnvio = new DatagramPacket(
                            bufferEnvio, bufferEnvio.length, dirServidor, 5000);
                    socket.send(paqueteEnvio);
                }
            }

            System.out.println("Juego terminado. Conexión cerrada.");

        } catch (Exception e) {
            System.out.println("Error cliente");
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}