package org.xujin.janus.monitor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/19 17:33
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DurationParser {

    public static Duration parseSeconds(String seconds) {
        return Duration.ofSeconds(Long.parseLong(seconds));
    }

}
