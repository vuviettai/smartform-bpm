package com.smartform.storage.mongo.entity;

import java.time.LocalDate;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BaseFormioEntity extends PanacheMongoEntityBase {
	@BsonId
    public ObjectId _id;
	protected Integer __v;
	protected LocalDate created;
	protected LocalDate modified;
	protected Double deleted;
	protected ObjectId owner;
}
