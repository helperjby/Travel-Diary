package com.traveldiary.be.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traveldiary.be.dto.FaqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.traveldiary.be.entity.Faq;
import com.traveldiary.be.repository.FaqRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FaqService {

    @Autowired
    private FaqRepository faqRepository;

    private List<FaqDTO> faqs;

    // 애플리케이션이 시작될 때 FAQ 데이터를 로드하고 db에 저장
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            faqs = mapper.readValue(new ClassPathResource("faqs.json").getFile(), new TypeReference<List<FaqDTO>>() {});
            faqRepository.saveAll(faqs.stream().map(this::convertToEntity).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 모든 FAQ를 조회
    public List<FaqDTO> getAllFAQs() {
        return faqRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Faq 엔티티를 FaqDTO로 변환
    private FaqDTO convertToDTO(Faq faq) {
        FaqDTO faqDTO = new FaqDTO();
        faqDTO.setId(faq.getId());
        faqDTO.setQuestion(faq.getQuestion());
        faqDTO.setAnswer(faq.getAnswer());
        return faqDTO;
    }

    // FaqDTO를 Faq 엔티티로 변환
    private Faq convertToEntity(FaqDTO faqDTO) {
        Faq faq = new Faq();
        faq.setId(faqDTO.getId());
        faq.setQuestion(faqDTO.getQuestion());
        faq.setAnswer(faqDTO.getAnswer());
        return faq;
    }
}
