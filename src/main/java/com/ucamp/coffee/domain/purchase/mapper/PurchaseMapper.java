package com.ucamp.coffee.domain.purchase.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ucamp.coffee.domain.purchase.dto.PurchaseAllResponseDTO;

@Mapper
public interface PurchaseMapper {

	// 소비자의 전체 주문 조회
	List<PurchaseAllResponseDTO> selectAllPurchase(Map<String, Object> param);
}
