import java.io.*;
import java.net.Socket;

public class ClientOdbiorca {

    private static final int port1 = 4444;
    private static final int port2 = 4445;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    ObjectOutputStream oosForFile;
    ObjectInputStream oisForFile;

    public static void main(String[] args) throws Exception {
        new ClientOdbiorca();
    }

    public ClientOdbiorca() throws Exception {
        Socket socketForMessage = new Socket("localhost", port1);
        Socket socketForFile = new Socket("localhost", port2);


        if (socketForFile.isConnected() && socketForMessage.isConnected()) {
            System.out.println("Połączono z serwerem !");
        }

        ElGamal elGamal = new ElGamal();


        Thread thread1 = new Thread(() -> {
            //odbieranie wiadomosci

            try {
                oos = new ObjectOutputStream(socketForMessage.getOutputStream());
                ois = new ObjectInputStream(socketForMessage.getInputStream());

                while (true) {

                    String str = (String) ois.readObject();
                    int key = (int) ois.readObject();

                    System.out.println("Wiadomosc: " + elGamal.Decrypt(str, key));
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    socketForFile.close();
                    socketForMessage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        });




        Thread thread3 = new Thread(() -> {
            //odbieranie pliku
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream("C:/plik.txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                oosForFile = new ObjectOutputStream(socketForFile.getOutputStream());
                oisForFile = new ObjectInputStream(socketForFile.getInputStream());

                while (true) {

                    File file = (File) oisForFile.readObject();
                    FileInputStream fis = new FileInputStream(file);
                    byte[] bytes = new byte[4096];
                    fis.read(bytes, 0, bytes.length);
                    fos.write(bytes, 0, bytes.length);
                    fos.flush();


                    System.out.println("Otrzymano plik!");

                }
            } catch (Exception e) {
                e.printStackTrace();

            }finally {
                try {
                    socketForFile.close();
                    socketForMessage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });


        thread1.start();
        thread3.start();

    }
}