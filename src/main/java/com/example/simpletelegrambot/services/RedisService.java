package com.example.simpletelegrambot.services;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class RedisService {
    @Resource
    private RedisTemplate<String,Object> template;

    public void setKey(String mapName, Map<String, String> modelMap) {
        HashOperations<String, String, String> hps = template.opsForHash();
        hps.putAll(mapName, modelMap);
    }

    public Map<String, String> getMapValue(String mapName) {
        HashOperations<String, String, String> hps = this.template.opsForHash();
        return hps.entries(mapName);

    }

    public String getValue(String mapName, String hashKey) {
        HashOperations<String, String, String> hps = this.template.opsForHash();
        return hps.get(mapName, hashKey);

    }

    public void deleteData(List<String> keys) {
        // Serialize template first when performing batch deletion operations
        template.setKeySerializer(new JdkSerializationRedisSerializer());
        template.delete(keys);
    }
}
