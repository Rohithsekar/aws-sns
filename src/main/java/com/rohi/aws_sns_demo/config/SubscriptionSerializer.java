package com.rohi.aws_sns_demo.config;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import software.amazon.awssdk.services.sns.model.Subscription;

import java.io.IOException;

public class SubscriptionSerializer extends JsonSerializer<Subscription> {
    @Override
    public void serialize(Subscription subscription, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("subscriptionArn", subscription.subscriptionArn());
        jsonGenerator.writeStringField("endpoint", subscription.endpoint());
        jsonGenerator.writeStringField("protocol", subscription.protocol());
        jsonGenerator.writeStringField("owner", subscription.owner());
        jsonGenerator.writeEndObject();

    }

}