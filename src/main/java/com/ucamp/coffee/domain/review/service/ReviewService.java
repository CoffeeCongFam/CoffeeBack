package com.ucamp.coffee.domain.review.service;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.review.dto.ReviewCreateDto;
import com.ucamp.coffee.domain.review.dto.ReviewResponseDto;
import com.ucamp.coffee.domain.review.dto.ReviewUpdateDto;
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
    private final MemberRepository memberRepository;

    @Transactional
    public void createReviewInfo(ReviewCreateDto dto) {
        String email = "user1@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findById(dto.getPartnerStoreId());
        Subscription subscription = subscriptionHelperService.findById(dto.getSubscriptionId())
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다."));

        repository.save(ReviewMapper.toEntity(dto, member, store, subscription));
    }

    public List<ReviewResponseDto> readReviewListByStore(Long partnerStoreId) {
        return repository.findByStoreWithRelations(partnerStoreId)
            .stream()
            .map(ReviewMapper::toDto)
            .toList();
    }

    public List<ReviewResponseDto> readMyReviews() {
        String email = "user1@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        return repository.findByMember(member)
            .stream()
            .filter(review -> review.getDeletedAt() == null)
            .map(ReviewMapper::toDto)
            .toList();
    }

    @Transactional
    public void updateReviewInfo(Long reviewId, ReviewUpdateDto dto) {
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
