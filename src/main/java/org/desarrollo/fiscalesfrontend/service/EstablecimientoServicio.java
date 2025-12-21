package org.desarrollo.fiscalesfrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.desarrollo.fiscalesfrontend.dto.*;
import org.desarrollo.fiscalesfrontend.mapper.EstablecimientoMapper;
import org.desarrollo.fiscalesfrontend.model.Establecimiento;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EstablecimientoServicio {

    private static final String URL_BASE = "http://localhost:8080/establecimientos";
    //private static final String URL_BASE = "https://fiscales-backend-production.up.railway.app/establecimientos";
    private HttpClient cliente;
    private ObjectMapper mapeo;

    public EstablecimientoServicio() {
        this.cliente = HttpClient.newHttpClient();
        this.mapeo = new ObjectMapper();
    }


    public HttpClient getCliente() {
        return cliente;
    }

    public List<EstablecimientoListaDTO> listarEstablecimientos() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
             return mapeo.readValue(respuesta.body(), new TypeReference<List<EstablecimientoListaDTO>>() {});
        } else {
            throw new IOException("Error al listar los establecimientos " + respuesta.statusCode());
        }
    }

    public EstablecimientoListaDTO guardarEstablecimiento(EstablecimientoRequestDTO dto) throws IOException, InterruptedException {
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
        return mapeo.readValue(respuesta.body(), EstablecimientoListaDTO.class);
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

    public EstablecimientoListaDTO buscoPorIdOptimizado(Integer id) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/lista/" + id))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), EstablecimientoListaDTO.class);
        } else {
            throw new IOException("Error al recuperar el establecimiento para la tabla");
        }
    }

    public String recuperarElTipoEstablecimientoPorIdEst(Integer idEstablecimiento) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/busco-tipo-estableciento-porid/" + idEstablecimiento))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), new TypeReference<String>() {});
        } else {
            throw new IOException("Error al recuperar el tipo de establecimiento por el id del establecimiento");
        }
    }

    public List<EstablecimientoEstadoDTO> listadoComoBoxAsignarFiscales() throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/evaluar-estados"))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), new TypeReference<List<EstablecimientoEstadoDTO>>() {});
        } else {
            throw new IOException("Error al recuperar los establecimientos con sus estados");
        }
    }

    public String recuperarEstadoDelEstablecimiento(Integer idEst) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + idEst + "/mesas/estado"))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), new TypeReference<String>() {});
        } else {
            throw new IOException("Error al recuperar el estado del establecimiento");
        }
    }

    public EstablecimientoDetalleEstadoDTO recuperarEstadosDeLosEstablecimientos(Integer idEst) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/estado/" + idEst))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), new TypeReference<EstablecimientoDetalleEstadoDTO>() {});
        } else {
            throw new IOException("Error al recuperar la lista de los establecimientos con su estado");
        }
    }

    public EstablecimientoResponseDTO buscarPorId(Integer elId) throws IOException, InterruptedException{
        String url = URL_BASE + "/" + elId;
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(url))
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
