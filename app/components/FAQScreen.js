import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { Entypo } from '@expo/vector-icons';

const FAQ = () => {
  const [activeIndex, setActiveIndex] = useState(-1);

  const faqData = [
    {
      question: '계정을 삭제하려면 어떻게 해야 하나요?',
      answer: '프로필 수정에서 회원 탈퇴 옵션을 선택하고 확인 절차를 거쳐 계정을 삭제할 수 있습니다.'
    },
    {
      question: '메모를 작성하는 방법은 무엇인가요?',
      answer: '메모 작성 페이지에서 메모를 입력하고 저장 버튼을 클릭하세요.'
    },
    {
      question: '메모를 삭제하고 싶어요.',
      answer: '메모 리스트에서 삭제하고자 하는 메모를 선택한 후 삭제 버튼을 클릭하세요.'
    },
    {
      question: '여행 일기를 공유할 수 있나요?',
      answer: '네, 생성된 일기(게시물)를 공유할 수 있습니다. 공유 설정을 설정하세요.'
    },
    {
      question: '일기 공유 시 개인정보 보호는 어떻게 되나요?',
      answer: '일기 공유 시 개인정보가 담긴 정보는 직접 설정 및 삭제하셔야 합니다.'
    },
    {
      question: 'AI 일기는 생성이 무료인가요?',
      answer: '네, 기본 서비스는 무료로 제공됩니다. 프리미엄 기능은 추가 비용이 발생할 수 있습니다.'
    },
    {
      question: '서비스 이용 중 오류가 발생하면 어떻게 하나요?',
      answer: '오류가 발생하면 고객 지원팀에 문의하거나 오류 신고 기능을 사용하세요.'
    }
  ];

  const toggleAccordion = (index) => {
    setActiveIndex(activeIndex === index ? -1 : index);
  };

  return (
    <View style={styles.container}>
      {faqData.map((faq, index) => (
        <View key={index} style={styles.accordionItem}>
          <TouchableOpacity
            style={styles.accordionHeader}
            onPress={() => toggleAccordion(index)}
          >
            <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}>
              <View style={{ flexDirection: 'row', alignItems: 'center', flex: 1 }}>
                <Text style={[styles.accordionTitle, { color: 'blue', fontWeight: 'bold' }]}>
                  Q.
                </Text>
                <Text style={styles.questionText}>
                  {faq.question}
                </Text>
              </View>
              <Entypo
                name={activeIndex === index ? 'chevron-up' : 'chevron-down'}
                size={24}
                color="black"
                style={{ marginLeft: 8 }}
              />
            </View>
          </TouchableOpacity>
          {activeIndex === index && (
            <View style={styles.accordionContent}>
              <Text style={styles.accordionAnswer}>
                {faq.answer}
              </Text>
            </View>
          )}
        </View>
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    paddingVertical: 16,
    paddingHorizontal: 24,
  },
  accordionItem: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    marginVertical: 8,
  },
  accordionHeader: {
    paddingHorizontal: 24,
    paddingVertical: 16,
  },
  accordionTitle: {
    fontSize: 16,
  },
  questionText: {
    fontSize: 16,
    marginLeft: 8,
    flex: 1,
    fontWeight: 'bold',
  },
  accordionContent: {
    paddingHorizontal: 24,
    paddingVertical: 16,
  },
  accordionAnswer: {
    fontSize: 16,
  },
});

export default FAQ;
