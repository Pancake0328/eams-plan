package com.eams.asset.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eams.purchase.entity.PurchaseOrder;
import com.eams.purchase.mapper.PurchaseOrderMapper;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AssetNumberGenerator {

    private final AtomicInteger assetCounter = new AtomicInteger(1);
    private final PurchaseOrderMapper purchaseOrderMapper;

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
    public synchronized String generatePurchaseNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询当天最大的采购单号
        QueryWrapper<PurchaseOrder> wrapper = new QueryWrapper<>();
        wrapper.likeRight("purchase_number", "PUR-" + date)
                .orderByDesc("purchase_number")
                .last("LIMIT 1");

        PurchaseOrder lastPurchase = purchaseOrderMapper.selectOne(wrapper);

        int sequence = 1;
        if (lastPurchase != null && lastPurchase.getPurchaseNumber() != null) {
            // 从采购单号中提取序号部分（PUR-20260201-0004 -> 0004）
            String lastNumber = lastPurchase.getPurchaseNumber();
            String[] parts = lastNumber.split("-");
            if (parts.length == 3) {
                try {
                    sequence = Integer.parseInt(parts[2]) + 1;
                } catch (NumberFormatException e) {
                    sequence = 1;
                }
            }
        }

        return String.format("PUR-%s-%04d", date, sequence);
    }
}
