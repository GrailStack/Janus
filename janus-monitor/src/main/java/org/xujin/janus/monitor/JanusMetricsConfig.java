package org.xujin.janus.monitor;

import org.xujin.janus.config.util.EnvUtils;
import lombok.Data;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/20 14:48
 **/
@Data
public class JanusMetricsConfig {

    private Boolean jvmBinderEnabled = true;

    private Boolean uptimeBinderEnabled = true;

    private Boolean processorBinderEnabled = true;

    private Boolean filesBinderEnabled = true;

    private Boolean diskBinderEnabled = true;

    private String diskBinderPath = EnvUtils.getUserDir();

    private Boolean micrometerBinderEnabled = true;

}
