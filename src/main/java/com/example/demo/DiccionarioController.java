package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class DiccionarioController {

    private final Diccionario diccionario;
    private final Benchmark benchmark;

    public DiccionarioController(Diccionario diccionario, Benchmark benchmark) {
        this.diccionario = diccionario;
        this.benchmark = benchmark;
    }

    @PostMapping("/palabra")
    @ResponseStatus(HttpStatus.CREATED)
    public Palabra crear(@RequestBody CrearPalabraRequest req) {
        if (req.getPalabra() == null || req.getPalabra().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La palabra no puede estar vacía"); //validación  palabra
        }
        if (req.getSignificado() == null || req.getSignificado().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El significado no puede estar vacío"); //validación significado
        }
        try {
            return diccionario.crear(req); //creación
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/palabra")
    public Palabra actualizar(@RequestBody ActualizarPalabraRequest req) {
        try {
            return diccionario.actualizar(req); //actualización
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/palabra/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        try {
            if (esNumerico(id)) {
                diccionario.eliminarPorId(Integer.parseInt(id)); //intento por id
            } else {
                diccionario.eliminarPorPalabra(id); //intento por palabra
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/palabra")
    public List<Palabra> listarTodas(
            @RequestParam(defaultValue = "alfabeto") String ordenarPor,
            @RequestParam(defaultValue = "asc") String orden) {
        return diccionario.listaPalabras(ordenarPor, orden);
    }

    @GetMapping("/palabra/{id}")
    public Palabra buscar(@PathVariable String id) {
        Palabra resultado;
        if (esNumerico(id)) {
            resultado = diccionario.buscarPorId(Integer.parseInt(id)); //intento por id
        } else {
            resultado = diccionario.buscarExacta(id); //intento por palabra
        }
        if (resultado == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Palabra no encontrada");
        }
        return resultado;
    }

    @GetMapping("/prefijo/{prefijo}")
    public List<Palabra> buscarPorPrefijo(
            @PathVariable String prefijo,
            @RequestParam(defaultValue = "0") int limite,
            @RequestParam(defaultValue = "alfabeto") String ordenarPor,
            @RequestParam(defaultValue = "asc") String orden) {
        return diccionario.buscarPorPrefijo(prefijo, limite, ordenarPor, orden);
    }

    @GetMapping("/comodin/{patron}")
    public List<Palabra> buscarComodin(
            @PathVariable String patron,
            @RequestParam(defaultValue = "0") int limite,
            @RequestParam(defaultValue = "alfabeto") String ordenarPor,
            @RequestParam(defaultValue = "asc") String orden) {
        return diccionario.buscarComodin(patron, limite, ordenarPor, orden);
    }

    @GetMapping("/top")
    public List<Palabra> top(
            @RequestParam(defaultValue = "10") int k,
            @RequestParam(defaultValue = "frecuencia") String ordenarPor,
            @RequestParam(defaultValue = "desc") String orden) {
        return diccionario.topK(k, ordenarPor, orden);
    }

    @PostMapping("/importar")
    public Map<String, Object> importar() {
        int cantidad = diccionario.importar();  // Usa ruta por defecto
        Map<String, Object> respuesta = new java.util.HashMap<>();
        respuesta.put("mensaje", "Importación completada desde archivo por defecto");
        respuesta.put("palabrasImportadas", cantidad);
        return respuesta;
    }

    @PostMapping("/importar/{nombreArchivo}")
    public Map<String, Object> importar(@PathVariable String nombreArchivo) {
        String rutaCompleta = nombreArchivo.endsWith(".csv") ? nombreArchivo : nombreArchivo + ".csv";
        int cantidad = diccionario.importar(rutaCompleta);
        Map<String, Object> respuesta = new java.util.HashMap<>();
        respuesta.put("mensaje", "Importación completada desde: " + rutaCompleta);
        respuesta.put("palabrasImportadas", cantidad);
        return respuesta;
    }

    @PostMapping("/exportar")
    public Map<String, String> exportar() {
        diccionario.exportar();  // Usa ruta por defecto
        Map<String, String> respuesta = new java.util.HashMap<>();
        respuesta.put("mensaje", "Exportado correctamente a archivo por defecto");
        return respuesta;
    }

    @PostMapping("/exportar/{nombreArchivo}")
    public Map<String, String> exportar(@PathVariable String nombreArchivo) {
        String rutaCompleta = nombreArchivo.endsWith(".csv") ? nombreArchivo : nombreArchivo + ".csv";
        diccionario.exportar(rutaCompleta);
        Map<String, String> respuesta = new java.util.HashMap<>();
        respuesta.put("mensaje", "Exportado correctamente a: " + rutaCompleta);
        return respuesta;
    }

    @GetMapping("/benchmark")
    public BenchmarkReporte ejecutarBenchmark() {
        return benchmark.inicializar();
    }

    private boolean esNumerico(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}