package com.blubank.doctorappointment.mapper;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BaseMapper<T,V> {

    T toT(V v);

    V toV(T t);

    List<T> toT(List<V> list);

    List<V> toV(List<T> list);
}
