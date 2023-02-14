package com.yzlc.common.service;

import java.io.IOException;
import java.util.Map;

public interface SvnService {
    boolean checkOut();

    int doUpdate();
}
