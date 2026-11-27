package com.proyectofinal.veterinarialosarcos.models;

import com.proyectofinal.veterinarialosarcos.enumerados.EstadoRevision;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Una visita médica (una hoja de la ficha clínica física).
 *
 * <p>Cada vez que el paciente vuelve se crea una consulta NUEVA; las anteriores nunca
 * se modifican, así el historial crece igual que en papel.</p>
 */
@Entity
@Table(name = "consulta")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "motivo_consulta", columnDefinition = "text")
    private String motivoConsulta;

    @Column(name = "dieta", columnDefinition = "text")
    private String dieta;

    @Column(name = "comentarios", columnDefinition = "text")
    private String comentarios;

    @Column(name = "temperatura")
    private Double temperatura;

    @Column(name = "peso")
    private Double peso;

    // ---- Examen clínico (igual que en la hoja física) ----

    @Enumerated(EnumType.STRING)
    @Column(name = "aspecto_general")
    private EstadoRevision aspectoGeneral;

    @Enumerated(EnumType.STRING)
    @Column(name = "piel_mucosas")
    private EstadoRevision pielMucosas;

    @Enumerated(EnumType.STRING)
    @Column(name = "respiratorio")
    private EstadoRevision respiratorio;

    @Enumerated(EnumType.STRING)
    @Column(name = "sistema_urinario")
    private EstadoRevision sistemaUrinario;

    @Enumerated(EnumType.STRING)
    @Column(name = "circulatorio")
    private EstadoRevision circulatorio;

    @Enumerated(EnumType.STRING)
    @Column(name = "digestivo")
    private EstadoRevision digestivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "ojos")
    private EstadoRevision ojos;

    @Enumerated(EnumType.STRING)
    @Column(name = "oidos")
    private EstadoRevision oidos;

    @Enumerated(EnumType.STRING)
    @Column(name = "sistema_nervioso")
    private EstadoRevision sistemaNervioso;

    @Enumerated(EnumType.STRING)
    @Column(name = "ganglios_linfaticos")
    private EstadoRevision gangliosLinfaticos;

    /** Vacunas aplicadas en esta visita. Se guardan junto con la consulta. */
    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaAplicacion")
    private List<VacunaAplicada> vacunas = new ArrayList<>();

    public Consulta() {
    }

    /** Agrega una vacuna aplicada y la vincula con esta consulta. */
    public void agregarVacuna(VacunaAplicada aplicada) {
        aplicada.setConsulta(this);
        vacunas.add(aplicada);
    }

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getDieta() {
        return dieta;
    }

    public void setDieta(String dieta) {
        this.dieta = dieta;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public EstadoRevision getAspectoGeneral() {
        return aspectoGeneral;
    }

    public void setAspectoGeneral(EstadoRevision aspectoGeneral) {
        this.aspectoGeneral = aspectoGeneral;
    }

    public EstadoRevision getPielMucosas() {
        return pielMucosas;
    }

    public void setPielMucosas(EstadoRevision pielMucosas) {
        this.pielMucosas = pielMucosas;
    }

    public EstadoRevision getRespiratorio() {
        return respiratorio;
    }

    public void setRespiratorio(EstadoRevision respiratorio) {
        this.respiratorio = respiratorio;
    }

    public EstadoRevision getSistemaUrinario() {
        return sistemaUrinario;
    }

    public void setSistemaUrinario(EstadoRevision sistemaUrinario) {
        this.sistemaUrinario = sistemaUrinario;
    }

    public EstadoRevision getCirculatorio() {
        return circulatorio;
    }

    public void setCirculatorio(EstadoRevision circulatorio) {
        this.circulatorio = circulatorio;
    }

    public EstadoRevision getDigestivo() {
        return digestivo;
    }

    public void setDigestivo(EstadoRevision digestivo) {
        this.digestivo = digestivo;
    }

    public EstadoRevision getOjos() {
        return ojos;
    }

    public void setOjos(EstadoRevision ojos) {
        this.ojos = ojos;
    }

    public EstadoRevision getOidos() {
        return oidos;
    }

    public void setOidos(EstadoRevision oidos) {
        this.oidos = oidos;
    }

    public EstadoRevision getSistemaNervioso() {
        return sistemaNervioso;
    }

    public void setSistemaNervioso(EstadoRevision sistemaNervioso) {
        this.sistemaNervioso = sistemaNervioso;
    }

    public EstadoRevision getGangliosLinfaticos() {
        return gangliosLinfaticos;
    }

    public void setGangliosLinfaticos(EstadoRevision gangliosLinfaticos) {
        this.gangliosLinfaticos = gangliosLinfaticos;
    }

    public List<VacunaAplicada> getVacunas() {
        return vacunas;
    }
}
