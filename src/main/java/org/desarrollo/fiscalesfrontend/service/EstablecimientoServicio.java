package org.desarrollo.fiscalesfrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoMesasDTO;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoResponseDTO;
import org.desarrollo.fiscalesfrontend.mapper.EstablecimientoMapper;
import org.desarrollo.fiscalesfrontend.model.Establecimiento;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EstablecimientoServicio {

    //private static final String URL_BASE = "http://localhost:8080/establecimientos";
    private static final String URL_BASE = "https://fiscales-backend-production.up.railway.app/establecimientos";
    private HttpClient cliente;
    private ObjectMapper mapeo;

    public EstablecimientoServicio() {
        this.cliente = HttpClient.newHttpClient();
        this.mapeo = new ObjectMapper();
    }


    public HttpClient getCliente() {
        return cliente;
    }

    public List<Establecimiento> listarEstablecimientos() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<EstablecimientoResponseDTO> listaDTO = mapeo.readValue(respuesta.body(), new TypeReference<List<EstablecimientoResponseDTO>>() {});
            return listaDTO.stream().map(EstablecimientoMapper::aEntidadModelo).toList();
        } else {
            throw new IOException("Error al listar los establecimientos " + respuesta.statusCode());
        }
    }

    public EstablecimientoResponseDTO guardarEstablecimiento(EstablecimientoRequestDTO dto) throws IOException, InterruptedException {
        String json = mapeo.writeValueAsString(dto);

        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() != 200 && respuesta.statusCode() != 201) {
            throw new RuntimeException("Error HTTP al guardar " + respuesta.statusCode());
        }
        return mapeo.readValue(respuesta.body(), EstablecimientoResponseDTO.class);
    }

    public boolean actualizarEstablecimiento(Integer elId, EstablecimientoRequestDTO dto) throws IOException, InterruptedException {
        String json = mapeo.writeValueAsString(dto);
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + elId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return Boolean.parseBoolean(respuesta.body());
        } else if (respuesta.statusCode() == 404) {
            return false;
        } else {
            throw new IOException("Error al actualizar: código HTTP: " + respuesta.statusCode());
        }
    }

    public List<Establecimiento> listarEstablecimientosSinMesa() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/buscar-establecimiento-sin-mesas"))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<EstablecimientoResponseDTO> dto = mapeo.readValue(respuesta.body(), new TypeReference<List<EstablecimientoResponseDTO>>() {});
            return dto.stream().map(EstablecimientoMapper::aEntidadModelo).toList();
        } else {
            throw new IOException("Error al recuperar los establecimientos sin mesa");
        }
    }

    public List<Establecimiento> listarEstablecimientosConMesas() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/buscar-establecimiento-con-mesas"))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<EstablecimientoResponseDTO> dto = mapeo.readValue(respuesta.body(), new TypeReference<List<EstablecimientoResponseDTO>>() {});
            return dto.stream().map(EstablecimientoMapper::aEntidadModelo).toList();
        } else {
            throw new IOException("No se pudo recuperar los establecimientos con mesas asignadas");
        }
    }

    public EstablecimientoResponseDTO buscarPorId(Integer elId) throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + elId))
                .GET()
                .build();
        HttpResponse<String> respuesa = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesa.statusCode() == 200) {
            return mapeo.readValue(respuesa.body(), EstablecimientoResponseDTO.class);
        } else {
            throw new IOException("Error al recuperar el establecimiento");
        }
    }

    public EstablecimientoMesasDTO mesasResumen(Integer id) throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/mesas-resumen/" + id))
                .GET()
                .build();
        HttpResponse<String>  respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), EstablecimientoMesasDTO.class);
        } else {
            throw new IOException("Error al recuperar el resumen de mesa ");
        }
    }






}
