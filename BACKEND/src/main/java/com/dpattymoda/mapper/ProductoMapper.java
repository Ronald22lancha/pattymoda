package com.dpattymoda.mapper;

import com.dpattymoda.dto.response.ProductoBasicoResponse;
import com.dpattymoda.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para conversión entre Producto y DTOs básicos
 */
@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(target = "precioVenta", expression = "java(producto.getPrecioVenta())")
    @Mapping(target = "nombreCategoria", expression = "java(producto.getCategoria() != null ? producto.getCategoria().getNombreCategoria() : null)")
    @Mapping(target = "imagenes", expression = "java(getImagenesArray(producto))")
    ProductoBasicoResponse toBasicoResponse(Producto producto);

    default String[] getImagenesArray(Producto producto) {
        // Implementación simplificada - en producción se parsearia el JSON
        return new String[0];
    }
}