package cliente;

import java.io.*;
import java.net.*;
import org.json.JSONObject;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int porta = 5000;

        try (
            Socket socket = new Socket(host, porta);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.print("Digite a substring de busca: ");
            String busca = teclado.readLine();

            JSONObject json = new JSONObject();
            json.put("query", busca);

            out.write(json.toString());
            out.newLine();
            out.flush();

            String resposta = in.readLine();
            System.out.println("Resultados encontrados:");
            System.out.println(resposta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
