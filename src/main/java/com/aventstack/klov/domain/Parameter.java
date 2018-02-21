package com.aventstack.klov.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Parameter extends KlovDocument implements Serializable {

	private static final long serialVersionUID = -4627624513390203570L;

	@Id
    private String key;

    private String value;
    
}
