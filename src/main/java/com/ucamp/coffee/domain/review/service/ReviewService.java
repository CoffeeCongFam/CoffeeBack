package com.ucamp.coffee.domain.review.service;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.review.dto.ReviewCreateDto;
import com.ucamp.coffee.domain.review.mapper.ReviewMapper;
import com.ucamp.coffee.domain.review.repository.ReviewRepository;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.service.StoreHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final StoreHelperService storeHelperService;
    private final ReviewRepository repository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createReviewInfo(ReviewCreateDto dto) {
        String email = "user1@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Store store = storeHelperService.findById(dto.getPartnerStoreId());

        repository.save(ReviewMapper.toEntity(dto, member, store));
    }
}
