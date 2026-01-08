package com.eams.asset.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 资产编号生成器
 *
 * @author EAMS Team
 * @since 2026-01-08
 */
@Component
public class AssetNumberGenerator {

    private final AtomicInteger assetCounter = new AtomicInteger(1);
    private final AtomicInteger purchaseCounter = new AtomicInteger(1);

    /**
     * 生成资产编号
     * 格式：AST-YYYYMMDD-XXXX
     */
    public String generateAssetNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = assetCounter.getAndIncrement();
        return String.format("AST-%s-%04d", date, sequence);
    }

    /**
     * 生成采购单号
     * 格式：PUR-YYYYMMDD-XXXX
     */
    public String generatePurchaseNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = purchaseCounter.getAndIncrement();
        return String.format("PUR-%s-%04d", date, sequence);
    }
}
