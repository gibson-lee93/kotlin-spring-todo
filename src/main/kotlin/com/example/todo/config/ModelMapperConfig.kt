package com.example.todo.config

import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig {

    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()
        // 필드 이름이 같은 것끼리 매칭
        modelMapper.configuration.isFieldMatchingEnabled = true

        // private 필드여도 접근 가능
        modelMapper.configuration.fieldAccessLevel = org.modelmapper.config.Configuration.AccessLevel.PRIVATE

        return modelMapper
    }
}