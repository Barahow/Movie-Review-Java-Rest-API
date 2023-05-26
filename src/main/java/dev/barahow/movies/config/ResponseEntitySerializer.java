package dev.barahow.movies.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.ResponseEntity;

public class ResponseEntitySerializer implements RedisSerializer<ResponseEntity<?>> {

    private final ObjectMapper objectMapper;

    public ResponseEntitySerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(ResponseEntity<?> responseEntity) throws SerializationException {
        try {
            // Serialize only the body of the ResponseEntity
            return objectMapper.writeValueAsBytes(responseEntity.getBody());
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing ResponseEntity", e);
        }
    }

    @Override
    public ResponseEntity<?> deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }

        try {
            // Create a new ResponseEntity with the deserialized body
            Object body = objectMapper.readValue(bytes, Object.class);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing ResponseEntity", e);
        }
    }
}
