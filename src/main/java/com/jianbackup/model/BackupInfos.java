package com.jianbackup.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by duanxun on 2018-11-02.
 */
@Data
@Component
@ConfigurationProperties(prefix = "backup-infos")
public class BackupInfos {
    private List<BackupInfo> JIANHISInfos;

    private List<BackupInfo> JIANEMRInfos;
}
