package ph.apper.account.util;

import org.apache.commons.lang.RandomStringUtils;
import ph.apper.account.service.AccountService;

import java.util.concurrent.atomic.AtomicInteger;

public class IdService {
    private static AtomicInteger atomicInteger = new AtomicInteger();

    public static String generateId() {
        String AcctId = "ACCT" +  atomicInteger.incrementAndGet();
        return AcctId;
    }
}
