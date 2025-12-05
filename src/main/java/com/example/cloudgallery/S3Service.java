package com.example.cloudgallery;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${s3.bucket.name}")
    private String bucketName;

    public S3Service(@Value("${aws.accessKey}") String accessKey,
            @Value("${aws.secretKey}") String secretKey,
            @Value("${aws.region}") String region) {
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .withRegion(region)
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // Upload sem ACL (usa as permiss�es padr�o do bucket)
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata);

        s3Client.putObject(request);

        return s3Client.getUrl(bucketName, fileName).toString();
    }

    /**
     * Deleta um arquivo do bucket S3 extraindo o nome do arquivo da URL
     * 
     * @param fileUrl URL completa do arquivo no S3
     */
    public void deleteFile(String fileUrl) {
        try {
            // Extrai o nome do arquivo da URL (última parte depois da última /)
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            s3Client.deleteObject(bucketName, fileName);
            System.out.println("Arquivo deletado do S3: " + fileName);
        } catch (Exception e) {
            System.err.println("Erro ao deletar arquivo do S3: " + e.getMessage());
            throw new RuntimeException("Falha ao deletar arquivo do S3", e);
        }
    }
}
