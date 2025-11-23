package org.desarrollo.fiscalesfrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import org.desarrollo.fiscalesfrontend.model.Fiscal;
import org.desarrollo.fiscalesfrontend.model.TipoFiscal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TipoFiscalServicio {

    private static final String URL_BASE = "http://localhost:8080/tiposfiscales";
    private HttpClient cliente;
    private ObjectMapper mapeo;

    public TipoFiscalServicio() {
        this.cliente = HttpClient.newHttpClient();
        this.mapeo = new ObjectMapper();
    }

    public List<TipoFiscal> listarTiposFiscales() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), new TypeReference<List<TipoFiscal>>() {});
        } else {
            throw new IOException("Error al acceder a la lista de los tipos de fiscal. HTTP código " + respuesta.statusCode());
        }
    }

    public TipoFiscal buscarFiscalPorNombre(String nombre) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/buscar/" + nombre))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), new TypeReference<TipoFiscal>() { });
        } else {
            throw new IOException("No se pudo recuperar los fiscales con el nombre " + nombre);
        }
    }

    public TipoFiscal buscarPorID(Integer elId) throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + elId))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), TipoFiscal.class);
        } else {
            throw new IOException("Error al recuperar el establecimiento");
        }
    }
}
