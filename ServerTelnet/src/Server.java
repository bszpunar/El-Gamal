import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int port1 = 3333;
    private static final int port2 = 4444;
    private static final int port3 = 3334;
    private static final int port4 = 4445;



    ServerSocket MserverSocket1;
    ServerSocket FserverSocket1;
    Socket socketForMessageFromClient1;
    Socket socketForFileFromClient1;
    ObjectOutputStream oos1;
    ObjectInputStream ois1;

    ServerSocket MserverSocket2;
    ServerSocket FserverSocket2;
    Socket socketForMessageFromClient2;
    Socket socketForFileFromClient2;
    ObjectOutputStream oos2;
    ObjectInputStream ois2;

    public static void main(String[] args) throws Exception {
        new Server();
    }

    public Server() throws Exception {
        System.out.println("Oczekiwanie na połączenia ...");

        MserverSocket1 = new ServerSocket(port1);
        MserverSocket2 = new ServerSocket(port2);
        FserverSocket1 = new ServerSocket(port3);
        FserverSocket2 = new ServerSocket(port4);

        socketForMessageFromClient1 = MserverSocket1.accept();
        socketForMessageFromClient2 = MserverSocket2.accept();
        socketForFileFromClient1 = FserverSocket1.accept();
        socketForFileFromClient2 = FserverSocket2.accept();

        System.out.println("Klienci Polaczeni !");

        oos2 = new ObjectOutputStream(socketForMessageFromClient1.getOutputStream());
        ois2 = new ObjectInputStream(socketForMessageFromClient2.getInputStream());
        oos1 = new ObjectOutputStream(socketForMessageFromClient2.getOutputStream());
        ois1 = new ObjectInputStream(socketForMessageFromClient1.getInputStream());




        Thread thread1 = new Thread(() -> {
            try {
                while (true) {

                    oos1.writeObject(ois1.readObject());
                    oos1.writeObject(ois1.readObject());
                    oos1.flush();
                    System.out.println("Wiadomość Przekazana!");

                    String strBack = "Wiadomosc zostala wyslana!";
                    oos2.writeObject(strBack);
                    oos2.flush();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    socketForMessageFromClient2.close();
                    socketForMessageFromClient1.close();
                    socketForFileFromClient1.close();
                    socketForFileFromClient2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread3 = new Thread(() -> {

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream("C:/plik.txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //wysylanie pliku
            try {
                ObjectOutputStream oosNow = new ObjectOutputStream(socketForFileFromClient2.getOutputStream());
                InputStream is = socketForFileFromClient1.getInputStream();

                while (true) {
                    byte[] bytes = new byte[4096];
                    is.read(bytes, 0, bytes.length);
                    fos.write(bytes, 0, bytes.length);
                    fos.flush();
                    System.out.println("Plik zapisany!");

                    File fileSaved = new File("C:/plik.txt");
                    oosNow.writeObject(fileSaved);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    socketForMessageFromClient2.close();
                    socketForMessageFromClient1.close();
                    socketForFileFromClient1.close();
                    socketForFileFromClient2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });



        thread1.start();
        thread3.start();

    }
}
