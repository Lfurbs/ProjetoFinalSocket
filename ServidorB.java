package servidorB;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.regex.Pattern;

public class ServidorB {
    public static void main(String[] args) throws IOException {
        int porta = 6000;
        // Cria o servidor socket que escuta na porta 6000
        ServerSocket serverSocket = new ServerSocket(porta);
        System.out.println("Servidor B aguardando...");

        // Loop infinito para aceitar múltiplas conexões
        while (true) {
            Socket socket = serverSocket.accept(); // Aceita conexão de um cliente (Servidor A)
            // Cria uma thread para tratar a busca de forma paralela
            new Thread(() -> tratarBusca(socket)).start();
        }
    }

    // Método para tratar a busca recebida
    public static void tratarBusca(Socket socket) {
        try (
            // Fluxo de entrada e saída de dados com o cliente
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        ) {
            // Lê a entrada do cliente (Servidor A)
            String entrada = in.readLine();
            JSONObject req = new JSONObject(entrada);
            String query = req.getString("query").toLowerCase(); // Converte a busca para minúsculo

            // Carrega os dados locais do servidor B a partir de um arquivo JSON
            JSONArray dados = new JSONArray(Files.readString(Paths.get("dados/dados_servidor_b.json")));
            JSONArray resultados = new JSONArray(); // Armazena os resultados encontrados

            // Percorre cada artigo do arquivo JSON
            for (int i = 0; i < dados.length(); i++) {
                JSONObject artigo = dados.getJSONObject(i);
                String titulo = artigo.optString("title", "").toLowerCase();       // Título do artigo
                String introducao = artigo.optString("abstract", "").toLowerCase(); // Introdução/resumo do artigo

                // Verifica se a query está presente no título
                if (titulo.contains(query)) {
                    JSONObject resultado = new JSONObject();
                    resultado.put("onde", "TÍTULO");
                    resultado.put("texto", destaque(titulo, query)); // Destaca a parte correspondente
                    resultados.put(resultado);
                }

                // Verifica se a query está presente na introdução
                if (introducao.contains(query)) {
                    JSONObject resultado = new JSONObject();
                    resultado.put("onde", "INTRODUÇÃO");
                    resultado.put("texto", destaque(introducao, query)); // Destaca a parte correspondente
                    resultados.put(resultado);
                }
            }

            // Envia os resultados encontrados para o cliente (Servidor A)
            out.write(resultados.toString());
            out.newLine();
            out.flush();
        } catch (Exception e) {
            // Exibe erro no console em caso de falha
            e.printStackTrace();
        }
    }

    // Função que destaca a palavra encontrada usando ANSI (cor amarela no terminal)
    public static String destaque(String texto, String query) {
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_RESET = "\u001B[0m";
        // Substitui ocorrências da query pela mesma palavra destacada em amarelo
        return texto.replaceAll("(?i)(" + Pattern.quote(query) + ")", ANSI_YELLOW + "$1" + ANSI_RESET);
    }

    /* Alternativa para destacar a palavra com markdown, útil se for exibido em um ambiente web:
    public static String destaque(String texto, String query) {
        return texto.replaceAll("(?i)(" + Pattern.quote(query) + ")", "**$1**");
    }
    */
}
