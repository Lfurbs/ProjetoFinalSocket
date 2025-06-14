package servidorA;

import java.io.*;
import java.net.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ServidorA {
    public static void main(String[] args) throws IOException {
        int porta = 5000;
        ServerSocket serverSocket = new ServerSocket(porta);
        System.out.println("Servidor A aguardando conexões...");

        while (true) {
            Socket clienteSocket = serverSocket.accept();
            new Thread(() -> tratarCliente(clienteSocket)).start();
        }
    }

    public static void tratarCliente(Socket clienteSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clienteSocket.getOutputStream()));
        ) {
            String input = in.readLine();
            JSONObject json = new JSONObject(input);
            String query = json.getString("query");

            // Envia busca para servidores B e C
            JSONArray resultados = combinarResultados(
                buscarEmServidor("localhost", 6000, query),
                buscarEmServidor("localhost", 7000, query)
            );

            out.write(resultados.toString());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray buscarEmServidor(String host, int porta, String query) {
        JSONArray resultados = new JSONArray();
        try (
            Socket socket = new Socket(host, porta);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            JSONObject json = new JSONObject();
            json.put("query", query);
            out.write(json.toString());
            out.newLine();
            out.flush();

            String resposta = in.readLine();
            resultados = new JSONArray(resposta);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultados;
    }

    public static JSONArray combinarResultados(JSONArray a, JSONArray b) {
        JSONArray combinados = new JSONArray();
        for (int i = 0; i < a.length(); i++) {
            combinados.put(a.get(i));
        }
        for (int i = 0; i < b.length(); i++) {
            combinados.put(b.get(i));
        }
        return combinados;
    }
}
