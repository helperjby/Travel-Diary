import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, SafeAreaView, StatusBar, Platform } from 'react-native';
import Header1 from './Header1'; // 헤더 컴포넌트 추가

const WriteOptionScreen = ({ navigation }) => {
  const [selectedOption, setSelectedOption] = useState(null);

  const handleOptionSelect = (option) => {
    setSelectedOption(option);
  };

  const handleSelectButtonPress = () => {
    if (selectedOption) {
      navigation.navigate(selectedOption);
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="dark-content" />
      <View style={styles.container}>
        <Header1 title="글쓰기" />
        <View style={styles.optionsContainer}>
          <TouchableOpacity
            style={[styles.option, selectedOption === 'AIWrite' && styles.selectedOption]}
            onPress={() => handleOptionSelect('AIWrite')}
          >
            <Text style={styles.optionTitle}>AI로 작성하기</Text>
            <Text style={styles.optionDescription}>몇 가지 질문들을 통해 AI가 글을 더 풍성하게 만들어줍니다.</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.option, selectedOption === 'ManualWrite' && styles.selectedOption]}
            onPress={() => handleOptionSelect('ManualWrite')}
          >
            <Text style={styles.optionTitle}>직접 작성하기</Text>
            <Text style={styles.optionDescription}>다이어리를 자유롭게 작성해보세요.</Text>
          </TouchableOpacity>
        </View>
        <TouchableOpacity style={styles.selectButton} onPress={handleSelectButtonPress}>
          <Text style={styles.selectButtonText}>선택</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#fff', // 헤더와 동일한 색상으로 설정
    paddingTop: Platform.OS === 'android' ? StatusBar.currentHeight : 0,
  },
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  optionsContainer: {
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: 20,
  },
  option: {
    padding: 20,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    marginBottom: 20,
  },
  selectedOption: {
    borderColor: '#4285F4',
    borderWidth: 2,
  },
  optionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  optionDescription: {
    fontSize: 14,
    color: 'gray',
  },
  selectButton: {
    backgroundColor: '#4285F4',
    padding: 15,
    alignItems: 'center',
    justifyContent: 'center',
    margin: 20,
    borderRadius: 5,
  },
  selectButtonText: {
    color: '#fff',
    fontSize: 16,
  },
});

export default WriteOptionScreen;