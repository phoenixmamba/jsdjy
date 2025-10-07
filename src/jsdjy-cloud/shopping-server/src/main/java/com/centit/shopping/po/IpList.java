package com.centit.shopping.po;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "ips")
@Data
public class IpList {
    private List<String> ipList = new ArrayList<>();
}
