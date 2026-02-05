package com.eams.asset.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eams.asset.entity.AssetNumberSequence;
import com.eams.asset.mapper.AssetNumberSequenceMapper;
import com.eams.purchase.entity.PurchaseOrder;
import com.eams.purchase.mapper.PurchaseOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 资产编号生成器
 *
 * @author EAMS Team
 * @since 2026-01-08
 */
@Component
@RequiredArgsConstructor
public class AssetNumberGenerator {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final AssetNumberSequenceMapper assetNumberSequenceMapper;
    private final com.eams.asset.mapper.AssetInfoMapper assetInfoMapper;

    /**
     * 生成资产编号
     * 格式：AST-YYYYMMDD-XXXX
     */
    @Transactional(rollbackFor = Exception.class)
    public String generateAssetNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "AST";

        assetNumberSequenceMapper.insertIgnore(prefix, date);

        for (int i = 0; i < 5; i++) {
            AssetNumberSequence sequence = assetNumberSequenceMapper.selectForUpdate(prefix, date);
            if (sequence == null) {
                throw new IllegalStateException("资产编号序列初始化失败");
            }
            int nextNumber = sequence.getCurrentNumber() + 1;
            assetNumberSequenceMapper.incrementSequence(prefix, date);

            String assetNumber = String.format("%s-%s-%04d", prefix, date, nextNumber);
            if (assetInfoMapper.countByAssetNumber(assetNumber) == 0) {
                return assetNumber;
            }
        }

        throw new IllegalStateException("资产编号生成失败，请重试");
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
