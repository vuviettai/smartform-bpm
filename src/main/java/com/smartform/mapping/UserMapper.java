package com.smartform.mapping;

import org.mapstruct.Mapper;

import com.smartform.config.MappingConfig;
import com.smartform.domain.Service;
import com.smartform.domain.User;
import com.smartform.domain.dto.ServiceDto;
import com.smartform.domain.dto.UserDto;

@Mapper(config = MappingConfig.class)
public interface UserMapper {
	/**
     * Maps {@link Service} entity to {@link ServiceDto}
     *
     * @param Service entity to be mapped
     * @return mapped dto
     */
    public UserDto toResource(User user);

    /**
     * Maps {@link ServiceDto} to {@link Service} entity
     *
     * @param ServiceDto to be mapped
     * @return mapped entity
     */
    public User fromResource(UserDto userDto);
}
