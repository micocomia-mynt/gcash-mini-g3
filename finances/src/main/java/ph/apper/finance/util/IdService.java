package ph.apper.finance.util;

import org.apache.commons.lang.RandomStringUtils;

import java.util.UUID;


public class IdService {

    public static String getNextTransactionId() {
        return UUID.randomUUID().toString();
    }
    public static String generateCode(int size) {
        return RandomStringUtils.randomAlphanumeric(size);
    }

}
