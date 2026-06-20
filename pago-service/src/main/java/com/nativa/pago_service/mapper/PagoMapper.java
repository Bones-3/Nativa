package com.nativa.pago_service.mapper;

import org.springframework.stereotype.Component;

import com.nativa.pago_service.dto.PagoRequest;
import com.nativa.pago_service.dto.PagoResponse;
import com.nativa.pago_service.model.Pago;

@Component
public class PagoMapper {

    public Pago toEntity(PagoRequest request) {

        Pago pago = new Pago();

        pago.setPedido_id(request.getPedido_id());
        pago.setUsuario_id(request.getUsuario_id());
        pago.setMetodoPago(request.getMetodoPago());
        
        return pago;   
    }

    public PagoResponse toResponse(Pago pago) {

        return PagoResponse.builder()
                .id(pago.getId())
                .pedido_id(pago.getPedido_id())
                .usuario_id(pago.getUsuario_id())
                .total(pago.getTotal())
                .metodoPago(pago.getMetodoPago())
                .fechaPago(pago.getFechaPago())
                .build();
    }
}
