import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket serverSocket;

    private ExecutorService service;


    public static void main(String[] args) {

        int port = 8080;

        if (System.getenv("PORT") != null) {
            port= Integer.parseInt(System.getenv("PORT"));


            try {
                new Server().start(port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        service = Executors.newCachedThreadPool();
        serverRequest();


    }

    private void serverRequest() {

        while (true) {


            try {
                Socket clientSocket = serverSocket.accept();

            } catch (IOException e) {

                System.out.println("Not possible to connect ");
            }


        }
    }

    private class RequestHandler implements Runnable {

        BufferedReader reader;
        DataOutputStream writer;
        Socket clientSocket;

        public RequestHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                writer= new DataOutputStream(clientSocket.getOutputStream());
                reader= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                // file
            }
        }


        @Override
        public void run() {

        }

        private void dealWithRequest(){

            try {
                String header= reader.readLine();
                String [] headers= splitHeader(header);
                getResource(header);
            } catch (IOException e) {
                // file
            }
        }

        private void getResource(String header) {

            String[] headerParts = splitHeader(header);

        System.out.printf(" ", headerParts[0]);

        String protocol = headerParts[2];
        String resource = headerParts[1]; // resource / or /index.html or /images/image.png//
        String httpVerb = headerParts[0]; //GET or PUT or POST
       reply(httpVerb, resource);
    }
        private void reply( String httpVerb, String resource) {
        try {
            if (httpVerb.equals("GET")) {
                //writer.write("<html><body><h1>Hello World</h1></body></html>");
                resource = resource.equals("/") ? "/hi.html" : resource;

            if (Files.exists(Path.of("www" + resource))) {
                replyWithFile(new File("www" + resource));
            } else {
                replyWithFile(new File("404error.html"));
            }
            } else {

                replyWithFile(new File("405error.html"));
            }
                writer.flush();

            } catch (IOException e) {
                System.err.println("");
        }finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private String[] splitHeader(String header) {
            String [] hearParts= header.split(" ");

            return hearParts;
        }


        private void replyWithFile(File file) throws IOException {
            byte[] bytes = Files.readAllBytes(Path.of(file.getPath()));
            replyWithHeader(ok(file.getPath(), file.length()));


        }

        public static String ok(String fileName, long length) {
            return "HTTP/1.0 200 Document Follows\r\n" +
                    contentType(fileName) +
                    "Server: Hero\r\n" +
                    "Set-Cookie: turma-mindswap=altamente; Expires="+ LocalDateTime.of(2022,12,12,10,4) +"\r\n" +
                    "Content-Length: " + length + "\r\n\r\n";

        }
        private static String contentType(String fileName) {
            String contentType = "";
            try {                contentType = Files.probeContentType(Path.of("www" + fileName));
            } catch (IOException e) {
        }
            return "Content-Type: " + contentType + "; charset=UTF-8\r\n";

    }

        private void replyWithHeader(String header) throws IOException {

            writer.writeBytes(header);
        }


    }
}