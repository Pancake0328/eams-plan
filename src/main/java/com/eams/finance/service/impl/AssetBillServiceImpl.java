package com.eams.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.common.exception.BusinessException;
import com.eams.finance.entity.AssetBill;
import com.eams.finance.entity.AssetBillDetail;
import com.eams.finance.mapper.AssetBillDetailMapper;
import com.eams.finance.mapper.AssetBillMapper;
import com.eams.finance.service.AssetBillService;
import com.eams.finance.service.AssetDepreciationService;
import com.eams.finance.vo.BillDetailVO;
import com.eams.finance.vo.BillVO;
import com.eams.finance.vo.DepreciationInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产账单服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetBillServiceImpl implements AssetBillService {

    private final AssetBillMapper billMapper;
    private final AssetBillDetailMapper billDetailMapper;
    private final AssetDepreciationService depreciationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateMonthlyBill(Integer year, Integer month) {
        // 检查是否已存在该月账单
        LambdaQueryWrapper<AssetBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetBill::getBillType, 1);
        wrapper.eq(AssetBill::getBillYear, year);
        wrapper.eq(AssetBill::getBillMonth, month);
        if (billMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该月账单已存在");
        }

        // 生成账单编号
        String billNumber = generateBillNumber(year, month);

        // 计算目标日期（该月最后一天）
        LocalDate targetDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);

        // 计算所有资产折旧
        List<DepreciationInfo> depreciationList = depreciationService.calculateAllDepreciation(targetDate);

        // 创建账单
        AssetBill bill = new AssetBill();
        bill.setBillNumber(billNumber);
        bill.setBillType(1); // 月度账单
        bill.setBillYear(year);
        bill.setBillMonth(month);
        bill.setBillStatus(1); // 已生成
        bill.setGenerateTime(LocalDateTime.now());

        // 计算汇总数据
        BigDecimal totalPurchase = BigDecimal.ZERO;
        BigDecimal totalDepreciation = BigDecimal.ZERO;
        BigDecimal totalAssetValue = BigDecimal.ZERO;
        BigDecimal totalNetValue = BigDecimal.ZERO;

        for (DepreciationInfo info : depreciationList) {
            totalPurchase = totalPurchase.add(info.getPurchaseAmount());
            totalDepreciation = totalDepreciation.add(info.getAccumulatedDepreciation());
            totalAssetValue = totalAssetValue.add(info.getPurchaseAmount());
            totalNetValue = totalNetValue.add(info.getNetValue());
        }

        bill.setTotalPurchaseAmount(totalPurchase);
        bill.setTotalDepreciationAmount(totalDepreciation);
        bill.setTotalAssetValue(totalAssetValue);
        bill.setTotalNetValue(totalNetValue);

        billMapper.insert(bill);

        // 创建账单明细
        for (DepreciationInfo info : depreciationList) {
            AssetBillDetail detail = new AssetBillDetail();
            detail.setBillId(bill.getId());
            detail.setAssetId(info.getAssetId());
            detail.setAssetNumber(info.getAssetNumber());
            detail.setAssetName(info.getAssetName());
            detail.setPurchaseAmount(info.getPurchaseAmount());
            detail.setAccumulatedDepreciation(info.getAccumulatedDepreciation());
            detail.setCurrentDepreciation(info.getMonthlyDepreciation());
            detail.setNetValue(info.getNetValue());
            detail.setDepreciationRate(info.getDepreciationRate());
            detail.setUsefulLife(info.getUsefulLife());
            detail.setUsedMonths(info.getUsedMonths());
            billDetailMapper.insert(detail);
        }

        return bill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateAnnualBill(Integer year) {
        // 检查是否已存在该年账单
        LambdaQueryWrapper<AssetBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetBill::getBillType, 2);
        wrapper.eq(AssetBill::getBillYear, year);
        if (billMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该年账单已存在");
        }

        // 生成账单编号
        String billNumber = generateBillNumber(year, null);

        // 计算目标日期（该年最后一天）
        LocalDate targetDate = LocalDate.of(year, 12, 31);

        // 计算所有资产折旧
        List<DepreciationInfo> depreciationList = depreciationService.calculateAllDepreciation(targetDate);

        // 创建账单
        AssetBill bill = new AssetBill();
        bill.setBillNumber(billNumber);
        bill.setBillType(2); // 年度账单
        bill.setBillYear(year);
        bill.setBillStatus(1); // 已生成
        bill.setGenerateTime(LocalDateTime.now());

        // 计算汇总数据
        BigDecimal totalPurchase = BigDecimal.ZERO;
        BigDecimal totalDepreciation = BigDecimal.ZERO;
        BigDecimal totalAssetValue = BigDecimal.ZERO;
        BigDecimal totalNetValue = BigDecimal.ZERO;

        for (DepreciationInfo info : depreciationList) {
            totalPurchase = totalPurchase.add(info.getPurchaseAmount());
            totalDepreciation = totalDepreciation.add(info.getAccumulatedDepreciation());
            totalAssetValue = totalAssetValue.add(info.getPurchaseAmount());
            totalNetValue = totalNetValue.add(info.getNetValue());
        }

        bill.setTotalPurchaseAmount(totalPurchase);
        bill.setTotalDepreciationAmount(totalDepreciation);
        bill.setTotalAssetValue(totalAssetValue);
        bill.setTotalNetValue(totalNetValue);

        billMapper.insert(bill);

        // 创建账单明细（简化处理）
        for (DepreciationInfo info : depreciationList) {
            AssetBillDetail detail = new AssetBillDetail();
            detail.setBillId(bill.getId());
            detail.setAssetId(info.getAssetId());
            detail.setAssetNumber(info.getAssetNumber());
            detail.setAssetName(info.getAssetName());
            detail.setPurchaseAmount(info.getPurchaseAmount());
            detail.setAccumulatedDepreciation(info.getAccumulatedDepreciation());
            detail.setNetValue(info.getNetValue());
            detail.setDepreciationRate(info.getDepreciationRate());
            detail.setUsefulLife(info.getUsefulLife());
            detail.setUsedMonths(info.getUsedMonths());
            billDetailMapper.insert(detail);
        }

        return bill.getId();
    }

    @Override
    public BillVO getBillDetail(Long billId) {
        AssetBill bill = billMapper.selectById(billId);
        if (bill == null) {
            throw new BusinessException("账单不存在");
        }

        BillVO vo = new BillVO();
        BeanUtils.copyProperties(bill, vo);

        // 设置账单类型文本
        vo.setBillTypeText(bill.getBillType() == 1 ? "月度账单" : "年度账单");

        // 设置账单状态文本
        String statusText = switch (bill.getBillStatus()) {
            case 0 -> "草稿";
            case 1 -> "已生成";
            case 2 -> "已确认";
            default -> "未知";
        };
        vo.setBillStatusText(statusText);

        // 查询账单明细
        LambdaQueryWrapper<AssetBillDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetBillDetail::getBillId, billId);
        List<AssetBillDetail> details = billDetailMapper.selectList(wrapper);

        List<BillDetailVO> detailVOs = new ArrayList<>();
        for (AssetBillDetail detail : details) {
            BillDetailVO detailVO = new BillDetailVO();
            BeanUtils.copyProperties(detail, detailVO);
            detailVOs.add(detailVO);
        }
        vo.setDetails(detailVOs);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmBill(Long billId) {
        AssetBill bill = billMapper.selectById(billId);
        if (bill == null) {
            throw new BusinessException("账单不存在");
        }

        bill.setBillStatus(2); // 已确认
        bill.setConfirmTime(LocalDateTime.now());
        billMapper.updateById(bill);
    }

    @Override
    public Page<BillVO> getBillPage(Integer current, Integer size, Integer billType, Integer year, Integer month) {
        Page<AssetBill> page = new Page<>(current, size);

        LambdaQueryWrapper<AssetBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(billType != null, AssetBill::getBillType, billType);
        wrapper.eq(year != null, AssetBill::getBillYear, year);
        wrapper.eq(month != null, AssetBill::getBillMonth, month);
        wrapper.orderByDesc(AssetBill::getBillYear, AssetBill::getBillMonth);

        Page<AssetBill> billPage = billMapper.selectPage(page, wrapper);

        Page<BillVO> voPage = new Page<>();
        BeanUtils.copyProperties(billPage, voPage, "records");

        List<BillVO> voList = new ArrayList<>();
        for (AssetBill bill : billPage.getRecords()) {
            BillVO vo = new BillVO();
            BeanUtils.copyProperties(bill, vo);
            vo.setBillTypeText(bill.getBillType() == 1 ? "月度账单" : "年度账单");
            String statusText = switch (bill.getBillStatus()) {
                case 0 -> "草稿";
                case 1 -> "已生成";
                case 2 -> "已确认";
                default -> "未知";
            };
            vo.setBillStatusText(statusText);
            voList.add(vo);
        }
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 生成账单编号
     */
    private String generateBillNumber(Integer year, Integer month) {
        if (month != null) {
            // 月度账单：BILL-YYYYMM-001
            return String.format("BILL-%04d%02d-%03d",
                    year, month, System.currentTimeMillis() % 1000);
        } else {
            // 年度账单：BILL-YYYY-001
            return String.format("BILL-%04d-%03d",
                    year, System.currentTimeMillis() % 1000);
        }
    }
}
