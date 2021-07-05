package com.hoomicorp.util;

import org.slf4j.Logger;

public final class Log {
    public static void log(final Logger logger, final String message, boolean isError) {
        if (isError) {
            logger.error(message);
        } else if (logger.isDebugEnabled()) {
            logger.debug(message);
        } else {
            logger.info(message);
        }
    }
}
