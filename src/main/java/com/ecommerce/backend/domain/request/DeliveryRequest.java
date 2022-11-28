package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.DeliveryReceiver;
import lombok.Data;
import lombok.experimental.Accessors;

public class DeliveryRequest {
    @Data @Accessors(chain = true)
    public static class Receiver {
        private String receiverName;

        private String receiverPhoneNumber;

        private String receiverEmail;

        public DeliveryReceiver toDeliveryReceiver(){
            return DeliveryReceiver
                    .builder()
                    .receiverName(receiverName)
                    .receiverPhoneNumber(receiverPhoneNumber)
                    .receiverEmail(receiverEmail)
                    .build();
        }
    }
}