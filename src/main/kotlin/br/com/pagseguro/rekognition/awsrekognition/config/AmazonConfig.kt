package br.com.pagseguro.rekognition.awsrekognition.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.rekognition.AmazonRekognition
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AmazonConfig {

    companion object {

        const val BUCKET_NAME = "poc-aws-rekognition"

        const val COLLECTION_ID = "poc-aws-rekognition-collection"

        private const val ACCESS_KEY = "<ACCESS_KEY>"

        private const val SECRET_KEY = "<PRIVATE_KEY>"

    }

    @Bean
    fun amazonS3(): AmazonS3 {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(AWSStaticCredentialsProvider(this.getCredentials()))
                .withRegion(Regions.US_EAST_2)
                .build()
    }

    @Bean
    fun amazonRekognition(): AmazonRekognition {
        return AmazonRekognitionClientBuilder.standard()
                .withCredentials(AWSStaticCredentialsProvider(this.getCredentials()))
                .withRegion(Regions.US_EAST_2)
                .build()
    }

    private fun getCredentials(): BasicAWSCredentials {
        return BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
    }

}