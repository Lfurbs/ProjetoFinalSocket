package servidorC;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import org.json.JSONArray;

public class ServidorC {
    public static void main(String[] args) throws IOException {
        int porta = 7000; 
        ServerSocket serverSocket = new ServerSocket(porta);
        System.out.println("Servidor C aguardando...");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> tratarBusca(socket)).start();
        }
    }

    public static void tratarBusca(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        ) {
            String entrada = in.readLine();
            JSONObject req = new JSONObject(entrada);
            String query = req.getString("query").toLowerCase();

            JSONArray dados = new JSONArray(Files.readString(Paths.get("dados/dados_servidor_c.json"))); // alterar para parte2.json no C
            JSONArray resultados = new JSONArray();

            for (int i = 0; i < dados.length(); i++) {
                JSONObject artigo = dados.getJSONObject(i);
                String titulo = artigo.optString("title", "").toLowerCase();
                String introducao = artigo.optString("abstract", "").toLowerCase();

                if (titulo.contains(query) || introducao.contains(query)) {
                    resultados.put(artigo);
                }
            }

            out.write(resultados.toString());
            out.newLine();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
