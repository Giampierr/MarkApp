package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.enums.TipoAsistencia;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.modelo.DetalleAsistencia;
import com.MarkApp.MarkApp.modelo.Sucursal;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.repositorio.DetalleAsistenciaRepositorio;
import com.MarkApp.MarkApp.repositorio.SucursalRepositorio;
import com.MarkApp.MarkApp.util.CalcularDistancia;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import static com.MarkApp.MarkApp.util.Validador.validarCoordenadas;

@Service
public class DetalleAsistenciaServicio {
    private static final ZoneId ZONA_PERU = ZoneId.of("America/Lima");
    private static final LocalTime HORA_INGRESO = LocalTime.of(8,0);
    private static final int TOLERANCIA_INGRESO_MINUTOS = 10;
    private final DetalleAsistenciaRepositorio detalleAsistenciaRepositorio;
    private final ColaboradorRepositorio colaboradorRepositorio;
    private final SucursalRepositorio sucursalRepositorio;

    public DetalleAsistenciaServicio(DetalleAsistenciaRepositorio detalleAsistenciaRepositorio, ColaboradorRepositorio colaboradorRepositorio, SucursalRepositorio sucursalRepositorio) {
        this.detalleAsistenciaRepositorio = detalleAsistenciaRepositorio;
        this.colaboradorRepositorio = colaboradorRepositorio;
        this.sucursalRepositorio = sucursalRepositorio;
    }

    @Transactional
    public DetalleAsistencia registrarIngreso(int idColaborador,double latitudEmpleado, double longitudEmpleado){

        LocalDate fecha = LocalDate.now(ZONA_PERU);
        LocalTime horaActual = LocalTime.now(ZONA_PERU);



        Colaborador colaborador = colaboradorRepositorio.findById(idColaborador).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Colaborador no encontrado"));

        validarUbicacion(latitudEmpleado,longitudEmpleado);


        if (detalleAsistenciaRepositorio.existsByColaborador_IdAndFecha(idColaborador,fecha)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"El colaborador ya marco Ingreso el dia de hoy");
        }

        if (horaActual.isBefore(HORA_INGRESO)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"El ingreso se habilita a las 08:00");
        }

        boolean dentroDeTolerancia = !horaActual.isAfter(HORA_INGRESO.plusMinutes(TOLERANCIA_INGRESO_MINUTOS));

        DetalleAsistencia detalleAsistencia = new DetalleAsistencia();
        detalleAsistencia.setFecha(fecha);
        detalleAsistencia.setColaborador(colaborador);
        detalleAsistencia.setHoraIngreso(horaActual);

        if (dentroDeTolerancia){
            detalleAsistencia.setTipoAsistencia(TipoAsistencia.Puntual);
        }else {
            detalleAsistencia.setTipoAsistencia(TipoAsistencia.Tardanza);
        }

        return detalleAsistenciaRepositorio.save(detalleAsistencia);
    }

    @Transactional
    public DetalleAsistencia registrarSalida(int idColaborador,double latitudEmpleado, double longitudEmpleado){
        LocalDate fecha = LocalDate.now(ZONA_PERU);
        LocalTime horaActual = LocalTime.now(ZONA_PERU);

        Colaborador colaborador = colaboradorRepositorio.findById(idColaborador).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Colaborador no encontrado"));

        validarUbicacion(latitudEmpleado,longitudEmpleado);


        DetalleAsistencia asistencia = detalleAsistenciaRepositorio.findByColaborador_IdAndFecha(idColaborador,fecha).orElseThrow(()->
                new ResponseStatusException(HttpStatus.CONFLICT,"No existe un ingreso registrado hoy"));

        if (asistencia.getHoraIngreso() == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"No se puede marcar la salida sin registrar el ingreso");
        }

        if (asistencia.getHoraSalida() != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"La salida ya fue marcada");
        }

        LocalTime horaSalidaPermitida;

        switch (colaborador.getJornada()){
            case Full -> horaSalidaPermitida = LocalTime.of(17,0);
            case Medio -> horaSalidaPermitida = LocalTime.of(13,0);
            case null, default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La jornada del colaborador no es válida");
        }

        if (horaActual.isBefore(horaSalidaPermitida)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"La salida se habilita a las " + horaSalidaPermitida);
        }

        asistencia.setHoraSalida(horaActual);

        return detalleAsistenciaRepositorio.save(asistencia);
    }

    private void validarUbicacion(
            double latitudEmpleado,
            double longitudEmpleado) {

        validarCoordenadas(latitudEmpleado, longitudEmpleado);

        Sucursal sucursal = sucursalRepositorio.findById(1)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));

        double distancia = CalcularDistancia.calcularDistanciaMetros(
                sucursal.getLatitud().doubleValue(),
                sucursal.getLongitud().doubleValue(),
                latitudEmpleado,
                longitudEmpleado
        );

        if (distancia > sucursal.getRadioGeocercaMetros()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No te encuentras dentro del radio permitido"
            );
        }
    }
}
