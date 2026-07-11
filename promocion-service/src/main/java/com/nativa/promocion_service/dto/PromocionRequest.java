package com.nativa.promocion_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromocionRequest {
    @NotBlank(message = "El codigo no puedo estar vacio")
    private String codigo;
   
    @NotBlank(message = "La descripcion no puede estar vacia, agregue para que producto es valida")
    private String descripcion;
    
    @NotNull(message = "Agregue el porcentaje de descuento, no puede estar vacio")
    private BigDecimal porcentajeDescuento;
    
    @FutureOrPresent(message = "La fecha no puede ser anterior al dia de hoy y no puede estar vacia")
    private LocalDate fechaInicio;   
    
    @FutureOrPresent(message = "La fecha no puede estar vacia")
    private LocalDate fechaFin;
    
    @NotNull(message = "debe colocar solo true o false")
    private Boolean activo;

}
