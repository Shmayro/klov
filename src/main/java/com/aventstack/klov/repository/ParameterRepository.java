package com.aventstack.klov.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.aventstack.klov.domain.Parameter;

@RepositoryRestResource(collectionResourceRel = "parameter", path = "parameters")
public interface ParameterRepository<T, ID extends Serializable> extends MongoRepository<Parameter, String> {

    long count();
    
    Parameter findByKey(@Param("key") String key);
    
    List<Parameter> findAll();

}
