package org.desarrollo.fiscalesfrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpPrincipal;
import org.desarrollo.fiscalesfrontend.dto.FiscalRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.FiscalResponseDTO;
import org.desarrollo.fiscalesfrontend.mapper.FiscalMapper;
import org.desarrollo.fiscalesfrontend.model.Fiscal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class FiscalServicio {

    private static final String URL_BASE = "http://localhost:8080/fiscales";
    private HttpClient cliente;
    private ObjectMapper mapeo;

    public FiscalServicio() {
        this.cliente = HttpClient.newHttpClient();
        this.mapeo = new ObjectMapper();
    }

    public HttpClient getCliente() {
        return this.cliente;
    }

    //======================= LISTAR TODOS LOS FISCALES ==============================
    public List<Fiscal> listarFiscales() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<FiscalResponseDTO> listaDTO = mapeo.readValue(respuesta.body(), new TypeReference<List<FiscalResponseDTO>>() {});
            return listaDTO.stream().map(FiscalMapper::aFiscalDeResponseDTO).toList();
        } else {
            throw new IOException("Error al listar los fiscales " + respuesta.statusCode());
        }

    }

    //======================= LISTAR FISCALES POR TIPO DE FISCAL ======================
    public List<Fiscal> listaPorTipoFiscal(Integer id) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/tipo-fiscal/" + id))
                .GET()
                .build();
        HttpResponse<String> resuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (resuesta.statusCode() == 200) {
            List<FiscalResponseDTO> listaDto = mapeo.readValue(resuesta.body(), new TypeReference<List<FiscalResponseDTO>>() { });
            return listaDto.stream().map(FiscalMapper::aFiscalDeResponseDTO).toList();
        } else {
            throw new IOException("Error al recuperr los fiscales de un determinado tipo" + resuesta.statusCode());
        }
    }


    //====================== GUARDAMOS UN FISCAL NUEVO ================================
    public FiscalResponseDTO guardarFiscal(FiscalRequestDTO dto) throws IOException, InterruptedException {
            ObjectMapper mapeo = new ObjectMapper();
            String json = mapeo.writeValueAsString(dto);

            HttpRequest requerimiento = HttpRequest.newBuilder()
                    .uri(URI.create(URL_BASE))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200 && respuesta.statusCode() != 201) {
                throw new RuntimeException("Error al guradar el fiscal " + respuesta.statusCode());
            }
            return mapeo.readValue(respuesta.body(), FiscalResponseDTO.class);
    }

    //====================== ACTUALIZAMOS UN FISCAL ================================
    public boolean actualizoFiscal(Integer idFiscal, FiscalRequestDTO dto) throws IOException, InterruptedException {
        String json = mapeo.writeValueAsString(dto);
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + idFiscal))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return Boolean.parseBoolean(respuesta.body());
        } else if (respuesta.statusCode() == 400) {
            return false;
        } else {
            throw new IOException("Error al actualizar el fiscal " + respuesta.statusCode());
        }
    }

    public Fiscal buscoPorId(Integer idFiscal) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + idFiscal))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return FiscalMapper.aFiscalDeResponseDTO(mapeo.readValue(respuesta.body(), FiscalResponseDTO.class));
        } else {
            throw new IOException("No se pudo recuperar el fiscal con su ID");
        }
    }

    public void asignoFiscalGeneral(Integer idFiscal, Integer idEstablecimiento) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE  + "/" + idFiscal +"/asignar/" + idEstablecimiento))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() != 200) {
            throw new IOException("No se pudo asignar el fiscal al establecimiento");
        }
    }

    public void asingarFiscalAUnaMesa(Integer idFiscal, Integer idMesa) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + idFiscal + "/asignar-fiscal-mesa/" + idMesa))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() != 200) {
            throw new IOException("No se pudo asignar el fiscal a la mesa");
        }
    }

    public void desasignarUnFiscalAUnaMesa(Integer idFiscal) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/desasignar-fiscal-mesa/" + idFiscal))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() != 200) {
            throw new IOException("No se pudo desasignar el fiscal a la mesa ");
        }
    }

    public void desasignarFiscalGeneral(Integer idFiscal) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/desasignar-fiscal-general/" + idFiscal))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() != 200) {
            throw new IOException("No se pudo desasignar el fiscal general");
        }
    }

    public List<Fiscal> listarPorEstablecimientoAsignado(Integer idEstablecimiento) throws IOException, InterruptedException{
        HttpRequest requeimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/fiscal-establecimiento-asignado/" + idEstablecimiento))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requeimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<FiscalResponseDTO> listaDto = mapeo.readValue(respuesta.body(), new TypeReference<List<FiscalResponseDTO>>() {});
            return listaDto.stream().map(FiscalMapper::aFiscalDeResponseDTO).toList();
        } else {
            throw new IOException("No se pudo recuperar la lista de los fiscales de este establecimiento");
        }
    }

    public List<Fiscal> listarTipoFiscalEstablecimientoNull(Integer idTipoFiscal) throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/tipo-fiscal-sin-establecimiento/" + idTipoFiscal))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<FiscalResponseDTO> lista = mapeo.readValue(respuesta.body(), new TypeReference<List<FiscalResponseDTO>>() {});
            return lista.stream().map(FiscalMapper::aFiscalDeResponseDTO).toList();
        } else {
            throw new IOException("Error al recuperar los fiscales sin establecimiento asignado");
        }
    }

    public List<Fiscal> listaFiscalesTipoFiscalJornadaSinMesa(Integer idTipoFiscal, Integer idJornada) throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/fiscal-tipo/" + idTipoFiscal + "/jornada/" + idJornada + "/sinmesa/"))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<FiscalResponseDTO> lista = mapeo.readValue(respuesta.body(), new TypeReference<List<FiscalResponseDTO>>() {});
            return lista.stream().map(FiscalMapper::aFiscalDeResponseDTO).toList();
        } else {
            throw new IOException("Error al recuperar la lista de los fiscales sin mesa y de un tipo y jornada");
        }
    }

    public FiscalResponseDTO fiscalTipoFiscalJornadaMesa(Integer idTipoFiscal, Integer idJornada,Integer idMesa) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/fiscal-tipo/" + idTipoFiscal + "/jornada/" + idJornada + "/" + idMesa))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            String cuerpo = respuesta.body().trim();
            if (cuerpo.isEmpty() || cuerpo.equalsIgnoreCase("null")) {
                return null;
            }
            return mapeo.readValue(cuerpo, FiscalResponseDTO.class);
        } else {
            throw new IOException("Error al recuperar el fiscal asignado a una mesa y de un tipo y jornada");
        }
    }

    public List<Fiscal> recuperarFiscalesSinMesa(Integer idTipoFiscal) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/fiscales-comunes-sin-mesa/" + idTipoFiscal))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<FiscalResponseDTO> lista = mapeo.readValue(respuesta.body(), new TypeReference<List<FiscalResponseDTO>>() {});
            return lista.stream().map(FiscalMapper::aFiscalDeResponseDTO).toList();
        } else {
            throw new IOException("Error al recuperar la lista de los fiscales sin mesa");
        }
    }

    public List<Fiscal> buscoFiscalPorIdMesa(Integer idMesa) throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/fiscal-por-id-mesa/" + idMesa))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        //System.out.println("Status " + respuesta.statusCode());
        //System.out.println("Body" + respuesta.body());
        if (respuesta.statusCode() == 200) {
            List<FiscalResponseDTO> list = mapeo.readValue(respuesta.body(), new TypeReference<List<FiscalResponseDTO>>() {});
            return list.stream().map(FiscalMapper::aFiscalDeResponseDTO).toList();
        } else {
            throw new IOException("No se ha podido recuperar el fiscal con la mesa especificada");
        }
    }

    public List<Fiscal> listarFiscalesTipoFiscalEstablecimientoAsignado(Integer idTipo, Integer idEstAsignado) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/fiscal-tipo/" + idTipo + "/establecimiento-asignado/" + idEstAsignado))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<FiscalResponseDTO> lista = mapeo.readValue(respuesta.body(), new TypeReference<List<FiscalResponseDTO>>() {});
            return lista.stream().map(FiscalMapper::aFiscalDeResponseDTO).toList();
        } else {
            throw new IOException("Error, no se pudo recuperar la lista de fiscales con establecimientos asignados");
        }
    }

}
