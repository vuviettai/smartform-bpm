package com.smartform.storage.mongo.entity;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;

import com.smartform.config.MappingConfig;
import com.smartform.domain.Service;
import com.smartform.domain.dto.ServiceDto;

@Mapper(config = MappingConfig.class)
public interface EntityMapper {
    public com.smartform.rest.model.FormioForm toModel(FormioForm entity);
    public FormioForm fromModel(com.smartform.rest.model.FormioForm model);
    public com.smartform.rest.model.Submission toModel(Submission entity);

    /**
     * Maps {@link ServiceDto} to {@link Service} entity
     *
     * @param ServiceDto to be mapped
     * @return mapped entity
     */
    public Submission fromModel(com.smartform.rest.model.Submission model);
//    default String fromObjectId(ObjectId _id) {
//        return _id == null ? null : _id.toString();
//    }
//
//    default ObjectId toObjectId(String _id) {
//        return _id == null ? null : new ObjectId(_id);
//    }
}
