package com.megastudy.surlkdh.domain.member.infrastructure.mybatis;

import com.megastudy.surlkdh.domain.member.entity.Member;
import com.megastudy.surlkdh.domain.member.infrastructure.mybatis.dto.MemberEntity;
import com.megastudy.surlkdh.domain.member.infrastructure.mybatis.mapper.MemberMapper;
import com.megastudy.surlkdh.domain.member.service.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberMapper memberMapper;

    @Override
    public void save(Member member) {
        MemberEntity entity = MemberEntity.from(member);
        memberMapper.insert(entity);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.ofNullable(memberMapper.findByEmail(email))
                .map(MemberEntity::toModel);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(memberMapper.findById(id))
                .map(MemberEntity::toModel);
    }

    @Override
    public Page<Member> findAll(Pageable pageable) {
        Long count = memberMapper.countAll();
        List<MemberEntity> entityList = memberMapper.findAll(pageable);
        List<Member> domainList = entityList.stream()
                .map(MemberEntity::toModel)
                .toList();
        return new PageImpl<>(domainList, pageable, count);
    }

    @Override
    public Optional<Member> update(Member member) {
        MemberEntity entity = MemberEntity.from(member);
        memberMapper.update(entity);

        return findById(entity.getMemberId());
    }
}