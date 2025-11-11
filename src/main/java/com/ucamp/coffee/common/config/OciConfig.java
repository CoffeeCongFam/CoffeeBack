package com.ucamp.coffee.common.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OciConfig {
    @Value("${oci.config.path}")
    private String configFilePath;

    @Value("${oci.config.profile}")
    private String configProfile;

    @Bean
    public ObjectStorageClient objectStorageClient() throws Exception {
        // 1. 구성 파일 읽기
        ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(configFilePath, configProfile);

        // 2. 인증 상세 정보 제공자 설정
        AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);

        // 3. 구성 파일에서 'region' 값 추출
        String regionId = configFile.get("region");
        Region region = Region.fromRegionId(regionId);

        // 4. ObjectStorageClient 생성 시 리전 설정
        return ObjectStorageClient.builder()
            .region(region)
            .build(provider);
    }
}
