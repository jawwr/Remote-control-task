import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) {
        try {
            try {
                serverSocket = new ServerSocket(1111);
                System.out.println("Server was started");
                clientSocket = serverSocket.accept();

                try {
                    while (true) {
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                        String word = in.readLine();
                        if (word.equals(":q")) {
                            out.write("Connection closed by foreign host.");
                            break;
                        }
                        String answer = execCommand(word);
                        out.write(answer);
                        out.write('\n');
                        out.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    in.close();
                    out.close();
                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                serverSocket.close();
                System.out.println("Server was stopped");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String execCommand(String command) throws IOException {
//        String[] commands = command.split(" ");
        ProcessBuilder processBuilder = new ProcessBuilder()
                .command("/bin/bash", "-c", command)
                .redirectErrorStream(true);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(processBuilder.start().getInputStream()))) {
            String line = reader.readLine();
            if(line == null){
                return sb.toString();
            }
            sb.append(line).append('\n');
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            sb.delete(0, sb.length());
            sb.append("Error");
            e.printStackTrace();
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }
}
