import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HiloCliente extends Thread {
    private DatagramSocket socket;
    private DatagramPacket pCliente;
    private static final int espera = 100;
    private static final String archivo = "Respuestas.txt";

    public HiloCliente(DatagramSocket socket, DatagramPacket pCliente) {
        this.socket = socket;
        this.pCliente = pCliente;
    }

    public void run() {
        try {
            InetAddress direccionCli = pCliente.getAddress();
            int puertoCliente = pCliente.getPort();
            String[] preguntasCliente = {
                    "¿Cuál es el continente más grande?",
                    "¿Cuál es la forma química del agua?",
                    "¿Cuál es el río más grande en América del Sur?",
                    "¿En qué año llegó el hombre a la Luna?",
                    "¿Cuál es el nombre del canal principal que atraviesa Venecia?"
            };
            String[] respuestasCorrectas = {
                    "asia",
                    "h2o",
                    "amazonas",
                    "1969",
                    "gran canal"
            };

            String[] respuestasCliente = new String[preguntasCliente.length];
            boolean[] respuestasCheck = new boolean[preguntasCliente.length];

            int puntaje = 0;
            File archivoNuevo = new File(archivo);
            FileWriter fw = new FileWriter(archivoNuevo, true);
            BufferedWriter texto = new BufferedWriter(fw);

            Date fechaHoy = new Date();
            SimpleDateFormat formato = new SimpleDateFormat("MMM-dd-YYYY HH:mm:ss");
            String fecha = formato.format(fechaHoy);
            String IP = direccionCli.getHostAddress();

            texto.write(" Preguntas \n");
            texto.write("Fecha: " + fecha + "\n");
            texto.write("IP del cliente: " + IP + "\n\n");

            enviarMensaje("El juego inicia", direccionCli, puertoCliente);
            Thread.sleep(espera);
            enviarMensaje("Estas son las preguntas, contesta correctamente", direccionCli, puertoCliente);

            for (int i = 0; i < preguntasCliente.length; i++) {
                enviarMensaje("PREGUNTA Nro " + (i + 1) + ": " + preguntasCliente[i], direccionCli, puertoCliente);

                byte[] bufferRespuesta = new byte[1024];
                DatagramPacket paqueteRespuesta = new DatagramPacket(bufferRespuesta, bufferRespuesta.length);
                socket.receive(paqueteRespuesta);

                String respuestaRecibida = new String(paqueteRespuesta.getData(), 0, paqueteRespuesta.getLength())
                        .trim().toLowerCase();

                respuestasCliente[i] = respuestaRecibida;

                if (respuestaRecibida.equals(respuestasCorrectas[i])) {
                    puntaje += 4;
                    respuestasCheck[i] = true;
                    enviarMensaje("Tu respuesta es: CORRECTA", direccionCli, puertoCliente);
                } else {
                    respuestasCheck[i] = false;
                    enviarMensaje("Tu respuesta es: INCORRECTA. La respuesta correcta es: " + respuestasCorrectas[i],
                            direccionCli, puertoCliente);
                }

                texto.write("Pregunta " + (i + 1) + ": " + preguntasCliente[i] + "\n");
                texto.write("Respuesta del cliente: " + respuestasCliente[i] + "\n");
                texto.write("Respuesta correcta: " + respuestasCorrectas[i] + "\n");
                texto.write("¿Fue correcta? " + (respuestasCheck[i] ? "Sí" : "No") + "\n\n");
            }

            texto.write("=== RESUMEN FINAL ===\n");
            texto.write("Puntaje total: " + puntaje + "/20\n");
            texto.write("================================\n\n");

            texto.close();

            enviarMensaje("SE ACABÓ EL JUEGO", direccionCli, puertoCliente);
            enviarMensaje("Puntaje final: " + puntaje + "/20", direccionCli, puertoCliente);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviarMensaje(String mensaje, InetAddress direccion, int puerto) throws Exception {
        byte[] bufferEnvio = mensaje.getBytes();
        DatagramPacket paqueteEnvio = new DatagramPacket(bufferEnvio, bufferEnvio.length, direccion, puerto);
        socket.send(paqueteEnvio);
    }
}