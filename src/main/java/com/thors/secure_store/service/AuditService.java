//package com.thors.secure_store.service;
//
//import com.thors.secure_store.dto.others.AuditEventDto;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.AllArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@AllArgsConstructor
//public class AuditService {
//
//    private final KafkaTemplate<String, AuditEventDto> kafkaTemplate;
//
//    private static final String TOPIC = "audit.file-events";
//
//    public void logEvent(AuditEventDto event) {
//        kafkaTemplate.send(TOPIC, event.getFileId(), event);
//    }
//
//    public void logEvent(String type, String fileId, String userId) {
//        AuditEventDto event = AuditEventDto.builder()
//                .eventType(type)
//                .fileId(fileId)
//                .performedBy(userId)
//                .timestamp(System.currentTimeMillis())
//                //TODO
////                .ipAddress(request.getRemoteAddr())
////                .userAgent(request.getHeader("User-Agent"))
//                .build();
//
//        logEvent(event);
//    }
//}
//
