package com.rohi.aws_sns_demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sns")
public class SNSController {


    @Value("${aws.sns.sms.type.value}")
    private String AWS_SNS_SMS_TYPE_VALUE;

    @Value("${aws.sns.sms.data.type}")
    private String dataType;
    @Autowired
    private SnsClient snsClient;

    @Value("${aws.sns.arn}")
    private String arn;

    /**
     * A noticable change from the SDK for Java 1.x to the SDK for Java 2.x is the package name change.
     * Package names begin with software.amazon.awssdk in SDK 2.x, whereas the SDK 1.x uses
     * com.amazonaws.
     * These same names differentiate Maven artifacts from SDK 1.x to SDK 2.x. Maven artifacts for the SDK 2.x
     * use the software.amazon.awssdk groupId, whereas the SDK 1.x uses the com.amazonaws groupId.
     * There are a few times when your code requires a com.amazonaws dependency for a project that
     * otherwise uses only SDK 2.x artifacts. One example of this is when you work with server-side AWS
     * Lambda.
     * <p>
     * You must create all clients using the client builder method. Constructors are no longer available.
     */

    @PostMapping("/createTopic/{topicName}")
    public String createSNSTopic(@PathVariable String topicName) {

        //arn:aws:sns:ap-south-1:489374343220:myTopic
        CreateTopicResponse result = null;
        try {
            CreateTopicRequest request = CreateTopicRequest.builder()
                    .name(topicName)
                    .build();

            result = snsClient.createTopic(request);
            return result.topicArn();
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }

    @PostMapping("/subscribeEmail")
    public String subscribeEmail(@RequestParam(value = "email") String email, @RequestParam(value = "topicArn") String topicArn) {
        SubscribeRequest request = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("email")
                .endpoint(email)
                .build();

        snsClient.subscribe(request);

        return "Subscription request is pending. To confirm the subscription, check your mail: " + email;
    }

    @PostMapping("/subscribeMobile")
    public String subscribeMobile(@RequestParam String mobileNumber, @RequestParam String topicArn) {
        SubscribeRequest request = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("sms")
                .endpoint(mobileNumber)
                .build();

        snsClient.subscribe(request);
        return "mobile number successfully subscribed.";

    }


//    @PostMapping("/publish")
//    public String publishMessageToTopic() {
//        PublishRequest publishRequest = PublishRequest.builder()
//                .topicArn(arn)
//                .message("You have received the first publication issued by the topic, since you subscribed.")
//                .subject("First published notification")
//                .build();
//
//        snsClient.publish(publishRequest);
//        return "Your notification has been published.";
//
//    }
//
//    @PostMapping("/publish/sms/")
//    public String publishSMSMessageToAll(@RequestParam(value = "topicArn") String topicArn, @RequestParam(value = "message") String message) {
//
//        try {
//            PublishRequest request = PublishRequest.builder()
//                    .message(message)
//                    .topicArn(topicArn)
//                    .messageAttributes(buildSMSAttributes(AWS_SNS_SMS_TYPE_VALUE)).build();
//
//            snsClient.publish(request);
//
//            return "Promotional message sent successfully.";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Failed to send message.";
//        }
//    }

    @PostMapping("/publish/all")
    public String publishMessage(@RequestParam(value = "topicArn") String topicArn, @RequestParam(value = "message") String message) {
        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .subject("Hahhahahha")
                .build();

        snsClient.publish(publishRequest);
        return "Your notification has been published.";
    }




    @PostMapping("/publish/mobile")
    public String publishSMSMessageToMobile(@RequestParam(value = "topicArn") String  topicArn,
                                            @RequestParam(value = "message") String message,
                                            @RequestParam(value = "mobile") String mobile) {
        try {
            PublishRequest request = PublishRequest.builder().message(message)
                    .phoneNumber(mobile)
//                    .topicArn(topicArn)
                    .messageAttributes(buildSMSAttributes("Transactional"))
                    .build();

            snsClient.publish(request);

            return "Customer Message sent successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send message.";
        }
    }

    private Map<String, MessageAttributeValue> buildSMSAttributes(String smsType) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        MessageAttributeValue value = MessageAttributeValue.builder().stringValue(smsType).dataType(dataType).build();
        messageAttributes.put("AWS.SNS.SMS.SMSType", value);
        return messageAttributes;
    }


    @GetMapping("/getTopics")
    public String listSNSTopics() {

        try {
            ListTopicsRequest request = ListTopicsRequest.builder().build();
            ListTopicsResponse result = snsClient.listTopics(request);
            return "Status was " + result.sdkHttpResponse().statusCode() + "\n\nTopics\n\n" + result.topics();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return "oops. please try again later.";
        }
    }

    @GetMapping("/getTopicsByArn/{topicArn}")
    public List<Subscription> listSNSSubscriptions(@PathVariable String topicArn) {

        List<Subscription> response = new ArrayList<>();
        try {

            ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder().topicArn(topicArn).build();

            ListSubscriptionsByTopicResponse snsResponse = snsClient.listSubscriptionsByTopic(request);
            for (Subscription subscription : snsResponse.subscriptions()) {
                System.out.println("Subscription ARN: " + subscription.subscriptionArn());
                System.out.println("Endpoint: " + subscription.endpoint());
                System.out.println("Protocol: " + subscription.protocol());
                System.out.println("Owner: " + subscription.owner());
                System.out.println("----");
            }
            response.addAll(snsResponse.subscriptions());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        } finally {
            return response;
        }

    }

    @PostMapping("/unsubscribe/{subscriptionArn}")
    public String unSubscribe(@PathVariable String subscriptionArn) {

        try {
            UnsubscribeRequest request = UnsubscribeRequest.builder()
                    .subscriptionArn(subscriptionArn)
                    .build();

            UnsubscribeResponse result = snsClient.unsubscribe(request);

            return "\n\nStatus was " + result.sdkHttpResponse().statusCode()
                    + "\n\nSubscription was removed for " + request.subscriptionArn();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return "oops. please try again later.";
        }
    }

    @DeleteMapping("/deleteTopicArn/{deleteTopicArn}")
    public String deleteSNSTopic(@PathVariable String deleteTopicArn) {

        try {
            DeleteTopicRequest request = DeleteTopicRequest.builder()
                    .topicArn(deleteTopicArn)
                    .build();

            DeleteTopicResponse result = snsClient.deleteTopic(request);
            return "Status was " + result.sdkHttpResponse().statusCode();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return "oops. try again later";
        }
    }


}
