package com.blubank.doctorappointment.service;

import java.util.List;

public interface BaseService <T>{

    T save(T t);

    T update(T t);

    void delete(Long id);

    T findById(Long id);

    List<T> findAll();
}
