package org.desarrollo.fiscalesfrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.desarrollo.fiscalesfrontend.model.TipoDepartamento;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TipoDepartamentoServicio {

    //private static final String URL_BASE = "http://localhost:8080/tiposdepartamentos";
    private static final String URL_BASE = "https://fiscales-backend-production.up.railway.app/tiposdepartamentos";
    private HttpClient cliente;
    private ObjectMapper mapeador;

    public TipoDepartamentoServicio() {
        this.cliente = HttpClient.newHttpClient();
        this.mapeador = new ObjectMapper();
    }

    public List<TipoDepartamento> listarTiposDepartamentos() throws IOException, InterruptedException {
        HttpRequest requeriminento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requeriminento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeador.readValue(respuesta.body(), new TypeReference<List<TipoDepartamento>>() {});
        } else {
            throw new IOException("Error al buscar los tipos de departamentos. HTTP código " + respuesta.statusCode());
        }
    }

    public TipoDepartamento buscarPorId(Integer id) throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + id))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeador.readValue(respuesta.body(), TipoDepartamento.class);
        } else {
            throw new IOException("Error al recuperar el tipo de departamento");
        }

    }
}
