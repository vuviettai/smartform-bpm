package com.smartform.domain.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

/**
 * Data transfer object for user entity
 *
 * @author serhiy
 */
@Data
@RegisterForReflection
public class UserDto {
    private UUID id;
    private String username;
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
}