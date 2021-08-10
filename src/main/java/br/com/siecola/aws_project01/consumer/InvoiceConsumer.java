package br.com.siecola.aws_project01.consumer;

import br.com.siecola.aws_project01.model.Invoice;
import br.com.siecola.aws_project01.model.SnsMessage;
import br.com.siecola.aws_project01.repository.InvoiceRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class InvoiceConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(InvoiceConsumer.class);
    private ObjectMapper objectMapper;
    private InvoiceRepository repository;
    private AmazonS3 amazonS3;

    @Autowired
    public InvoiceConsumer(ObjectMapper objectMapper, InvoiceRepository repository, AmazonS3 amazonS3){
        this.objectMapper = objectMapper;
        this.repository = repository;
        this.amazonS3 = amazonS3;
    }

    @JmsListener(destination = "${aws.s3.queue.invoice.events.name}")
    public void receiveS3Event(TextMessage textMessage) throws JMSException, IOException {
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);

        S3EventNotification eventNotification = objectMapper.readValue(snsMessage.getMessage(), S3EventNotification.class);

        processInvoiceNotification(eventNotification);
    }

    private void processInvoiceNotification(S3EventNotification eventNotification) throws IOException {
        for(S3EventNotification.S3EventNotificationRecord record : eventNotification.getRecords()){
            S3EventNotification.S3Entity s3Entity = record.getS3();

            String bucketName = s3Entity.getBucket().getName();
            String objectKey = s3Entity.getObject().getKey();

            String invoiceFile = downloadObject(bucketName,objectKey);

            Invoice invoice = objectMapper.readValue(invoiceFile, Invoice.class);
            LOG.info("Invoice received: {}", invoice.getInvoiceNumber());

            repository.save(invoice);

            amazonS3.deleteObject(bucketName, objectKey);
        }
    }

    private String downloadObject(String bucketName, String objectKey) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, objectKey);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(s3Object.getObjectContent())
        );
        String content = null;
        while ((content = bufferedReader.readLine()) != null){
            stringBuilder.append(content);
        }

        return stringBuilder.toString();
    }

}
