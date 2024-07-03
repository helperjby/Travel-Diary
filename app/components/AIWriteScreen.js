import React, { useState } from 'react';
import { View, Text, TextInput, Button, StyleSheet, FlatList, KeyboardAvoidingView, Platform, TouchableOpacity } from 'react-native';

const questions = [
  '오늘 방문한 장소 중 가장 인상 깊었던 곳은 어디였나요? 그 이유는 무엇인가요?',
  '오늘 여행 중 가장 즐거웠던 순간은 무엇이었나요?',
  '오늘 만난 사람 중 기억에 남는 사람이 있나요? 그 사람과 어떤 대화를 나누었나요?',
  '오늘 먹은 음식 중 가장 맛있었던 것은 무엇이었나요? 그 음식에 대해 어떻게 느꼈나요?',
  '오늘 여행하면서 어려웠던 점이나 예상치 못한 일이 있었나요? 어떻게 해결했나요?',
  '내일의 여행 계획은 무엇인가요? 기대되는 점은 무엇인가요?',
];

const AIWriteScreen = () => {
  const [currentStep, setCurrentStep] = useState(0);
  const [answers, setAnswers] = useState([]);
  const [input, setInput] = useState('');

  const handleNext = () => {
    if (input.trim()) {
      setAnswers([...answers, input]);
      setInput('');
      setCurrentStep(currentStep + 1);
    }
  };

  const handleSubmit = async () => {
    try {
      const response = await fetch('https://your-server-url.com/api/save-answers', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ answers }),
      });

      if (response.ok) {
        alert('답변이 성공적으로 전송되었습니다.');
      } else {
        alert('답변 전송에 실패했습니다.');
      }
    } catch (error) {
      alert('답변 전송에 실패했습니다.');
    }
  };

  const isAllMandatoryQuestionsAnswered = currentStep >= questions.length;

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      keyboardVerticalOffset={Platform.OS === 'ios' ? 100 : 0}
    >
      <FlatList
        ListHeaderComponent={
          isAllMandatoryQuestionsAnswered && (
            <View style={styles.headerContainer}>
              <Text style={styles.headerText}>필수 질문이 완료되었습니다.</Text>
              <TouchableOpacity style={styles.submitButton} onPress={handleSubmit}>
                <Text style={styles.submitButtonText}>글 작성하기</Text>
              </TouchableOpacity>
            </View>
          )
        }
        data={answers.map((answer, index) => ({
          key: `${index}`,
          question: questions[index],
          answer: answer,
        }))}
        renderItem={({ item }) => (
          <View style={styles.chatItem}>
            <View style={styles.questionBubble}>
              <Text style={styles.questionText}>{item.question}</Text>
            </View>
            <View style={styles.answerBubble}>
              <Text style={styles.answerText}>{item.answer}</Text>
            </View>
          </View>
        )}
        keyExtractor={(item) => item.key}
        style={styles.chatList}
        contentContainerStyle={styles.chatListContent}
      />
      {currentStep < questions.length && (
        <View style={styles.inputContainer}>
          <View style={styles.questionBubble}>
            <Text style={styles.questionText}>{questions[currentStep]}</Text>
          </View>
          <TextInput
            style={styles.input}
            value={input}
            onChangeText={setInput}
            placeholder="사용자가 입력하기"
          />
          <Button title="작성" onPress={handleNext} />
        </View>
      )}
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  chatList: {
    flex: 1,
    padding: 20,
  },
  chatListContent: {
    paddingBottom: 100, // 아래에 공간 추가
  },
  chatItem: {
    marginVertical: 10, // 채팅 아이템 간의 간격 추가
  },
  questionBubble: {
    backgroundColor: '#d3d3d3', // 회색
    padding: 10,
    borderRadius: 10,
    alignSelf: 'flex-start',
    marginBottom: 10,
    marginLeft: 10, // 왼쪽 여백 추가
    marginRight: 50, // 오른쪽 여백 추가
  },
  answerBubble: {
    backgroundColor: '#007AFF', // 파란색
    padding: 10,
    borderRadius: 10,
    alignSelf: 'flex-end',
    marginBottom: 10,
    marginLeft: 50, // 왼쪽 여백 추가
    marginRight: 10, // 오른쪽 여백 추가
  },
  questionText: {
    color: '#000', // 검은색
    fontSize: 16,
  },
  answerText: {
    color: '#fff', // 흰색
    fontSize: 16,
  },
  inputContainer: {
    padding: 20,
    borderTopWidth: 1,
    borderColor: '#ccc',
    backgroundColor: '#fff',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 5,
    padding: 10,
    marginBottom: 10,
  },
  headerContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 10,
    backgroundColor: '#f8f8f8',
  },
  headerText: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  submitButton: {
    paddingVertical: 5,
    paddingHorizontal: 10,
    backgroundColor: '#007AFF',
    borderRadius: 5,
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
  },
});

export default AIWriteScreen;