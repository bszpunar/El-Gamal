import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

public class ClientNadawca {

    private static final int port1 = 3333;
    private static final int port2 = 3334;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    ObjectOutputStream oosForFile;
    ObjectInputStream oisForFile;



    public static void main(String[] args) throws Exception {
        new ClientNadawca();
    }

    public ClientNadawca() throws Exception {
        Socket socketForMessage = new Socket("localhost", port1);
        Socket socketForFile = new Socket("localhost", port2);


        if (socketForFile.isConnected() && socketForMessage.isConnected()) {
            System.out.println("Połączono z serwerem !");
        }
        ElGamal elGamal = new ElGamal();


        Thread thread2 = new Thread(() -> {
            //wysylanie
            try {
                oos = new ObjectOutputStream(socketForMessage.getOutputStream());
                ois = new ObjectInputStream(socketForMessage.getInputStream());
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String wiadomosc = scanner.nextLine();
                    if (wiadomosc.isEmpty()) {
                        //wyysylanie pliku
                        System.out.println("Podaj sciezke do pliku: ");
                        String str = scanner.nextLine();
                        if (str == "") {
                            System.out.println("Podaj poprawną sciezke! ");
                        } else {
                            FileInputStream fis = new FileInputStream(str);
                            byte[] bytes = new byte[4096];
                            OutputStream os = socketForFile.getOutputStream();
                            System.out.println("Wysyłanie pliku...");
                            fis.read(bytes, 0, bytes.length);
                            os.write(bytes, 0, bytes.length);
                            os.flush();

                            System.out.println("Wysyłanie pliku zakonczone !");
                        }


                    } else {
                        CharSequence ls = "ls";
                        if(wiadomosc.contains(ls)){
                            //System.out.println("it works");
                            Path path = Paths.get("C:\\Program Files\\Java\\jre1.8.0_321");
                            try(Stream<Path> subPaths= Files.walk(path,1)){
                                subPaths.forEach(System.out::println);
                            }catch (IOException e){
                                e.printStackTrace();
                            }

                        }else{
                            String str = elGamal.Encrypt(wiadomosc, elGamal.x);
                            int key = elGamal.x;

                            oos.writeObject(str);
                            oos.writeObject(key);
                            oos.flush();
                            System.out.println("" + ois.readObject());
                        }


                    }
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


        thread2.start();

    }
}