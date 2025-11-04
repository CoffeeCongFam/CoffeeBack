package com.ucamp.coffee.domain.review.service;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.service.MemberHelperService;
import com.ucamp.coffee.domain.review.dto.ReviewCreateDTO;
import com.ucamp.coffee.domain.review.dto.ReviewResponseDTO;
import com.ucamp.coffee.domain.review.dto.ReviewUpdateDTO;
import com.ucamp.coffee.domain.review.entity.Review;
import com.ucamp.coffee.domain.review.mapper.ReviewMapper;
import com.ucamp.coffee.domain.review.repository.ReviewRepository;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.service.StoreHelperService;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.service.SubscriptionHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final StoreHelperService storeHelperService;
    private final SubscriptionHelperService subscriptionHelperService;
    private final ReviewRepository repository;
    private final MemberHelperService memberHelperService;

    @Transactional
    public void createReviewInfo(ReviewCreateDTO dto, Long memberId) {
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findById(dto.getPartnerStoreId());
        Subscription subscription = subscriptionHelperService.findById(dto.getSubscriptionId())
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다."));

        repository.save(ReviewMapper.toEntity(dto, member, store, subscription));
    }

    public List<ReviewResponseDTO> readReviewListByStore(Long partnerStoreId) {
        return repository.findByStoreWithRelations(partnerStoreId)
            .stream()
            .map(ReviewMapper::toDto)
            .toList();
    }

    public List<ReviewResponseDTO> readMyReviews(Long memberId) {
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        return repository.findByMemberOrderByCreatedAtDesc(member)
            .stream()
            .filter(review -> review.getDeletedAt() == null)
            .map(ReviewMapper::toDto)
            .toList();
    }

    @Transactional
    public void updateReviewInfo(Long reviewId, ReviewUpdateDTO dto) {
        Review review = repository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        review.update(dto);
    }

    @Transactional
    public void deleteReviewInfo(Long reviewId) {
        Review review = repository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        review.setDeletedAt(LocalDateTime.now());
    }
}
