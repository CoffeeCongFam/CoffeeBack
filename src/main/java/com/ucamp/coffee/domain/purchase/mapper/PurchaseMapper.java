package com.ucamp.coffee.domain.purchase.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ucamp.coffee.domain.purchase.dto.PurchaseAllGiftDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseAllResponseDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseReceiveGiftDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseSendGiftDTO;

@Mapper
public interface PurchaseMapper {

	// 소비자의 전체 주문 조회
	List<PurchaseAllResponseDTO> selectAllPurchase(Map<String, Object> param);

	//소비자의 상세 주문 조회
	PurchaseAllResponseDTO selectPurchase(Long purchaseId);
	
	// 소비자의 모든 선물 목록 조회
	List<PurchaseAllGiftDTO> selectAllGift(Long memberId);

	// 소비자의 모든 보낸 선물 조회
	List<PurchaseSendGiftDTO> selectAllSendGift(Long memberId);

	// 소비자의 모든 받은 선물 조회
	List<PurchaseReceiveGiftDTO> selectAllReceivedGift(Long memberId);

	// 소비자의 구독권 사용 내역 조회(받은 선물 조회랑 매칭용)
	List<PurchaseReceiveGiftDTO.UsageHistoryDTO> selectUsageReceiveHistoryBySubscriptionId(Long memberSubscriptionId);
	List<PurchaseSendGiftDTO.UsageHistoryDTO> selectUsageSendHistoryBySubscriptionId(Long memberSubscriptionId);

	//소비자의 보낸 선물 상세 조회(선물함에서 조회)
	PurchaseSendGiftDTO selectSendGiftDetail(Long purchaseId);
	
	//소비자의 받은 선물 상세 조회
	PurchaseReceiveGiftDTO selectReceivedGiftDetail(Long memberSubscriptionId);
	
	// 소비자의 보낸 선물 상세 조회(선물 보낸 직후)
	PurchaseSendGiftDTO selectDetailSendGift(Long purchaseId);

}
