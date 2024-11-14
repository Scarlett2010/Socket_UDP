import java.net.*;

class Servidor {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(5000);
            System.out.println("Servidor UDP en ejecución...");
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                HiloCliente HiloCliente = new HiloCliente(socket, paquete);
                HiloCliente.start();
                HiloCliente.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}